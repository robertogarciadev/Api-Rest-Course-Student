package com.microservice.student;

import com.microservice.student.entities.Student;
import com.microservice.student.repository.StudentRepository;
import com.microservice.student.service.StudentServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.xml.crypto.Data;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "eureka.client.enabled=false"  // Deshabilita el cliente de Eureka para este test
})
public class StudentServiceImplTest {

    @Mock
    StudentRepository studentRepositoryMock;

    @InjectMocks
    StudentServiceImpl studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById_entityFound_returnOptionalFull(){
        //GIVEN
            when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.of(DataProviderStudent.getStudent()));
        //WHEN
            Optional<Student> studentDb = studentService.findById(1L);
        //THEN
            assertTrue(studentDb.isPresent());
            assertEquals(1L, studentDb.get().getId());
            assertEquals("Rob", studentDb.get().getName());
            assertEquals("Gordon", studentDb.get().getLastName());
            assertEquals("rob@gmail.com", studentDb.get().getEmail());
            assertEquals(1L, studentDb.get().getIdCourse());
            verify(studentRepositoryMock, times(1)).findById(anyLong());
    }

    @Test
    public void testFindById_entityNotFound_returnOptionalEmpty(){
        //GIVEN
        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());
        //WHEN
        Optional<Student> studentDb =  studentService.findById(1L);
        //THEN
        assertFalse(studentDb.isPresent());
        verify(studentRepositoryMock, times(1)).findById(anyLong());
    }

    @Test
    public void testFindAllStudent_existingStudent_returnListStudent(){
        //GIVEN
            when(studentRepositoryMock.findAll()).thenReturn(DataProviderStudent.getListStudent());
        //WHEN
        List<Student> allStudent = studentService.findAllStudent();
        //THEN
        assertFalse(allStudent.isEmpty());
        assertEquals(3, allStudent.size());
        assertEquals(1L, allStudent.get(0).getId());
        assertEquals(1L, allStudent.get(0).getIdCourse());
        assertEquals(2L, allStudent.get(1).getId());
        assertEquals("Marta", allStudent.get(1).getName());
        assertEquals(3L, allStudent.get(2).getId());
        assertEquals("ampa@gmail.com", allStudent.get(2).getEmail());
        verify(studentRepositoryMock, times(2)).findAll();
    }

    @Test
    public void testFindAllStudent_nonExistingStudent_throwException(){
        //GIVEN
        when(studentRepositoryMock.findAll()).thenReturn(Collections.emptyList());
        //WHEN
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                ()-> studentService.findAllStudent());
        //THEN
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("List empty", exception.getMessage());
        verify(studentRepositoryMock, times(1)).findAll();
    }

    @Test
    public void testFindAllStudentByIdCourse_existingStudent_returnList(){
        //GIVEN
        when(studentRepositoryMock.findAllByIdCourse(anyLong())).thenReturn(DataProviderStudent.getListStudent());
        //WHEN
        List<Student> allStudentWithSameIdCourse = studentService.findAllStudentWithSameIdCourse(1L);
        //THEN
        assertFalse(allStudentWithSameIdCourse.isEmpty());
        assertEquals(3,allStudentWithSameIdCourse.size());
        assertEquals(1L, allStudentWithSameIdCourse.get(0).getId());
        verify(studentRepositoryMock, times(1)).findAllByIdCourse(anyLong());
    }

    @Test
    public void testFindAllStudentByIdCourse_nonExistingStudent_ReturnListEmpty(){
        //GIVEN
        when(studentRepositoryMock.findAllByIdCourse(anyLong())).thenReturn(Collections.emptyList());
        //WHEN
        List<Student> allStudentWithSameIdCourse = studentService.findAllStudentWithSameIdCourse(1L);
        //THEN
        assertTrue(allStudentWithSameIdCourse.isEmpty());
        verify(studentRepositoryMock, times(1)).findAllByIdCourse(anyLong());
    }

    @Test
    public void testCreateStudent_returnStudentCreated(){

        //GIVEN
        when(studentRepositoryMock.save(any(Student.class))).thenReturn(DataProviderStudent.getStudent());
        //WHEN
        Student newStudent = DataProviderStudent.getStudent();
        Student studentCreated = studentService.createStudent(newStudent);
        //THEN
        assertNotNull(studentCreated, "No debería ser nulo");
        assertEquals(1, studentCreated.getId());
        assertEquals("Rob", studentCreated.getName());
        assertEquals("Gordon", studentCreated.getLastName());
        assertEquals("rob@gmail.com", studentCreated.getEmail());
        assertEquals(1L, studentCreated.getIdCourse());
        verify(studentRepositoryMock, times(1)).save(any(Student.class));
    }

    @Test
    public void testUpdateStudent_foundStudent_returnStudentUpdated(){
        //GIVEN
        Student student = DataProviderStudent.getStudent();
        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.of(student));
        when(studentRepositoryMock.save(any(Student.class))).thenReturn(student);
        //WHEN
        Optional<Student> studentUpdate = studentService.updateStudent(student);
        //THEN
        assertTrue(studentUpdate.isPresent());
        assertNotNull(studentUpdate.get(), "No debería ser nulo");
        assertEquals(1, studentUpdate.get().getId());
        assertEquals("Rob", studentUpdate.get().getName());
        assertEquals("Gordon", studentUpdate.get().getLastName());
        assertEquals("rob@gmail.com", studentUpdate.get().getEmail());
        assertEquals(1L, studentUpdate.get().getIdCourse());
        verify(studentRepositoryMock, times(1)).save(any(Student.class));
        verify(studentRepositoryMock, times(1)).findById(anyLong());

    }

    @Test
    public void testUpdateStudent_notFoundStudent_returnOptionalEmpty(){
        //GIVEN
        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());
        //WHEN
        Student studentToUpdate = DataProviderStudent.getStudent();
        Optional<Student> studentUpdated = studentService.updateStudent(studentToUpdate);
        //THEN
        assertFalse(studentUpdated.isPresent());
        verify(studentRepositoryMock, times(1)).findById(anyLong());
        verify(studentRepositoryMock, never()).save(any(Student.class));
    }

    @Test
    public void testDeleteById_foundStudent_callDelete(){
        //GIVEN
        Student student = DataProviderStudent.getStudent();
        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.of(student));
        //WHEN
        studentService.deleteById(1L);
        //THEN
        verify(studentRepositoryMock, times(1)).findById(anyLong());
        verify(studentRepositoryMock, times(1)).deleteById(anyLong());
    }

    @Test
    public void testDeleteById_notFound_throwException(){
        //GIVEN
        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());
        //WHEN
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, ()->{
            studentService.deleteById(1L);
        });
        //THEN
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("Entity not found", exception.getMessage());
        verify(studentRepositoryMock, times(1)).findById(anyLong());
        verify(studentRepositoryMock, never()).deleteById(anyLong());
    }

    @Test
    public void testDeleteAll_thereAreEntities_callDeleteAll(){

        //GIVEN
        when(studentRepositoryMock.findAll()).thenReturn(DataProviderStudent.getListStudent());
        //WHEN
        studentService.deleteAll();
        //THEN
        verify(studentRepositoryMock, times(1)).findAll();
        verify(studentRepositoryMock, times(1)).deleteAll();
    }

    @Test
    public void testDeleteAll_noEntities_ThrowException(){

        //GIVEN
        when(studentRepositoryMock.findAll()).thenReturn(Collections.emptyList());
        //WHEN
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                ()-> {studentService.deleteAll();});
        //THEN
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("There aren´t entities", exception.getMessage());
        verify(studentRepositoryMock, times(1)).findAll();
        verify(studentRepositoryMock, never()).deleteAll();
    }
}
