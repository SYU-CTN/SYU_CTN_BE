package com.example.TreeNavigator.syu_ctn_be.repository;
import com.example.TreeNavigator.syu_ctn_be.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}
