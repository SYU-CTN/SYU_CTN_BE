package com.example.TreeNavigator.syu_ctn_be.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

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
    private String courseCode;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "professor_name", length = 50)
    private String professorName;

    private Integer credits;

    @Column(name = "grade_level", nullable = false)
    private Integer gradeLevel;

    @Column(length = 30)
    private String category;

    @Column(name = "track_name", length = 100)
    private String trackName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "syllabus_url", length = 500)
    private String syllabusUrl;

    private Integer recommendation;

    // React Flow 좌표 정보
    @Column(name = "pos_x")
    private Float posX;

    @Column(name = "pos_y")
    private Float posY;

    /**
     * 선수 과목 관계 설정 (Self-referencing Many-to-Many)
     * 중간 테이블인 prerequisites를 통해 연결됩니다.
     */
    @ManyToMany
    @JoinTable(
            name = "prerequisites",
            joinColumns = @JoinColumn(name = "post_id"),
    inverseJoinColumns = @JoinColumn(name = "pre_id")
    )
    @Builder.Default
    private List<Course> prerequisiteCourses = new ArrayList<>();
}