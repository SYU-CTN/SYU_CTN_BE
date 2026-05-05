package com.example.TreeNavigator.syu_ctn_be.controller;

import com.example.TreeNavigator.syu_ctn_be.domain.Course;
import com.example.TreeNavigator.syu_ctn_be.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseRepository courseRepository;

    @GetMapping
    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseRepository.save(course);
    }
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
