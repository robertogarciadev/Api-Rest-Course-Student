package com.microservice.student.controller;

import com.microservice.student.entities.Student;
import com.microservice.student.service.StudentServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/student")
public class StudentController {

    @Autowired
    private StudentServiceImpl studentService;


    @GetMapping("/id")
    public ResponseEntity<?> findById(@RequestParam Long id){
        return studentService.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll(){

        try {
            List<Student> allStudent = studentService.findAllStudent();
            return ResponseEntity.ok(allStudent);
        }catch (EntityNotFoundException e){
            return ResponseEntity.noContent().build();
        }

    }


    @GetMapping("/allStudentByCourseId")
    public ResponseEntity<List<Student>> findAllStudentByCourseId(@RequestParam Long idCourse){
        List<Student> studentWithSameCourseId = studentService.findAllStudentWithSameIdCourse(idCourse);
        return !studentWithSameCourseId.isEmpty() ? ResponseEntity.ok(studentWithSameCourseId) :
                ResponseEntity.noContent().build();
    }

    @PostMapping("/create")
    public ResponseEntity<Student> createStudent(@RequestBody Student student){
        Student newStudent = studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(newStudent);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateStudent(@RequestBody Student student){
        Optional<Student> studentUpdated = studentService.updateStudent(student);
        return studentUpdated.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        try {
            studentService.deleteById(id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (EntityNotFoundException e ){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(){
        try {
            studentService.deleteAll();
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (EntityNotFoundException e ){
            return ResponseEntity.noContent().build();
        }
    }

}
