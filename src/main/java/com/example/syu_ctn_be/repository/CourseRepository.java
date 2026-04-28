package com.example.syu_ctn_be.repository;

import com.example.syu_ctn_be.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    // 기본 상세 조회를 위한 findById는 내장되어 있습니다.
}