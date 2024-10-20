package com.microservice.course;


import com.microservice.course.controller.CourseController;
import com.microservice.course.entities.Course;
import com.microservice.course.service.CourseServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.http.MediaType;

import javax.xml.crypto.Data;

@SpringBootTest
@TestPropertySource(properties = {
        "eureka.client.enabled=false"  // Deshabilita el cliente de Eureka para este test
})
public class CourseControllerTest {

    @Mock
    private CourseServiceImpl courseServiceMock;

    @InjectMocks
    private CourseController courseController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();  // Inicializa MockMvc con el controlador
    }

    @Test
    public void findById_existingEntity_responseOk() throws Exception {
        //GIVEN
        when(courseServiceMock.findById(anyLong())).thenReturn(Optional.of(DataProvider.courseProvider()));
        //WHEN y THEN
        mockMvc.perform(get(DataProvider.ENDPOINT_FIND_BY_ID).param("id", String.valueOf(anyLong())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.teacher").value("Antonio"));

        verify(courseServiceMock, times(1)).findById(anyLong());
    }

    @Test
    public void findById_nonExistingEntity_responseNotFound() throws Exception {
        //GIVEN
        when(courseServiceMock.findById(anyLong())).thenReturn(Optional.empty());
        //WHEN & THEN
        mockMvc.perform(get(DataProvider.ENDPOINT_FIND_BY_ID).param("id", String.valueOf(anyLong())))
                .andExpect(status().isNotFound());
        verify(courseServiceMock, times(1)).findById(anyLong());
    }

    @Test
    public void findAllCourses_listNonEmpty_responseOk() throws Exception {
        //GIVEN
        when(courseServiceMock.findAllCourse()).thenReturn(DataProvider.listCoursesProvider());
        //WHEN & THEN
        mockMvc.perform(get(DataProvider.ENDPOINT_FIND_ALL_COURSES))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].teacher").value("Paco"));
        verify(courseServiceMock, times(2)).findAllCourse();
    }

    @Test
    public void findAllCourses_listEmpty_responseNoContent() throws Exception{
        //GIVEN
        when(courseServiceMock.findAllCourse()).thenReturn(Collections.emptyList());
        //WHEN & THEN
        mockMvc.perform(get(DataProvider.ENDPOINT_FIND_ALL_COURSES))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.length()").doesNotExist());
        verify(courseServiceMock, times(1)).findAllCourse();

    }

    @Test
    public void createCourse_createdSuccessfully_responseCreated() throws Exception{
        //GIVEN
        when(courseServiceMock.create(any(Course.class))).thenReturn(DataProvider.courseProvider());
        //WHEN THEN
        mockMvc.perform(post(DataProvider.ENDPOINT_CREATE_COURSE)
                .contentType(MediaType.APPLICATION_JSON) //Define el contenido dela petición como JSON
                .content("{\"teacher\":\"Antonio\"}")) //Define el contenido en la petición
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //Verifica el contenido como JSON en la respuesta
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.teacher").value("Antonio")); //Contenido del Body
        verify(courseServiceMock, times(1)).create(any(Course.class));
    }

    @Test
    public void updateCourse_existingCourse_responseAccepted() throws Exception {
        //GIVEN
        when(courseServiceMock.updateCourse(any(Course.class))).thenReturn(Optional.of(DataProvider.courseProvider()));
        //WHEN & THEN
        mockMvc.perform(put(DataProvider.ENDPOINT_UPDATE_COURSE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"1\"," +
                        "\"teacher\":\"Antonio\"}"))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.teacher").value("Antonio"));

        verify(courseServiceMock, times(1)).updateCourse(any(Course.class));
    }
    @Test
    public void updateCourse_notExistingCourse_responseNotFound() throws Exception {
        //GIVEN
        when(courseServiceMock.updateCourse(any(Course.class))).thenReturn(Optional.empty());
        //WHEN & THEN
        mockMvc.perform(put(DataProvider.ENDPOINT_UPDATE_COURSE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\"," +
                                "\"teacher\":\"Antonio\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void deleteCourse_existingCourse_responseAccepted() throws Exception {
        //GIVEN
        doNothing().when(courseServiceMock).delete(anyLong());
        //WHEN & THEN
        mockMvc.perform(delete(DataProvider.ENDPOINT_DELETE_COURSE).param("id", String.valueOf(anyLong())))
                .andExpect(status().isAccepted());

        verify(courseServiceMock, times(1)).delete(anyLong());
    }
    @Test
    public void deleteCourse_nonExistingCourse_responseNotFound() throws Exception {
        //GIVEN
        doThrow(EntityNotFoundException.class).when(courseServiceMock).delete(anyLong());
        //WHEN & THEN
        mockMvc.perform(delete(DataProvider.ENDPOINT_DELETE_COURSE).param("id", String.valueOf(anyLong())))
                .andExpect(status().isNotFound());

        verify(courseServiceMock, times(1)).delete(anyLong());
    }

    @Test
    public void deleteAllCourses_existingCourses_responseAccepted() throws Exception{
        //GIVEN
        doNothing().when(courseServiceMock).deleteAll();
        //WHEN & THEN
        mockMvc.perform(delete(DataProvider.ENDPOINT_DELETE_ALL_COURSE))
                .andExpect(status().isAccepted());
        verify(courseServiceMock, times(1)).deleteAll();
    }
    @Test
    public void deleteAllCourses_nonExistingCourses_responseNoContent() throws Exception {
        //GIVEN
        doThrow(EntityNotFoundException.class).when(courseServiceMock).deleteAll();
        //WHEN & THEN
        mockMvc.perform(delete(DataProvider.ENDPOINT_DELETE_ALL_COURSE))
                .andExpect(status().isNoContent());
        verify(courseServiceMock, times(1)).deleteAll();
    }

}
