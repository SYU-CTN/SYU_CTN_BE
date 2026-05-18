package com.example.syu_ctn_be.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_code", unique = true, nullable = false, length = 20)
    private String code;                  // 예: CS101

    @Column(nullable = false, length = 100)
    private String title;                 // 예: AI를 위한 미적분학

    @Column(nullable = false)
    private Integer credits;              // 학점

    @Column(name = "grade_level", nullable = false)
    private Integer grade;                // 학년 (1~4)

    @Column(nullable = false)
    private Integer semester;             // 학기 (1, 2)

    @Column(nullable = false, length = 30)
    private String category;              // 공통 / SW전공 / 컴공전공
}