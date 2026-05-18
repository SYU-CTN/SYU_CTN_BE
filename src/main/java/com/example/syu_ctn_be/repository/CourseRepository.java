package com.example.syu_ctn_be.repository;

import com.example.syu_ctn_be.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCode(String code);

    boolean existsByCode(String code);

    List<Course> findByGrade(Integer grade);

    List<Course> findByCategory(String category);

    /** 검색: 과목명 또는 코드에 키워드가 포함된 과목 조회 */
    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.code) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> searchByKeyword(String keyword);
}