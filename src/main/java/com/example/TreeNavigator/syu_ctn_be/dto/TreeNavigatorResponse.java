package com.example.TreeNavigator.syu_ctn_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TreeNavigatorResponse {
    private List<CourseDTO> courses;

    private List<prerequisiteDTO> links;

}
