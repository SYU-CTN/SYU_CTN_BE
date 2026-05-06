package com.example.syu_ctn_be.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StartSessionResponse {
    private final Long sessionId;
}
