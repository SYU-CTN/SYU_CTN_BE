package com.example.syu_ctn_be.rag;

import com.example.syu_ctn_be.service.OpenAiClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "rag.provider", havingValue = "chroma")
public class ChromaKnowledgeIngestionService implements KnowledgeIngestionService {

    private static final String DOC_TYPE_KNOWLEDGE = "knowledge";
    private static final String DOC_TYPE_MARKER = "knowledge_marker";

    private final KnowledgeFileLoader knowledgeFileLoader;
    private final OpenAiClient openAiClient;
    private final ChromaClient chromaClient;

    public ChromaKnowledgeIngestionService(
            KnowledgeFileLoader knowledgeFileLoader,
            OpenAiClient openAiClient,
            ChromaClient chromaClient) {
        this.knowledgeFileLoader = knowledgeFileLoader;
        this.openAiClient = openAiClient;
        this.chromaClient = chromaClient;
    }

    @Override
    public void ingest() {
        try {
            List<KnowledgeChunk> chunks = knowledgeFileLoader.loadChunks();
            Map<String, List<KnowledgeChunk>> chunksBySource = chunks.stream()
                    .collect(Collectors.groupingBy(KnowledgeChunk::source));

            for (Map.Entry<String, List<KnowledgeChunk>> entry : chunksBySource.entrySet()) {
                ingestSource(entry.getKey(), entry.getValue());
            }
        } catch (IOException ex) {
            throw new IllegalStateException("지식 파일 로딩에 실패했습니다.", ex);
        }
    }

    private void ingestSource(String source, List<KnowledgeChunk> chunks) {
        if (chunks.isEmpty()) {
            return;
        }

        String contentHash = chunks.get(0).contentHash();
        String markerId = markerId(source, contentHash);
        if (chromaClient.existsById(markerId)) {
            return;
        }

        chromaClient.deleteBySource(source);

        List<String> ids = new ArrayList<>();
        List<float[]> embeddings = new ArrayList<>();
        List<Map<String, Object>> metadatas = new ArrayList<>();
        List<String> documents = new ArrayList<>();

        for (KnowledgeChunk chunk : chunks) {
            ids.add(chunk.id());
            embeddings.add(openAiClient.embed(chunk.text()));
            metadatas.add(chunk.metadata());
            documents.add(chunk.text());
        }

        String markerText = markerText(source, contentHash);
        ids.add(markerId);
        embeddings.add(openAiClient.embed(markerText));
        metadatas.add(Map.of(
                "source", source,
                "contentHash", contentHash,
                "chunkIndex", -1,
                "docType", DOC_TYPE_MARKER));
        documents.add(markerText);

        chromaClient.upsert(ids, embeddings, metadatas, documents);
    }

    private String markerId(String source, String contentHash) {
        return "marker:" + source + ":" + contentHash;
    }

    private String markerText(String source, String contentHash) {
        return "INDEX_MARKER " + DOC_TYPE_KNOWLEDGE + " " + source + " " + contentHash;
    }
}
