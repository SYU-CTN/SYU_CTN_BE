package com.example.syu_ctn_be.chat.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * OpenAI Chat Completions 메시지 단위. role 은 system/user/assistant.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiMessage {
    private String role;
    private String content;
}
