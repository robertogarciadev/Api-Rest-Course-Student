package com.microservice.student.service;

import com.microservice.student.entities.Student;
import com.microservice.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements IStudentService{

    @Autowired
    StudentRepository studentRepository;


    @Override
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public List<Student> findAllStudent() throws EntityNotFoundException {
        String message = "List empty";
        if (!studentRepository.findAll().isEmpty()){
            return studentRepository.findAll();
        } else
            throw new EntityNotFoundException(message);
    }

    @Override
    public List<Student> findAllStudentWithSameIdCourse(Long id) {
        return studentRepository.findAllByIdCourse(id);
    }

    @Override
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Optional<Student> updateStudent(Student student) {
        Optional<Student> studentDb = studentRepository.findById(student.getId());
        if (studentDb.isPresent()){
            studentDb.get().setName(student.getName());
            studentDb.get().setLastName(student.getLastName());
            studentDb.get().setEmail(student.getEmail());
            studentDb.get().setIdCourse(student.getIdCourse());
            return Optional.of(studentRepository.save(studentDb.get()));
        } else return Optional.empty();
    }

    @Override
    public void deleteById(Long id)  {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isPresent()){
            studentRepository.deleteById(student.get().getId());
        } else throw  new EntityNotFoundException("Entity not found");
    }

    @Override
    public void deleteAll() {
        if (!studentRepository.findAll().isEmpty()){
            studentRepository.deleteAll();
        } else throw new EntityNotFoundException("There aren´t entities");
    }
}
