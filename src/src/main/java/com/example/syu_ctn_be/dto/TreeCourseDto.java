package com.example.syu_ctn_be.dto;

import com.example.syu_ctn_be.domain.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TreeCourseDto {
    private Long id;
    private String courseCode;
    private String title;
    private Integer credits;
    private String category;
    private Integer gradeLevel;
    private Integer semester;
    private Float posX;
    private Float posY;

    public static TreeCourseDto from(Course course) {
        return new TreeCourseDto(
                course.getId(),
                course.getCourseCode(),
                course.getTitle(),
                course.getCredits(),
                course.getCategory(),
                course.getGradeLevel(),
                course.getSemester(),
                course.getPosX(),
                course.getPosY()
        );
    }
}
