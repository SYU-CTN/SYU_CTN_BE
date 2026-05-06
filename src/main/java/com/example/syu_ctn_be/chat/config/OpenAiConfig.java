package com.example.syu_ctn_be.chat.config;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * OpenAI 연동에 필요한 인프라(RestTemplate)와 프로퍼티 바인딩을 등록한다.
 * 별도의 RestTemplate Bean 을 분리해 두면 다른 외부 호출 모듈에 영향을 주지 않는다.
 */
@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
public class OpenAiConfig {

    /**
     * OpenAI 호출 전용 RestTemplate.
     * 타임아웃을 짧게 잡아 외부 장애가 우리 서비스 응답을 길게 막지 않도록 한다.
     */
    @Bean(name = "openAiRestTemplate")
    public RestTemplate openAiRestTemplate(RestTemplateBuilder builder, OpenAiProperties properties) {
        Duration timeout = Duration.ofMillis(properties.getTimeoutMs());
        return builder
                .connectTimeout(timeout)
                .readTimeout(timeout)
                .build();
    }
}
