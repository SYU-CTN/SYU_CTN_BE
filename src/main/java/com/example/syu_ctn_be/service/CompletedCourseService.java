package com.example.syu_ctn_be.service;

import com.example.syu_ctn_be.domain.Course;
import com.example.syu_ctn_be.dto.CompletedCourseGradeRequestDto;
import com.example.syu_ctn_be.dto.CompletedCourseRequestDto;
import com.example.syu_ctn_be.dto.CompletedCourseResponseDto;
import com.example.syu_ctn_be.entity.CompletedCourse;
import com.example.syu_ctn_be.exception.ResourceNotFoundException;
import com.example.syu_ctn_be.repository.CompletedCourseRepository;
import com.example.syu_ctn_be.repository.CourseRepository;
import com.example.syu_ctn_be.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompletedCourseService {

    private final CompletedCourseRepository completedCourseRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public List<CompletedCourseResponseDto> getCompletedCourses(String loginId) {
        requireUser(loginId);
        return completedCourseRepository.findAllByLoginIdOrderByCourse_IdAsc(loginId).stream()
                .map(CompletedCourseResponseDto::from)
                .toList();
    }

    @Transactional
    public CompletedCourseResponseDto addCompletedCourse(String loginId, CompletedCourseRequestDto request) {
        requireUser(loginId);

        return completedCourseRepository.findByLoginIdAndCourse_Id(loginId, request.getCourseId())
                .map(existing -> {
                    if (request.getGrade() != null) {
                        existing.changeGrade(request.getGrade());
                    }
                    return CompletedCourseResponseDto.from(existing);
                })
                .orElseGet(() -> {
                    Course course = findCourse(request.getCourseId());
                    CompletedCourse saved = completedCourseRepository.save(
                            CompletedCourse.of(loginId, course, request.getGrade())
                    );
                    return CompletedCourseResponseDto.from(saved);
                });
    }

    @Transactional
    public CompletedCourseResponseDto updateGrade(
            String loginId,
            Long courseId,
            CompletedCourseGradeRequestDto request
    ) {
        requireUser(loginId);
        CompletedCourse completedCourse = completedCourseRepository.findByLoginIdAndCourse_Id(loginId, courseId)
                .orElseGet(() -> completedCourseRepository.save(
                        CompletedCourse.of(loginId, findCourse(courseId), null)
                ));

        completedCourse.changeGrade(request == null ? null : request.getGrade());
        return CompletedCourseResponseDto.from(completedCourse);
    }

    private void requireUser(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            throw new IllegalArgumentException("loginId는 필수입니다.");
        }

        if (!userRepository.existsByLoginId(loginId)) {
            throw new ResourceNotFoundException("사용자를 찾을 수 없습니다. loginId=" + loginId);
        }
    }

    private Course findCourse(Long courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("courseId는 필수입니다.");
        }

        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("과목을 찾을 수 없습니다. id=" + courseId));
    }
}
