package com.example.TreeNavigator.syu_ctn_be.controller;

import com.example.TreeNavigator.syu_ctn_be.domain.Course;
import com.example.TreeNavigator.syu_ctn_be.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/V1/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
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

    @PatchMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody Course updateData) {
        return courseRepository.findById(id)
                .map(course -> {
                    if (updateData.getTitle() != null) course.setTitle(updateData.getTitle());
                    if (updateData.getCredits() != null) course.setCredits(updateData.getCredits());
                    if (updateData.getDescription() != null) course.setDescription(updateData.getDescription());
                    return courseRepository.save(course);
                })
                .orElseThrow(() -> new IllegalArgumentException("해당 과목이 없습니다. id=" + id));
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}