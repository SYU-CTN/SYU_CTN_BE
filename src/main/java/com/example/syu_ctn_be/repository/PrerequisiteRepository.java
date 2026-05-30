package com.example.syu_ctn_be.repository;

import com.example.syu_ctn_be.domain.Prerequisite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrerequisiteRepository extends JpaRepository<Prerequisite, Long> {

    boolean existsByPreCourseIdAndPostCourseId(Long preCourseId, Long postCourseId);

    Optional<Prerequisite> findByPreCourseIdAndPostCourseId(Long preCourseId, Long postCourseId);

    /** 특정 과목의 선수 과목들 조회 (이 과목을 듣기 전에 들어야 하는 것들) */
    List<Prerequisite> findByPostCourseId(Long postCourseId);

    /** 특정 과목의 후속 과목들 조회 */
    List<Prerequisite> findByPreCourseId(Long preCourseId);

    /** 특정 과목과 관련된 모든 선수관계 삭제 (과목 삭제 시 사용) */
    void deleteByPreCourseIdOrPostCourseId(Long preCourseId, Long postCourseId);
}