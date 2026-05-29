package com.example.syu_ctn_be.entity;

import com.example.syu_ctn_be.domain.Course;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "completed_courses",
        uniqueConstraints = @UniqueConstraint(name = "uq_completed_course_user_course", columnNames = {"login_id", "course_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompletedCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", nullable = false, length = 255)
    private String loginId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "grade", length = 20)
    private String grade;

    private CompletedCourse(String loginId, Course course, String grade) {
        this.loginId = loginId;
        this.course = course;
        this.grade = normalizeGrade(grade);
    }

    public static CompletedCourse of(String loginId, Course course, String grade) {
        return new CompletedCourse(loginId, course, grade);
    }

    public void changeGrade(String grade) {
        this.grade = normalizeGrade(grade);
    }

    private static String normalizeGrade(String grade) {
        return grade == null || grade.isBlank() ? null : grade.trim();
    }
}
