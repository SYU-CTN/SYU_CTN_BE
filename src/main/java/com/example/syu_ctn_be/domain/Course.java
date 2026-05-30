package com.example.syu_ctn_be.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    private String code;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "professor_name", length = 50)
    private String professorName;

    @Column(nullable = false)
    private Integer credits;

    @Column(name = "grade_level", nullable = false)
    private Integer grade;

    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(name = "track_name", length = 100)
    private String trackName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "syllabus_url", length = 500)
    private String syllabusUrl;

    private Integer recommendation;

    @Column(name = "pos_x")
    private Float posX;

    @Column(name = "pos_y")
    private Float posY;

    @ManyToMany
    @JoinTable(
            name = "prerequisites",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "pre_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE) // 이 설정을 추가하여 하이버네이트의 이중 삭제 방지
    @Builder.Default
    private List<Course> prerequisiteCourses = new ArrayList<>();

    public String getCourseCode() {
        return code;
    }

    public void setCourseCode(String courseCode) {
        this.code = courseCode;
    }

    public Integer getGradeLevel() {
        return grade;
    }

    public void setGradeLevel(Integer gradeLevel) {
        this.grade = gradeLevel;
    }
}
