package com.example.syu_ctn_be.dto;

import com.example.syu_ctn_be.domain.Course;
import com.example.syu_ctn_be.entity.CompletedCourse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompletedCourseResponseDto {
    private Long id;
    private String loginId;
    private Long courseId;
    private String courseCode;
    private String code;
    private String title;
    private Integer credits;
    private Integer gradeLevel;
    private Integer semester;
    private String category;
    private String completedGrade;
    private String grade;

    public static CompletedCourseResponseDto from(CompletedCourse completedCourse) {
        Course course = completedCourse.getCourse();
        return CompletedCourseResponseDto.builder()
                .id(completedCourse.getId())
                .loginId(completedCourse.getLoginId())
                .courseId(course.getId())
                .courseCode(course.getCode())
                .code(course.getCode())
                .title(course.getTitle())
                .credits(course.getCredits())
                .gradeLevel(course.getGrade())
                .semester(course.getSemester())
                .category(course.getCategory())
                .completedGrade(completedCourse.getGrade())
                .grade(completedCourse.getGrade())
                .build();
    }
}
