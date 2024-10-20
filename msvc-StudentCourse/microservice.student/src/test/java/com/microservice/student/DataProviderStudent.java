package com.microservice.student;

import com.microservice.student.entities.Student;

import java.util.List;

public class DataProviderStudent {

    public static String ENDPOINT_FIND_STUDENT_BY_ID ="http://localhost/api/student/id";
    public static String ENDPOINT_FIND_ALL_STUDENT ="http://localhost/api/student/findAll";
    public static String ENDPOINT_FIND_ALL_STUDENT_BY_COURSE_ID ="http://localhost/api/student/allStudentByCourseId";
    public static String ENDPOINT_CREATE_STUDENT ="http://localhost/api/student/create";
    public static String ENDPOINT_UPDATE_STUDENT ="http://localhost/api/student/update";
    public static String ENDPOINT_DELETE_BY_ID ="http://localhost/api/student/deleteById/1";
    public static String ENDPOINT_DELETE_All ="http://localhost/api/student/deleteAll";




    public static Student getStudent(){
        return Student.builder()
                .id(1L)
                .name("Rob")
                .lastName("Gordon")
                .email("rob@gmail.com")
                .idCourse(1L)
                .build();
    }
    public static String getJsonRequest(){
        return "{\"name\":\"Rob\"," +
                "\"lastName\":\"Gordon\"," +
                "\"email\":\"rob@gmail.com\"," +
                "\"idCourse\":1}";
    }

    public static List<Student> getListStudent(){
        Student student1 = new Student(1L, "Rob", "Gordon", "rob@gmail.com", 1L);
        Student student2 = new Student(2L, "Marta", "Mendoza", "marmen@gmail.com", 1L);
        Student student3 = new Student(3L, "Amparo", "Aguilas", "ampa@gmail.com", 2L);
        return List.of(student1, student2, student3);
    }
    public static List<Student> getListStudentByCourseId(){
        Student student1 = new Student(1L, "Rob", "Gordon", "rob@gmail.com", 1L);
        Student student2 = new Student(2L, "Marta", "Mendoza", "marmen@gmail.com", 1L);
        return List.of(student1, student2);
    }
}
