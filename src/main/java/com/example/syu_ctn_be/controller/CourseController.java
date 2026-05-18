package com.example.syu_ctn_be.controller;

import com.example.syu_ctn_be.dto.CourseDTO;
import com.example.syu_ctn_be.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("V1/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * 과목 목록 조회 (필터 지원)
     * GET /api/courses
     * GET /api/courses?grade=2
     * GET /api/courses?category=SW전공
     * GET /api/courses?keyword=프로그래밍
     */
    @GetMapping
    public ResponseEntity<List<CourseDTO.Response>> getCourses(
            @RequestParam(required = false) Integer grade,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword
    ) {
        if (keyword != null) {
            return ResponseEntity.ok(courseService.searchCourses(keyword));
        }
        if (grade != null) {
            return ResponseEntity.ok(courseService.getCoursesByGrade(grade));
        }
        if (category != null) {
            return ResponseEntity.ok(courseService.getCoursesByCategory(category));
        }
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    /** 단일 과목 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO.Response> getCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourse(id));
    }

    /** 과목 추가 */
    @PostMapping
    public ResponseEntity<CourseDTO.Response> createCourse(@Valid @RequestBody CourseDTO.Request request) {
        CourseDTO.Response created = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** 과목 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO.Response> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO.Request request
    ) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    /** 과목 삭제 (관련 선수관계도 함께 삭제) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}