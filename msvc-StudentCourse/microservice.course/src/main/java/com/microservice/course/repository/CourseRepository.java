package com.microservice.course.repository;

import com.microservice.course.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

}
