package com.microservice.student.service;

import com.microservice.student.entities.Student;

import java.util.List;
import java.util.Optional;

public interface IStudentService {

    Optional<Student> findById(Long id);
    List<Student> findAllStudent();
    List<Student> findAllStudentWithSameIdCourse(Long id);
    Student createStudent(Student student);
    Optional<Student> updateStudent(Student student);
    void deleteById(Long id);
    void deleteAll();
}
