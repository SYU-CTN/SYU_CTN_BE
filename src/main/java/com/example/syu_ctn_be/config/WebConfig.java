package com.example.syu_ctn_be.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
<<<<<<< HEAD:src/main/java/com/example/syu_ctn_be/config/WebConfig.java
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
=======
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://localhost:3000"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
>>>>>>> 69907f6ed072dc8256c4ed04366d0864a1519c78:src/main/java/com/example/TreeNavigator/syu_ctn_be/config/WebConfig.java
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}