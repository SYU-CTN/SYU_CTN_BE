package com.example.syu_ctn_be.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AskRequest {
    private Long sessionId;
    private String question;
}
