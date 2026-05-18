package com.example.syu_ctn_be.domain;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "prerequisites",
        uniqueConstraints = @UniqueConstraint(columnNames = {"pre_course_id", "post_course_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prerequisite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 선수 과목 (먼저 들어야 하는 과목) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_course_id", nullable = false)
    private Course preCourse;

    /** 후속 과목 (선수 이후에 들을 수 있는 과목) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_course_id", nullable = false)
    private Course postCourse;
}
