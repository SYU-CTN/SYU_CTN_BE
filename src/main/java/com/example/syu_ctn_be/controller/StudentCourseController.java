package com.example.syu_ctn_be.controller;

import com.example.syu_ctn_be.dto.CompletedCourseGradeRequestDto;
import com.example.syu_ctn_be.dto.CompletedCourseRequestDto;
import com.example.syu_ctn_be.dto.CompletedCourseResponseDto;
import com.example.syu_ctn_be.service.CompletedCourseService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students/{loginId}/completed-courses")
@RequiredArgsConstructor
public class StudentCourseController {

    private final CompletedCourseService completedCourseService;

    @PostMapping
    public ResponseEntity<CompletedCourseResponseDto> addCompletedCourse(
            @PathVariable String loginId,
            @Valid @RequestBody CompletedCourseRequestDto request
    ) {
        CompletedCourseResponseDto response = completedCourseService.addCompletedCourse(loginId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CompletedCourseResponseDto>> getCompletedCourses(@PathVariable String loginId) {
        return ResponseEntity.ok(completedCourseService.getCompletedCourses(loginId));
    }

    @PatchMapping("/{courseId}/grade")
    public ResponseEntity<CompletedCourseResponseDto> updateGrade(
            @PathVariable String loginId,
            @PathVariable Long courseId,
            @RequestBody CompletedCourseGradeRequestDto request
    ) {
        return ResponseEntity.ok(completedCourseService.updateGrade(loginId, courseId, request));
    }
}
