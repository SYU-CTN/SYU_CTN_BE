package com.example.syu_ctn_be.dto;

import com.example.syu_ctn_be.domain.UserType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {
    private String loginId;
    private String password;
    private String name;
    private UserType userType;
    private String department;
    private Integer grade;
    private String email;
    private String phone;
}