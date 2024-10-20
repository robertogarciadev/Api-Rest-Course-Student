package com.microservice.course;

import com.microservice.course.entities.Course;

import java.util.ArrayList;
import java.util.List;

public class DataProvider {

    public static String ENDPOINT_FIND_BY_ID = "http://localhost/api/course/id";
    public static String ENDPOINT_FIND_ALL_COURSES = "http://localhost/api/course/all";
    public static String ENDPOINT_CREATE_COURSE = "http://localhost/api/course/create";
    public static String ENDPOINT_UPDATE_COURSE = "http://localhost/api/course/update";
    public static String ENDPOINT_DELETE_COURSE = "http://localhost/api/course/delete";
    public static String ENDPOINT_DELETE_ALL_COURSE = "http://localhost/api/course/deleteAll";

    public static Course courseProvider(){
        return Course.builder()
                .id(1L)
                .teacher("Antonio")
                .build();
    }

    public static Course courseProvider2(){
        return Course.builder()
                .id(3L)
                .teacher("Fernando")
                .build();
    }

    public static Course courseProviderUpdate(){
        return Course.builder()
                .id(5L)
                .teacher("Marta")
                .build();
    }

    public static List<Course> listCoursesProvider(){
        return List.of(
                new Course(1L, "Paco"),
                new Course(2L, "Roberto"),
                new Course(3L, "Marta"));
    }

}
