package com.example.syu_ctn_be.config;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
public class OpenAiConfig {

    @Bean(name = "openAiRestTemplate")
    public RestTemplate openAiRestTemplate(RestTemplateBuilder builder, OpenAiProperties properties) {
        Duration timeout = Duration.ofMillis(properties.getTimeoutMs());
        return builder
                .connectTimeout(timeout)
                .readTimeout(timeout)
                .build();
    }
}
