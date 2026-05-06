package com.example.syu_ctn_be.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String courseCode; // 과목코드 (예: E01)

    @Column(nullable = false)
    private String title; // 과목명

    private Integer credits; // 학점
    private Integer gradeLevel; // 학년
    private String category; // 구분 (전공/교양 등)

    @Column(columnDefinition = "TEXT")
    private String description; // 상세 설명

    private String syllabusUrl; // 강의계획서 링크

    private Long recommendation; // 후수강 추천 과목 ID
}