package com.example.syu_ctn_be.repository;

import com.example.syu_ctn_be.rag.KnowledgeRetriever;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RagDocumentRepository implements DocumentRepository {

    private final KnowledgeRetriever knowledgeRetriever;

    @Override
    public String findRelevantContext(String query) {
        return knowledgeRetriever.retrieve(query);
    }
}
