package com.example.syu_ctn_be.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OpenAiEmbeddingRequest {
    private final String model;
    private final String input;
}
