package com.example.syu_ctn_be.domain;

<<<<<<< HEAD
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
=======
import com.example.syu_ctn_be.dto.CourseRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "courses")
>>>>>>> 69907f6ed072dc8256c4ed04366d0864a1519c78
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
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
=======
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

    public void update(CourseRequestDto requestDto) {
        if (requestDto.getTitle() != null) this.title = requestDto.getTitle();
        if (requestDto.getDescription() != null) this.description = requestDto.getDescription();
        if (requestDto.getCredits() != null) this.credits = requestDto.getCredits();
        if (requestDto.getGradeLevel() != null) this.gradeLevel = requestDto.getGradeLevel();
        if (requestDto.getCategory() != null) this.category = requestDto.getCategory();
        if (requestDto.getSyllabusUrl() != null) this.syllabusUrl = requestDto.getSyllabusUrl();
    }
>>>>>>> 69907f6ed072dc8256c4ed04366d0864a1519c78
}