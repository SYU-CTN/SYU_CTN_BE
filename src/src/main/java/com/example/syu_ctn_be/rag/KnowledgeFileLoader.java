package com.example.syu_ctn_be.rag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeFileLoader {

    private static final Pattern TOKEN_SPLIT_PATTERN = Pattern.compile("[^0-9A-Za-z가-힣]+");

    private final RagProperties ragProperties;

    public KnowledgeFileLoader(RagProperties ragProperties) {
        this.ragProperties = ragProperties;
    }

    public List<KnowledgeChunk> loadChunks() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(ragProperties.getKnowledgeLocation());

        List<KnowledgeChunk> chunks = new ArrayList<>();
        for (Resource resource : resources) {
            String source = resource.getFilename() == null ? "knowledge" : resource.getFilename();
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String contentHash = sha256(content);
            List<String> splitTexts = split(content);

            for (int i = 0; i < splitTexts.size(); i++) {
                String text = normalizeWhitespace(splitTexts.get(i));
                if (text.isBlank()) {
                    continue;
                }
                Map<String, Object> metadata = new LinkedHashMap<>();
                metadata.put("source", source);
                metadata.put("contentHash", contentHash);
                metadata.put("chunkIndex", i);
                metadata.put("docType", "knowledge");

                chunks.add(new KnowledgeChunk(
                        source + ":" + contentHash + ":" + i,
                        source,
                        contentHash,
                        i,
                        text,
                        tokenize(text),
                        metadata));
            }
        }

        return chunks;
    }

    private List<String> split(String content) {
        String normalized = normalizeWhitespace(content);
        if (normalized.isBlank()) {
            return List.of();
        }

        int maxLength = Math.max(200, ragProperties.getMaxChunkLength());
        int overlap = Math.max(0, Math.min(ragProperties.getChunkOverlap(), maxLength / 2));
        List<String> chunks = new ArrayList<>();

        int start = 0;
        while (start < normalized.length()) {
            int end = Math.min(start + maxLength, normalized.length());
            int adjustedEnd = adjustEnd(normalized, start, end);
            chunks.add(normalized.substring(start, adjustedEnd).trim());

            if (adjustedEnd >= normalized.length()) {
                break;
            }
            start = Math.max(0, adjustedEnd - overlap);
        }

        return chunks;
    }

    private int adjustEnd(String text, int start, int end) {
        if (end >= text.length()) {
            return text.length();
        }

        int sentenceEnd = Math.max(
                text.lastIndexOf(". ", end),
                Math.max(text.lastIndexOf("? ", end), text.lastIndexOf("! ", end)));
        if (sentenceEnd > start + 100) {
            return sentenceEnd + 1;
        }

        int space = text.lastIndexOf(' ', end);
        if (space > start + 100) {
            return space;
        }

        return end;
    }

    private Set<String> tokenize(String text) {
        Set<String> tokens = new LinkedHashSet<>();
        for (String token : TOKEN_SPLIT_PATTERN.split(text.toLowerCase(Locale.ROOT))) {
            if (!token.isBlank()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private String normalizeWhitespace(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("\\s+", " ").trim();
    }

    private String sha256(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", ex);
        }
    }
}
