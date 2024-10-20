package com.microservice.course.service;

import com.microservice.course.client.StudentClient;
import com.microservice.course.controller.dto.StudentDTO;
import com.microservice.course.entities.Course;
import com.microservice.course.http.response.StudentByCourseResponse;
import com.microservice.course.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements ICourseService{

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentClient studentClient;

    @Override
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    public List<Course> findAllCourse() {
        String message = "List empty";
        if (!courseRepository.findAll().isEmpty()){
            return  courseRepository.findAll();
        } else throw new EntityNotFoundException(message);
    }

    @Override
    public Optional<Course> updateCourse(Course course) {
        Optional<Course> coursedb = courseRepository.findById(course.getId());
        if (coursedb.isPresent()){
            coursedb.get().setTeacher(course.getTeacher());
            Course courseUpdated = courseRepository.save(coursedb.get());
            return Optional.of(courseUpdated);
        }
        return Optional.empty();
    }

    @Override
    public Course create(Course course) {
        return courseRepository.save(course);

    }

    @Override
    public void delete(Long id) {
        Optional<Course> courseDb = courseRepository.findById(id);
        if (courseDb.isPresent()){
            courseRepository.deleteById(courseDb.get().getId());
        } else throw new EntityNotFoundException("Non existing object");
    }

    @Override
    public void deleteAll() {

        if (!courseRepository.findAll().isEmpty()){
            courseRepository.deleteAll();
        } else throw new EntityNotFoundException("List empty");

    }

    @Override
    public StudentByCourseResponse findStudentWithSameIdCourse(Long idCourse) {

        Optional<Course> course = courseRepository.findById(idCourse);
        List<StudentDTO> studentDTOList = studentClient.findAllStudentWithSameCourseId(idCourse);
        return StudentByCourseResponse.builder()
                .id(course.map(Course::getId).orElse(null))
                .teacher(course.map(Course::getTeacher).orElse(null))
                .studentDTOList(studentDTOList)
                .build();
    }
}
