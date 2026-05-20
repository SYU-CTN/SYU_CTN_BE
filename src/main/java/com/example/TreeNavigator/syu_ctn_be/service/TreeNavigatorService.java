package com.example.TreeNavigator.syu_ctn_be.service;

import com.example.TreeNavigator.syu_ctn_be.domain.Course;
import com.example.TreeNavigator.syu_ctn_be.dto.CourseDTO;
import com.example.TreeNavigator.syu_ctn_be.dto.TreeNavigatorResponse;
import com.example.TreeNavigator.syu_ctn_be.dto.prerequisiteDTO;
import com.example.TreeNavigator.syu_ctn_be.repository.CourseRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TreeNavigatorService {

    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public TreeNavigatorResponse getCourseTree() {
        List<Course> courses = courseRepository.findAll();

        List<CourseDTO> courseDTOs = courses.stream()
                .map(course -> new CourseDTO(
                        course.getId(),
                        course.getCourseCode(),
                        course.getTitle(),
                        course.getGradeLevel(),
                        course.getPosX(),
                        course.getPosY()
                ))
                .collect(Collectors.toList());

        List<prerequisiteDTO> prerequisiteDTOs = new ArrayList<>();
        for(Course course : courses) {
        for(Course preCourse : course.getPrerequisiteCourses()) {
            prerequisiteDTOs.add(new prerequisiteDTO(
                    preCourse.getId(),
                    course.getId()
            ));
        }
    }
    return new TreeNavigatorResponse(courseDTOs, prerequisiteDTOs);
}

}
