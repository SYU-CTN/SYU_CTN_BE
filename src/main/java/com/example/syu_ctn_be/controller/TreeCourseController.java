package com.example.syu_ctn_be.controller;

import com.example.syu_ctn_be.domain.Course;
import com.example.syu_ctn_be.dto.TreeCourseDto;
import com.example.syu_ctn_be.dto.TreeCourseRequest;
import com.example.syu_ctn_be.repository.CourseRepository;
import java.util.List;
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

@RestController
@RequestMapping("/V1/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TreeCourseController {

    private final CourseRepository courseRepository;

    @GetMapping
    public List<TreeCourseDto> getCourses() {
        return courseRepository.findAll().stream()
                .map(TreeCourseDto::from)
                .toList();
    }

    @PostMapping
    public TreeCourseDto createCourse(@RequestBody TreeCourseRequest request) {
        validateCreateRequest(request);

        Course course = Course.builder()
                .code(request.getCourseCode())
                .title(request.getTitle())
                .credits(request.getCredits())
                .category(request.getCategory())
                .grade(request.getGradeLevel())
                .semester(request.getSemester())
                .build();

        return TreeCourseDto.from(courseRepository.save(course));
    }

    @PatchMapping("/{id}")
    public TreeCourseDto patchCourse(@PathVariable Long id, @RequestBody TreeCourseRequest request) {
        return updateCourse(id, request);
    }

    @PutMapping("/{id}")
    public TreeCourseDto putCourse(@PathVariable Long id, @RequestBody TreeCourseRequest request) {
        return updateCourse(id, request);
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    private TreeCourseDto updateCourse(Long id, TreeCourseRequest request) {
        return courseRepository.findById(id)
                .map(course -> {
                    if (request.getTitle() != null) {
                        course.setTitle(request.getTitle());
                    }
                    if (request.getCourseCode() != null) {
                        course.setCourseCode(request.getCourseCode());
                    }
                    if (request.getCredits() != null) {
                        course.setCredits(request.getCredits());
                    }
                    if (request.getCategory() != null) {
                        course.setCategory(request.getCategory());
                    }
                    if (request.getGradeLevel() != null) {
                        course.setGradeLevel(request.getGradeLevel());
                    }
                    if (request.getSemester() != null) {
                        course.setSemester(request.getSemester());
                    }
                    return TreeCourseDto.from(courseRepository.save(course));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found. id=" + id));
    }

    private void validateCreateRequest(TreeCourseRequest request) {
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
}
