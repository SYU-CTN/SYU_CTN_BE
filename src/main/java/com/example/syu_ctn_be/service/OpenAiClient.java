package com.example.syu_ctn_be.service;

import com.example.syu_ctn_be.config.OpenAiProperties;
import com.example.syu_ctn_be.dto.openai.OpenAiChatRequest;
import com.example.syu_ctn_be.dto.openai.OpenAiChatResponse;
import com.example.syu_ctn_be.dto.openai.OpenAiEmbeddingRequest;
import com.example.syu_ctn_be.dto.openai.OpenAiEmbeddingResponse;
import com.example.syu_ctn_be.dto.openai.OpenAiMessage;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
public class OpenAiClient {

    private static final String EMBEDDINGS_URL = "https://api.openai.com/v1/embeddings";
    private static final int MAX_ATTEMPTS = 3;
    private static final long INITIAL_BACKOFF_MS = 500L;
    private static final long MAX_BACKOFF_MS = 2_000L;
    private static final Pattern ERROR_MESSAGE_PATTERN =
            Pattern.compile("\"message\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"");

    private final RestTemplate restTemplate;
    private final OpenAiProperties properties;

    public OpenAiClient(@Qualifier("openAiRestTemplate") RestTemplate restTemplate,
                        OpenAiProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public String complete(List<OpenAiMessage> messages) {
        validateApiKey();

        OpenAiChatRequest body = new OpenAiChatRequest(properties.getModel(), messages);
        HttpEntity<OpenAiChatRequest> request = new HttpEntity<>(body, authHeaders());

        OpenAiChatResponse response = executeWithRetry(
                "chat completion",
                () -> restTemplate.exchange(
                        properties.getApi().getUrl(),
                        HttpMethod.POST,
                        request,
                        OpenAiChatResponse.class));

        return extractAnswer(response);
    }

    public float[] embed(String input) {
        validateApiKey();
        if (input == null || input.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Embedding input is empty.");
        }

        OpenAiEmbeddingRequest body = new OpenAiEmbeddingRequest(properties.getEmbeddingModel(), input);
        HttpEntity<OpenAiEmbeddingRequest> request = new HttpEntity<>(body, authHeaders());

        OpenAiEmbeddingResponse response = executeWithRetry(
                "embedding",
                () -> restTemplate.exchange(
                        EMBEDDINGS_URL,
                        HttpMethod.POST,
                        request,
                        OpenAiEmbeddingResponse.class));

        return extractEmbedding(response);
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getApi().getKey());
        return headers;
    }

    private <T> T executeWithRetry(String operation, OpenAiCall<T> call) {
        long backoffMs = INITIAL_BACKOFF_MS;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                ResponseEntity<T> response = call.execute();
                return response.getBody();
            } catch (HttpStatusCodeException ex) {
                if (!shouldRetry(ex.getStatusCode()) || attempt == MAX_ATTEMPTS) {
                    throw toResponseStatusException(operation, ex);
                }
                log.warn("OpenAI {} failed with status {}. Retrying {}/{}.",
                        operation, ex.getStatusCode(), attempt + 1, MAX_ATTEMPTS);
                sleep(backoffMs);
                backoffMs = Math.min(backoffMs * 2, MAX_BACKOFF_MS);
            } catch (ResourceAccessException ex) {
                if (attempt == MAX_ATTEMPTS) {
                    log.error("OpenAI {} timed out or was unreachable: {}", operation, ex.getMessage());
                    throw new ResponseStatusException(
                            HttpStatus.GATEWAY_TIMEOUT,
                            "AI 서버 응답 시간이 초과되었습니다. 잠시 후 다시 시도해주세요.");
                }
                log.warn("OpenAI {} timed out or was unreachable. Retrying {}/{}.",
                        operation, attempt + 1, MAX_ATTEMPTS);
                sleep(backoffMs);
                backoffMs = Math.min(backoffMs * 2, MAX_BACKOFF_MS);
            } catch (RestClientException ex) {
                log.error("OpenAI {} call failed: {}", operation, ex.getMessage());
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "AI 응답 생성에 실패했습니다. 잠시 후 다시 시도해주세요.");
            }
        }

        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI 응답 생성에 실패했습니다.");
    }

    private boolean shouldRetry(HttpStatusCode status) {
        return status.value() == 429 || status.is5xxServerError();
    }

    private ResponseStatusException toResponseStatusException(String operation, HttpStatusCodeException ex) {
        HttpStatusCode openAiStatus = ex.getStatusCode();
        String detail = extractOpenAiErrorMessage(ex.getResponseBodyAsString());
        String suffix = detail == null || detail.isBlank() ? "" : " 상세: " + detail;

        log.error("OpenAI {} failed with status {}.{}", operation, openAiStatus, suffix);

        if (openAiStatus.value() == 401 || openAiStatus.value() == 403) {
            return new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "OpenAI 인증 설정을 확인해야 합니다." + suffix);
        }
        if (openAiStatus.value() == 429) {
            return new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "AI 요청 한도를 초과했습니다. 잠시 후 다시 시도해주세요." + suffix);
        }
        if (openAiStatus.is4xxClientError()) {
            return new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "AI 요청이 거절되었습니다." + suffix);
        }
        return new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "AI 서버 오류로 응답 생성에 실패했습니다." + suffix);
    }

    private void sleep(long backoffMs) {
        try {
            Thread.sleep(backoffMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "AI 요청 재시도 중 인터럽트가 발생했습니다.");
        }
    }

    private String extractOpenAiErrorMessage(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }

        Matcher matcher = ERROR_MESSAGE_PATTERN.matcher(body);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1)
                .replace("\\\"", "\"")
                .replace("\\n", " ")
                .replace("\\r", " ")
                .trim();
    }

    private void validateApiKey() {
        String key = properties.getApi() == null ? null : properties.getApi().getKey();
        if (key == null || key.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "OpenAI API Key가 설정되지 않았습니다.");
        }
    }

    private float[] extractEmbedding(OpenAiEmbeddingResponse body) {
        if (body == null || body.getData() == null || body.getData().isEmpty()
                || body.getData().get(0).getEmbedding() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI embedding 응답이 비어 있습니다.");
        }

        List<Double> values = body.getData().get(0).getEmbedding();
        float[] embedding = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            embedding[i] = values.get(i).floatValue();
        }
        return embedding;
    }

    private String extractAnswer(OpenAiChatResponse body) {
        if (body == null || body.getChoices() == null || body.getChoices().isEmpty()
                || body.getChoices().get(0).getMessage() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI 응답이 비어 있습니다.");
        }
        String content = body.getChoices().get(0).getMessage().getContent();
        return content == null ? "" : content.trim();
    }

    @FunctionalInterface
    private interface OpenAiCall<T> {
        ResponseEntity<T> execute();
    }
}
