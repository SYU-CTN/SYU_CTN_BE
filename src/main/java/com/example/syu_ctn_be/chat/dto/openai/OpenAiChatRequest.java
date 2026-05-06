package com.example.syu_ctn_be.chat.dto.openai;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OpenAiChatRequest {
    private final String model;
    private final List<OpenAiMessage> messages;
}
