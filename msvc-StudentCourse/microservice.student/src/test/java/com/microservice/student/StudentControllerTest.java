package com.microservice.student;



import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.microservice.student.controller.StudentController;
import com.microservice.student.entities.Student;
import com.microservice.student.service.StudentServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.xml.crypto.Data;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@TestPropertySource(properties = {
        "eureka.client.enabled=false"  // Deshabilita el cliente de Eureka para este test
})
public class StudentControllerTest {

    @Mock
    private StudentServiceImpl studentServiceMock;

    @InjectMocks
    private StudentController studentController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
    }


    @Test
    public void testFindById_foundEntity_responseOk() throws Exception {
        //GIVEN
        when(studentServiceMock.findById(anyLong())).thenReturn(Optional.of(DataProviderStudent.getStudent()));
        //WHEN & THEN
        mockMvc.perform(get(DataProviderStudent.ENDPOINT_FIND_STUDENT_BY_ID).param("id", String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Rob"))
                .andExpect(jsonPath("$.lastName").value("Gordon"))
                .andExpect(jsonPath("$.email").value("rob@gmail.com"))
                .andExpect(jsonPath("$.idCourse").value(1L));

        verify(studentServiceMock, times(1)).findById(anyLong());

    }

    @Test
    public void testFindById_entityNotFound_responseNotFound() throws Exception {

        //GIVEN
        when(studentServiceMock.findById(anyLong())).thenReturn(Optional.empty());
        //WHEN & THEN
        mockMvc.perform(get(DataProviderStudent.ENDPOINT_FIND_STUDENT_BY_ID).param("id", String.valueOf(1L)))
                .andExpect(status().isNotFound());
        verify(studentServiceMock, times(1)).findById(anyLong());

    }

    @Test
    public void testFindAll_thereAreEntities_responseOk() throws Exception {
        //GIVEN
        when(studentServiceMock.findAllStudent()).thenReturn(DataProviderStudent.getListStudent());
        //WHEN & THEN
        mockMvc.perform(get(DataProviderStudent.ENDPOINT_FIND_ALL_STUDENT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Rob"))
                .andExpect(jsonPath("$[0].lastName").value("Gordon"))
                .andExpect(jsonPath("$[0].email").value("rob@gmail.com"))
                .andExpect(jsonPath("$[0].idCourse").value(1L));

        verify(studentServiceMock, times(1)).findAllStudent();
    }

    @Test
    public void testFindAllStudent_noEntities_responseNoContent() throws Exception {
        //GIVEN
        when(studentServiceMock.findAllStudent()).thenThrow(EntityNotFoundException.class);
        //WHEN & THEN
        mockMvc.perform(get(DataProviderStudent.ENDPOINT_FIND_ALL_STUDENT))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.length()").doesNotExist());

        verify(studentServiceMock, times(1)).findAllStudent();

    }

    @Test
    public void testFindAllStudentByCourseId_thereAreEntities_responseOk() throws Exception {
        //GIVEN
        List<Student> studentByCourseId = DataProviderStudent.getListStudentByCourseId();
        when(studentServiceMock.findAllStudentWithSameIdCourse(anyLong())).thenReturn(studentByCourseId);
        //WHEN &THEN
        mockMvc.perform(get(DataProviderStudent.ENDPOINT_FIND_ALL_STUDENT_BY_COURSE_ID)
                        .param("idCourse", String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Rob"))
                .andExpect(jsonPath("$[0].lastName").value("Gordon"))
                .andExpect(jsonPath("$[0].email").value("rob@gmail.com"))
                .andExpect(jsonPath("$[0].idCourse").value(1L))
                .andExpect(jsonPath("$[1].idCourse").value(1L));
        verify(studentServiceMock,times(1)).findAllStudentWithSameIdCourse(anyLong());
    }
    @Test
    public void testFindAllStudentByCourseId_noEntities_responseOk() throws Exception {
        //GIVEN
        when(studentServiceMock.findAllStudentWithSameIdCourse(anyLong())).thenReturn(Collections.emptyList());
        //WHEN & THEN
        mockMvc.perform(get(DataProviderStudent.ENDPOINT_FIND_ALL_STUDENT_BY_COURSE_ID)
                        .param("idCourse", String.valueOf(1L)))
                .andExpect(status().isNoContent());

        verify(studentServiceMock,times(1)).findAllStudentWithSameIdCourse(anyLong());
    }

    @Test
    public void testCreateStudent_responseOk() throws Exception {
        //GIVEN
        when(studentServiceMock.createStudent(any(Student.class))).thenReturn(DataProviderStudent.getStudent());
        //WHEN & THEN
        mockMvc.perform(post(DataProviderStudent.ENDPOINT_CREATE_STUDENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(DataProviderStudent.getJsonRequest()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Rob"))
                .andExpect(jsonPath("$.lastName").value("Gordon"))
                .andExpect(jsonPath("$.email").value("rob@gmail.com"))
                .andExpect(jsonPath("$.idCourse").value(1L));

        verify(studentServiceMock, times(1)).createStudent(any(Student.class));
    }

    @Test
    public void testUpdateStudent_foundEntity_responseOk() throws Exception {
        //GIVEN
        Student student = DataProviderStudent.getStudent();
        when(studentServiceMock.updateStudent(any(Student.class))).thenReturn(Optional.of(student));
        //WHEN & THEN
        mockMvc.perform(put(DataProviderStudent.ENDPOINT_UPDATE_STUDENT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(DataProviderStudent.getJsonRequest()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Rob"))
                .andExpect(jsonPath("$.lastName").value("Gordon"))
                .andExpect(jsonPath("$.email").value("rob@gmail.com"))
                .andExpect(jsonPath("$.idCourse").value(1L));

        verify(studentServiceMock,times(1)).updateStudent(any(Student.class));
    }

    @Test
    public void testUpdateStudent_notFoundEntity_responseNotFound() throws Exception {
        //GIVEN
        when(studentServiceMock.updateStudent(any(Student.class))).thenReturn(Optional.empty());
        //WHEN & THEN
        mockMvc.perform(put(DataProviderStudent.ENDPOINT_UPDATE_STUDENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DataProviderStudent.getJsonRequest()))
                .andExpect(status().isNotFound());

        verify(studentServiceMock,times(1)).updateStudent(any(Student.class));
    }

    @Test
    public void testDeleteById_existingEntity_responseAccepted() throws Exception {
        //GIVEN
        doNothing().when(studentServiceMock).deleteById(anyLong());
        //WHEN & THEN
        mockMvc.perform(delete(DataProviderStudent.ENDPOINT_DELETE_BY_ID))
                .andExpect(status().isAccepted());

        verify(studentServiceMock, times(1)).deleteById(anyLong());
    }

    @Test
    public void testDeleteById_EntityNotFound_responseNotFound() throws Exception {
        //GIVEN
        doThrow(EntityNotFoundException.class).when(studentServiceMock).deleteById(anyLong());
        //WHEN & THEN
        mockMvc.perform(delete(DataProviderStudent.ENDPOINT_DELETE_BY_ID))
                .andExpect(status().isNotFound());
        verify(studentServiceMock, times(1)).deleteById(anyLong());
    }

    @Test
    public void testDeleteAll_thereAreEntities_responseAccepted() throws Exception {
        //GIVEN
        doNothing().when(studentServiceMock).deleteAll();
        //WHEN & THEN
        mockMvc.perform(delete(DataProviderStudent.ENDPOINT_DELETE_All))
                .andExpect(status().isAccepted());
        verify(studentServiceMock,times(1)).deleteAll();
    }

    @Test
    public void testDeleteAll_NoEntities_responseAccepted() throws Exception {
        //GIVEN
        doThrow(EntityNotFoundException.class).when(studentServiceMock).deleteAll();
        //WHEN & THEN
        mockMvc.perform(delete(DataProviderStudent.ENDPOINT_DELETE_All))
                .andExpect(status().isNoContent());
        verify(studentServiceMock,times(1)).deleteAll();
    }
}
