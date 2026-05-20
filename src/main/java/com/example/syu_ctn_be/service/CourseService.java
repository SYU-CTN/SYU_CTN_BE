package com.example.syu_ctn_be.service;

import com.example.syu_ctn_be.dto.CourseDTO;
import com.example.syu_ctn_be.domain.Course;
import com.example.syu_ctn_be.exception.DuplicateResourceException;
import com.example.syu_ctn_be.exception.ResourceNotFoundException;
import com.example.syu_ctn_be.repository.CourseRepository;
import com.example.syu_ctn_be.repository.PrerequisiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final PrerequisiteRepository prerequisiteRepository;

    /** 전체 과목 조회 */
    public List<CourseDTO.Response> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseDTO.Response::from)
                .toList();
    }

    /** 단일 과목 조회 */
    public CourseDTO.Response getCourse(Long id) {
        return CourseDTO.Response.from(findById(id));
    }

    /** 학년별 조회 */
    public List<CourseDTO.Response> getCoursesByGrade(Integer grade) {
        return courseRepository.findByGrade(grade).stream()
                .map(CourseDTO.Response::from)
                .toList();
    }

    /** 카테고리별 조회 */
    public List<CourseDTO.Response> getCoursesByCategory(String category) {
        return courseRepository.findByCategory(category).stream()
                .map(CourseDTO.Response::from)
                .toList();
    }

    /** 키워드 검색 */
    public List<CourseDTO.Response> searchCourses(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllCourses();
        }
        return courseRepository.searchByKeyword(keyword.trim()).stream()
                .map(CourseDTO.Response::from)
                .toList();
    }

    /** 과목 추가 */
    @Transactional
    public CourseDTO.Response createCourse(CourseDTO.Request request) {
        if (courseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("이미 존재하는 과목 코드입니다: " + request.getCode());
        }
        Course saved = courseRepository.save(request.toEntity());
        return CourseDTO.Response.from(saved);
    }

    /** 과목 정보 수정 */
    @Transactional
    public CourseDTO.Response updateCourse(Long id, CourseDTO.Request request) {
        Course course = findById(id);

        // 코드 변경 시 중복 체크
        if (!course.getCode().equals(request.getCode())
                && courseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("이미 존재하는 과목 코드입니다: " + request.getCode());
        }

        course.setCode(request.getCode());
        course.setTitle(request.getTitle());
        course.setCredits(request.getCredits());
        course.setGrade(request.getGrade());
        course.setSemester(request.getSemester());
        course.setCategory(request.getCategory());

        return CourseDTO.Response.from(course);
    }

    /** 과목 삭제 (관련 선수관계도 함께 삭제) */
    @Transactional
    public void deleteCourse(Long id) {
        Course course = findById(id);
        // 관련 선수관계 삭제
        prerequisiteRepository.deleteByPreCourseIdOrPostCourseId(id, id);
        // 과목 삭제
        courseRepository.delete(course);
    }

    /** 내부 헬퍼: ID로 조회 (없으면 예외) */
    Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("과목을 찾을 수 없습니다. id=" + id));
    }
}