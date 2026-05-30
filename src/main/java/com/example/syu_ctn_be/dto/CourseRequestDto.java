package com.example.syu_ctn_be.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequestDto {
    private String title;
    private String description;
    private Integer credits;
    private Integer gradeLevel;
    private String category;
    private String syllabusUrl;
}