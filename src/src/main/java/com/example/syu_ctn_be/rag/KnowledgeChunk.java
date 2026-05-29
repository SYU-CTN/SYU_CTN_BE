package com.example.syu_ctn_be.rag;

import java.util.Map;
import java.util.Set;

public record KnowledgeChunk(
        String id,
        String source,
        String contentHash,
        int chunkIndex,
        String text,
        Set<String> tokens,
        Map<String, Object> metadata
) {
}
