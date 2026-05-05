package com.example.syu_ctn_be.controller;

import com.example.syu_ctn_be.domain.Course;
import com.example.syu_ctn_be.dto.CourseRequestDto;
import com.example.syu_ctn_be.dto.CourseResponseDto;
import com.example.syu_ctn_be.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

        boolean canEdit = checkUserAdminAuthority();

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
                .canEdit(canEdit)
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{courseId}")
    @Transactional // 엔티티의 변경사항을 DB에 반영하기 위함
    public ResponseEntity<Long> updateCourse(
            @PathVariable Long courseId,
            @RequestBody CourseRequestDto requestDto) {

        // 1. 권한 확인 (관리자가 아니면 403 Forbidden 반환)
        if (!checkUserAdminAuthority()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 2. 대상 과목 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 과목 정보를 찾을 수 없습니다. ID: " + courseId));

        // 3. 엔티티 수정 (수정 메서드 호출)
        course.update(requestDto);

        // 4. 수정된 과목 ID 반환
        return ResponseEntity.ok(course.getId());
    }

    // 임시 권한 체크 메서드
    private boolean checkUserAdminAuthority() {
        // 실제로는 SecurityContextHolder에서 유저의 Role을 꺼내와야 합니다.
        return true;
    }
}