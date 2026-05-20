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
    //현재 사용자가 편집 권한이 있는지 판단을 위함.
    private boolean canEdit;
}