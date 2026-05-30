package com.example.syu_ctn_be.repository;

import com.example.syu_ctn_be.entity.CompletedCourse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompletedCourseRepository extends JpaRepository<CompletedCourse, Long> {

    List<CompletedCourse> findAllByLoginIdOrderByCourse_IdAsc(String loginId);

    Optional<CompletedCourse> findByLoginIdAndCourse_Id(String loginId, Long courseId);

    boolean existsByLoginIdAndCourse_Id(String loginId, Long courseId);

    void deleteByLoginId(String loginId);
}
