package com.microservice.course.client;

import com.microservice.course.controller.dto.StudentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient (name = "msvc-student", url = "http://localhost:8090/api/student")
public interface StudentClient {

    @GetMapping("/allStudentByCourseId")
    List<StudentDTO> findAllStudentWithSameCourseId(@RequestParam Long idCourse);
}
