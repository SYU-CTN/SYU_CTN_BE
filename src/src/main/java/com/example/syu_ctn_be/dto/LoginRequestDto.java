package com.example.syu_ctn_be.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {
    private String loginId;
    private String password;
}