package com.example.syu_ctn_be.rag;

import com.example.syu_ctn_be.service.OpenAiClient;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "rag.provider", havingValue = "chroma")
public class ChromaKnowledgeRetriever implements KnowledgeRetriever {

    private final OpenAiClient openAiClient;
    private final ChromaClient chromaClient;
    private final RagProperties ragProperties;

    public ChromaKnowledgeRetriever(OpenAiClient openAiClient, ChromaClient chromaClient, RagProperties ragProperties) {
        this.openAiClient = openAiClient;
        this.chromaClient = chromaClient;
        this.ragProperties = ragProperties;
    }

    @Override
    public String retrieve(String query) {
        if (query == null || query.isBlank()) {
            return "";
        }

        float[] queryEmbedding = openAiClient.embed(query);
        List<ChromaDocument> documents = chromaClient.query(
                queryEmbedding,
                ragProperties.getTopK(),
                Map.of("docType", "knowledge"));

        return documents.stream()
                .filter(document -> document.similarity() >= ragProperties.getSimilarityThreshold())
                .map(this::format)
                .reduce((left, right) -> left + "\n" + right)
                .orElse("");
    }

    private String format(ChromaDocument document) {
        Object source = document.metadata().getOrDefault("source", "chroma");
        Object chunkIndex = document.metadata().getOrDefault("chunkIndex", "?");
        return "- [" + source + "#" + chunkIndex + "] " + document.document();
    }
}
