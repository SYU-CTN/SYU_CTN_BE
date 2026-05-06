package com.example.syu_ctn_be.chat.service;

import com.example.syu_ctn_be.chat.config.OpenAiProperties;
import com.example.syu_ctn_be.chat.dto.openai.OpenAiChatRequest;
import com.example.syu_ctn_be.chat.dto.openai.OpenAiChatResponse;
import com.example.syu_ctn_be.chat.dto.openai.OpenAiMessage;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 * OpenAI Chat Completions 호출 전담 컴포넌트.
 * API Key 는 OpenAiProperties 에서만 읽고, 절대 인자/로그/예외 메시지로 노출하지 않는다.
 */
@Slf4j
@Component
public class OpenAiClient {

    private final RestTemplate restTemplate;
    private final OpenAiProperties properties;

    public OpenAiClient(@Qualifier("openAiRestTemplate") RestTemplate restTemplate,
                        OpenAiProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    /**
     * 시스템/유저 메시지 묶음을 그대로 OpenAI 에 전달하고 답변 텍스트를 반환한다.
     * 호출자는 RAG 단계에서 system 메시지에 컨텍스트를 미리 합쳐 두었다고 가정한다.
     */
    public String complete(List<OpenAiMessage> messages) {
        validateApiKey();

        OpenAiChatRequest body = new OpenAiChatRequest(properties.getModel(), messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Bearer 토큰은 헤더에만 실리고 본문/로그에는 절대 남지 않는다.
        headers.setBearerAuth(properties.getApi().getKey());

        HttpEntity<OpenAiChatRequest> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<OpenAiChatResponse> response = restTemplate.exchange(
                    properties.getApi().getUrl(),
                    HttpMethod.POST,
                    request,
                    OpenAiChatResponse.class);

            return extractAnswer(response.getBody());
        } catch (RestClientException ex) {
            log.error("OpenAI 호출 실패: {}", ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI 응답 생성에 실패했습니다.");
        }
    }

    private void validateApiKey() {
        String key = properties.getApi() == null ? null : properties.getApi().getKey();
        if (key == null || key.isBlank()) {
            // 시작 시 즉시 알아채도록 5xx 로 응답.
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "OpenAI API Key 가 설정되지 않았습니다.");
        }
    }

    private String extractAnswer(OpenAiChatResponse body) {
        if (body == null || body.getChoices() == null || body.getChoices().isEmpty()
                || body.getChoices().get(0).getMessage() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI 응답이 비어 있습니다.");
        }
        String content = body.getChoices().get(0).getMessage().getContent();
        return content == null ? "" : content.trim();
    }
}
