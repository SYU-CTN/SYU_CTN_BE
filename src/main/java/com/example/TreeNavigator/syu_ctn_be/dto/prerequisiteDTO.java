package com.example.TreeNavigator.syu_ctn_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class prerequisiteDTO {
    private Long preId;
    private Long postId;
}
