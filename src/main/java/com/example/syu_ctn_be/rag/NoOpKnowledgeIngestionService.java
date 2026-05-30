package com.example.syu_ctn_be.rag;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "rag.provider", havingValue = "memory", matchIfMissing = true)
public class NoOpKnowledgeIngestionService implements KnowledgeIngestionService {

    @Override
    public void ingest() {
        // Memory RAG loads knowledge files in MemoryKnowledgeRetriever.
    }
}
