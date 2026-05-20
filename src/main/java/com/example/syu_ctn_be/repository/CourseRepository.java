package com.example.syu_ctn_be.repository;

import com.example.syu_ctn_be.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
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
=======
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    // 기본 상세 조회를 위한 findById는 내장되어 있습니다.
>>>>>>> 69907f6ed072dc8256c4ed04366d0864a1519c78
}