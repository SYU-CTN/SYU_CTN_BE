package com.example.syu_ctn_be.service;

import com.example.syu_ctn_be.domain.Course;
import com.example.syu_ctn_be.dto.TreeCourseDto;
import com.example.syu_ctn_be.dto.TreeNavigatorResponse;
import com.example.syu_ctn_be.dto.TreePrerequisiteDto;
import com.example.syu_ctn_be.repository.CourseRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TreeNavigatorService {

    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public TreeNavigatorResponse getCourseTree() {
        List<Course> courses = courseRepository.findAll();
        List<TreeCourseDto> courseDtos = courses.stream()
                .map(TreeCourseDto::from)
                .toList();

        List<TreePrerequisiteDto> prerequisiteDtos = new ArrayList<>();
        for (Course course : courses) {
            for (Course preCourse : course.getPrerequisiteCourses()) {
                prerequisiteDtos.add(new TreePrerequisiteDto(preCourse.getId(), course.getId()));
            }
        }

        return new TreeNavigatorResponse(courseDtos, prerequisiteDtos);
    }
}
