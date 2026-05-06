package com.example.syu_ctn_be.chat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.yml 의 openai.* 프로퍼티를 타입 안전하게 매핑한다.
 * API Key 는 절대 코드에 하드코딩하지 않고 환경 변수에서 주입받는다.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

    private Api api = new Api();
    private String model = "gpt-4o-mini";
    private int timeoutMs = 15000;
    private String systemPersona =
            "당신은 삼육대학교 커리큘럼 상담 AI 도우미입니다. 한국어로 친절하고 정확하게 답하세요.";

    @Getter
    @Setter
    public static class Api {
        private String key;
        private String url = "https://api.openai.com/v1/chat/completions";
    }
}
