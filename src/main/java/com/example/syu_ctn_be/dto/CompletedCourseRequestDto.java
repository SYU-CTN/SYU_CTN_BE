package com.example.syu_ctn_be.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompletedCourseRequestDto {

    @NotNull(message = "courseId는 필수입니다.")
    private Long courseId;

    private String grade;
}
