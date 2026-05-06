package com.example.TreeNavigator.syu_ctn_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id; //과목 고유 ID
    private String courseCode; //과목 코드
    private String title; //과목명
    private Integer gradeLevel; //권장 학년
    private Float posX; //과목 박스에 react flow x좌표
    private Float posY; //과목 박스에 react flow y좌표
}
