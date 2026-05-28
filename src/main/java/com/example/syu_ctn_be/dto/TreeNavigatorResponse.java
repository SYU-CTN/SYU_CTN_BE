package com.example.syu_ctn_be.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TreeNavigatorResponse {
    private List<TreeCourseDto> courses;
    private List<TreePrerequisiteDto> prerequisites;
}
