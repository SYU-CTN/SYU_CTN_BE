package com.example.TreeNavigator.syu_ctn_be.controller;

import com.example.TreeNavigator.syu_ctn_be.domain.Course;
import com.example.TreeNavigator.syu_ctn_be.dto.CourseDTO;
import com.example.TreeNavigator.syu_ctn_be.dto.CourseRequest;
import com.example.TreeNavigator.syu_ctn_be.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/V1/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CourseController {

    private final CourseRepository courseRepository;

    @GetMapping
    public List<CourseDTO> getCourses() {
        return courseRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public CourseDTO createCourse(@RequestBody CourseRequest request) {
        validateCreateRequest(request);

        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .title(request.getTitle())
                .credits(request.getCredits())
                .category(request.getCategory())
                .gradeLevel(request.getGradeLevel())
                .semester(request.getSemester())
                .build();

        return toDto(courseRepository.save(course));
    }

    @PatchMapping("/{id}")
    public CourseDTO patchCourse(@PathVariable Long id, @RequestBody CourseRequest request) {
        return updateCourse(id, request);
    }

    @PutMapping("/{id}")
    public CourseDTO putCourse(@PathVariable Long id, @RequestBody CourseRequest request) {
        return updateCourse(id, request);
    }

    private CourseDTO updateCourse(Long id, CourseRequest request) {
        return courseRepository.findById(id)
                .map(course -> {
                    if (request.getTitle() != null) course.setTitle(request.getTitle());
                    if (request.getCourseCode() != null) course.setCourseCode(request.getCourseCode());
                    if (request.getCredits() != null) course.setCredits(request.getCredits());
                    if (request.getCategory() != null) course.setCategory(request.getCategory());
                    if (request.getGradeLevel() != null) course.setGradeLevel(request.getGradeLevel());
                    if (request.getSemester() != null) course.setSemester(request.getSemester());
                    return toDto(courseRepository.save(course));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found. id=" + id));
    }

    private CourseDTO toDto(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getCourseCode(),
                course.getTitle(),
                course.getCredits(),
                course.getCategory(),
                course.getGradeLevel(),
                course.getSemester(),
                course.getPosX(),
                course.getPosY()
        );
    }

    private void validateCreateRequest(CourseRequest request) {
        if (request.getCourseCode() == null || request.getCourseCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "courseCode or code is required.");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required.");
        }
        if (request.getCredits() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "credits is required.");
        }
        if (request.getGradeLevel() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "gradeLevel or grade is required.");
        }
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
