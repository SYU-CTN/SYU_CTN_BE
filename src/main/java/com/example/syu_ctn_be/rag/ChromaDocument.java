package com.example.syu_ctn_be.rag;

import java.util.Map;

public record ChromaDocument(
        String id,
        String document,
        Map<String, Object> metadata,
        double distance
) {
    public double similarity() {
        return 1.0 - distance;
    }
}
