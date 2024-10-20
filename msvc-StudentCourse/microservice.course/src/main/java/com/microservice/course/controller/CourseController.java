package com.microservice.course.controller;

import com.microservice.course.entities.Course;
import com.microservice.course.http.response.StudentByCourseResponse;
import com.microservice.course.service.CourseServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.Response;
import jdk.jshell.Snippet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/course")
public class CourseController {

    @Autowired
    CourseServiceImpl courseService;

    //Buscar por Id
    @GetMapping("/id")
    public ResponseEntity<Course> findById(@RequestParam Long id){
        return courseService.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Course>> findAll(){

        return !courseService.findAllCourse().isEmpty()
                ? ResponseEntity.ok(courseService.findAllCourse())
                : ResponseEntity.noContent().build();
    }

    @GetMapping("/allStudentWithSameCourse")
    public ResponseEntity<StudentByCourseResponse> findAllStudentSameCourse(@RequestParam Long id){
        StudentByCourseResponse allStudentWithSameCourse = courseService.findStudentWithSameIdCourse(id);
        return ResponseEntity.status(HttpStatus.OK).body(allStudentWithSameCourse);
    }

    @PostMapping("/create")
    public ResponseEntity<Course> createCourse(@RequestBody Course course){
        Course courseCreated = courseService.create(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(courseCreated);
    }


    @PutMapping("/update")
    public ResponseEntity<Course> updateCourse(@RequestBody Course course){
        Optional<Course> courseUpdate = courseService.updateCourse(course);
        return courseUpdate.map(courseGet -> ResponseEntity.status(HttpStatus.ACCEPTED).body(courseGet))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteById(@RequestParam Long id){
       try {
           courseService.delete(id);
           return ResponseEntity.status(HttpStatus.ACCEPTED).build();
       } catch (EntityNotFoundException e){
          return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
       }
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAll(){

        try {
            courseService.deleteAll();
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @GetMapping("/searchStudent/{idCourse}")
    public ResponseEntity<StudentByCourseResponse> findStudentByIdCourse(@PathVariable Long idCourse){
        return ResponseEntity.ok(courseService.findStudentWithSameIdCourse(idCourse));
    }
}
