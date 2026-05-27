package com.example.syu_ctn_be.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "rag.ingestion.enabled", havingValue = "true", matchIfMissing = true)
public class VectorDbInitializer implements CommandLineRunner {

    private final KnowledgeIngestionService knowledgeIngestionService;

    @Override
    public void run(String... args) {
        knowledgeIngestionService.ingest();
    }
}
