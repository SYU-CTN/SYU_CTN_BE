package com.example.syu_ctn_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TreePrerequisiteDto {
    private Long source;
    private Long target;
}
