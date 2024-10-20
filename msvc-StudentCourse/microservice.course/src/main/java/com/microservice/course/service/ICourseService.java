package com.microservice.course.service;

import com.microservice.course.entities.Course;
import com.microservice.course.http.response.StudentByCourseResponse;

import java.util.List;
import java.util.Optional;

public interface ICourseService {

    Optional<Course> findById(Long id);
    List<Course> findAllCourse();
    Optional<Course> updateCourse(Course course);
    Course create(Course course);
    void delete(Long id);
    void deleteAll();
    StudentByCourseResponse findStudentWithSameIdCourse(Long idCourse);

}
