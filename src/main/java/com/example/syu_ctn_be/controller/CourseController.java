package com.example.syu_ctn_be.controller;

import com.example.syu_ctn_be.domain.Course;
import com.example.syu_ctn_be.dto.CourseResponseDto;
import com.example.syu_ctn_be.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseRepository courseRepository;

    // 과목 상세 조회 API
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponseDto> getCourseDetail(@PathVariable Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 과목 정보를 찾을 수 없습니다. ID: " + courseId));

        CourseResponseDto response = CourseResponseDto.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .title(course.getTitle())
                .description(course.getDescription())
                .credits(course.getCredits())
                .gradeLevel(course.getGradeLevel())
                .category(course.getCategory())
                .syllabusUrl(course.getSyllabusUrl())
                .recommendation(course.getRecommendation())
                .build();

        return ResponseEntity.ok(response);
    }
}