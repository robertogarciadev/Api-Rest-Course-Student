package com.microservice.course;

import com.microservice.course.entities.Course;
import com.microservice.course.repository.CourseRepository;
import com.microservice.course.service.CourseServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
        "eureka.client.enabled=false"  // Deshabilita el cliente de Eureka para este test
})
public class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepositoryMock; // Simulación del repositorio

    @InjectMocks
    private CourseServiceImpl courseService; // Inyección del servicio a probar

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks y la inyección de dependencias
    }

    @Test
    public void testFindById_existing_returnObject(){

        //Given
        when(courseRepositoryMock.findById(anyLong())).thenReturn(Optional.of(DataProvider.courseProvider()));
        //When
        Optional<Course> courseDb = courseService.findById(anyLong());
        //Then
        assertTrue(courseDb.isPresent());
        assertEquals(1L, courseDb.get().getId());
        assertEquals("Antonio", courseDb.get().getTeacher());
        verify(courseRepositoryMock, times(1)).findById(anyLong());
    }

    @Test
    public void testFindById_nonExisting_returnOptionalEmpty(){
        //GIVE
        when(courseRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());
        //WHEN
        Optional<Course> course = courseService.findById(anyLong());
        //THEN
        assertFalse(course.isPresent());
        verify(courseRepositoryMock, times(1)).findById(anyLong());
    }

    @Test
    public void testCreateCourse(){

        //Given
        when(courseRepositoryMock.save(any(Course.class))).thenReturn(DataProvider.courseProvider2());
        //When
        Course courseCreated = courseService.create(DataProvider.courseProvider2());
        //Then
        assertNotNull(courseCreated);
        assertEquals(3L, courseCreated.getId());
        assertEquals("Fernando", courseCreated.getTeacher());
        verify(courseRepositoryMock, times(1)).save(any(Course.class));
    }

    @Test
    public void testUpdateCourse_existingObject(){

        //GIVEN
        when(courseRepositoryMock.findById(anyLong())).thenReturn(Optional.of(DataProvider.courseProviderUpdate()));
        when(courseRepositoryMock.save(any(Course.class))).thenReturn(DataProvider.courseProviderUpdate());

        //WHEN
        Optional<Course> courseUpdated = courseService.updateCourse(DataProvider.courseProviderUpdate());

        //THEN
        assertTrue(courseUpdated.isPresent());
        assertEquals(5L, courseUpdated.get().getId());
        assertEquals("Marta", courseUpdated.get().getTeacher());
        verify(courseRepositoryMock, times(1)).findById(anyLong());
        verify(courseRepositoryMock, times(1)).save(any(Course.class));
    }

    @Test
    public void testUpdateCourse_nonExistingObject(){

        //GIVEN
        when(courseRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());
        when(courseRepositoryMock.save(any(Course.class))).thenReturn(DataProvider.courseProviderUpdate());
        //WHEN
        Optional<Course> courseUpdated = courseService.updateCourse(DataProvider.courseProviderUpdate());
        //THEN
        assertFalse(courseUpdated.isPresent());
        verify(courseRepositoryMock, times(1)).findById(anyLong());
        verify(courseRepositoryMock, never()).save(any(Course.class));
    }

    @Test
    public void testFindAllCourses_listNonEmpty_callFindAll(){
        //GIVEN
        when(courseRepositoryMock.findAll()).thenReturn(DataProvider.listCoursesProvider());
        //WHEN
        List<Course> allCourses = courseService.findAllCourse();
        //THEN
        assertFalse(allCourses.isEmpty());
        assertEquals(3, allCourses.size());
        assertEquals(1L, allCourses.get(0).getId());
        assertEquals("Paco", allCourses.get(0).getTeacher());
        verify(courseRepositoryMock, times(2)).findAll();
    }

    @Test
    public void testFindAllCourses_listEmpty_throwEntityNotFoundException(){
        //GIVEN
        when(courseRepositoryMock.findAll()).thenReturn(Collections.emptyList());
        //WHEN
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->{
            courseService.findAllCourse();
        });
        //THEN
        assertEquals("List empty", exception.getMessage());
        verify(courseRepositoryMock, times(1)).findAll();
    }

    @Test
    public void testDelete_existingObject_callDeleteById(){

        //GIVEN
        when(courseRepositoryMock.findById(anyLong())).thenReturn(Optional.of(DataProvider.courseProvider()));
        //WHEN
        courseService.delete(anyLong());
        //THEN
        verify(courseRepositoryMock, times(1)).findById(anyLong());
        verify(courseRepositoryMock, times(1)).deleteById(anyLong());

    }

    @Test
    public void testDelete_nonExistingObject_throwEntityNotFoundException(){
        //GIVEN
        when(courseRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());
        //WHEN
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, ()->{
            courseService.delete(anyLong());
        });
        //THEN
        assertEquals("Non existing object", exception.getMessage());
        verify(courseRepositoryMock, times(1)).findById(anyLong());
        verify(courseRepositoryMock, never()).deleteById(anyLong());
    }

    @Test
    public void testDeleteAll_listNoEmpty_callDeleteAll(){

        //GIVEN
        when(courseRepositoryMock.findAll()).thenReturn(DataProvider.listCoursesProvider());
        //WHEN
        courseService.deleteAll();
        //THEN
        verify(courseRepositoryMock, times(1)).findAll();
        verify(courseRepositoryMock, times(1)).deleteAll();

    }

    @Test
    public void testDeleteAll_listEmpty_throwsException(){
        //GIVEN
        when(courseRepositoryMock.findAll()).thenReturn(Collections.emptyList());
        //WHEN
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, ()->{
            courseService.deleteAll();
        });
        //THEN
        assertEquals("List empty", exception.getMessage());
        verify(courseRepositoryMock, never()).deleteAll();
        verify(courseRepositoryMock, times(1)).findAll();

    }
}
