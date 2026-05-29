package com.example.syu_ctn_be.rag;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "rag.provider", havingValue = "memory", matchIfMissing = true)
public class MemoryKnowledgeRetriever implements KnowledgeRetriever {

    private static final String TOKEN_SPLIT_REGEX = "[^0-9A-Za-z가-힣]+";

    private final KnowledgeFileLoader knowledgeFileLoader;
    private final RagProperties ragProperties;
    private final List<KnowledgeChunk> chunks = new ArrayList<>();

    public MemoryKnowledgeRetriever(KnowledgeFileLoader knowledgeFileLoader, RagProperties ragProperties) {
        this.knowledgeFileLoader = knowledgeFileLoader;
        this.ragProperties = ragProperties;
    }

    @PostConstruct
    void loadKnowledge() throws IOException {
        chunks.clear();
        chunks.addAll(knowledgeFileLoader.loadChunks());
        loadFallbackKnowledge();
    }

    @Override
    public String retrieve(String query) {
        if (query == null || query.isBlank()) {
            return "";
        }

        Set<String> queryTokens = new LinkedHashSet<>(
                Arrays.asList(query.toLowerCase(Locale.ROOT).split(TOKEN_SPLIT_REGEX)));

        return chunks.stream()
                .map(chunk -> new ScoredChunk(chunk, score(chunk, query, queryTokens)))
                .filter(scored -> scored.score() > 0)
                .sorted((left, right) -> Integer.compare(right.score(), left.score()))
                .limit(ragProperties.getTopK())
                .map(scored -> format(scored.chunk()))
                .reduce((left, right) -> left + "\n" + right)
                .orElse("");
    }

    private int score(KnowledgeChunk chunk, String query, Set<String> queryTokens) {
        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        String normalizedText = chunk.text().toLowerCase(Locale.ROOT);
        int score = normalizedText.contains(normalizedQuery) ? 10 : 0;

        for (String token : queryTokens) {
            if (token == null || token.length() < 2) {
                continue;
            }
            if (chunk.tokens().contains(token)) {
                score += token.length() >= 4 ? 3 : 1;
            } else if (normalizedText.contains(token)) {
                score += 1;
            }
        }
        return score;
    }

    private String format(KnowledgeChunk chunk) {
        return "- [" + chunk.source() + "#" + chunk.chunkIndex() + "] " + chunk.text();
    }

    private void loadFallbackKnowledge() {
        chunks.add(fallback("fallback:graduation",
                "컴퓨터공학과 학생은 총 130학점 이상을 이수해야 졸업할 수 있습니다. "
                        + "전공 필수와 전공 선택 학점 요건은 학번별 교육과정표를 확인해야 합니다."));
        chunks.add(fallback("fallback:course-registration",
                "수강신청은 학기 시작 전 정해진 기간에 학사 시스템에서 진행합니다. "
                        + "정원 초과 과목은 대기 또는 증원 공지를 확인해야 합니다."));
        chunks.add(fallback("fallback:scholarship",
                "장학금은 직전 학기 이수 학점, 평점 평균, 학년별 성적 등의 기준으로 선발될 수 있습니다. "
                        + "세부 기준은 학기별 장학 공지를 확인해야 합니다."));
    }

    private KnowledgeChunk fallback(String id, String text) {
        Set<String> tokens = new LinkedHashSet<>(Arrays.asList(text.toLowerCase(Locale.ROOT).split(TOKEN_SPLIT_REGEX)));
        return new KnowledgeChunk(id, "fallback", "fallback", 0, text, tokens, Map.of("source", "fallback"));
    }

    private record ScoredChunk(KnowledgeChunk chunk, int score) {
    }
}
