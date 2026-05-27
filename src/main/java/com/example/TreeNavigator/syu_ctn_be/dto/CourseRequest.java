package com.example.TreeNavigator.syu_ctn_be.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseRequest {

    @JsonAlias("code")
    private String courseCode;

    private String title;

    private Integer credits;

    private String category;

    @JsonAlias("grade")
    private Integer gradeLevel;

    private Integer semester;
}
