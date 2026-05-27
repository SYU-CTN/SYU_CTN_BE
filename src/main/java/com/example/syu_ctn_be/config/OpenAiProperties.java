package com.example.syu_ctn_be.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

    private Api api = new Api();
    private String model = "gpt-4o-mini";
    private String embeddingModel = "text-embedding-3-small";
    private int timeoutMs = 15000;
    private String systemPersona =
            "당신은 삼육대학교 학사 안내 AI 도우미입니다. 한국어로 친절하고 정확하게 답변하세요.";

    @Getter
    @Setter
    public static class Api {
        private String key;
        private String url = "https://api.openai.com/v1/chat/completions";
    }
}
