package com.example.syu_ctn_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDto {
    private Long id;
    private String courseCode;
    private String title;
    private String description;
    private Integer credits;
    private Integer gradeLevel;
    private String category;
    private String syllabusUrl;
    private Long recommendation;
}