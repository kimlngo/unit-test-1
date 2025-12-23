package com.kimlngo.component;

import com.kimlngo.component.dao.ApplicationDao;
import com.kimlngo.component.models.CollegeStudent;
import com.kimlngo.component.models.StudentGrades;
import com.kimlngo.component.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MockAnnotationTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    CollegeStudent studentOne;

    @Autowired
    StudentGrades grades;

    //@Mock
    @MockitoBean
    private ApplicationDao applicationDao;

    //@InjectMocks
    @Autowired
    private ApplicationService applicationService;

    @BeforeEach
    public void beforeEach() {
        studentOne.setFirstname("Thomas");
        studentOne.setLastname("Ngo");
        studentOne.setEmailAddress("kimlngo@paypal.com");
        studentOne.setStudentGrades(grades);
    }

    @Test
    @DisplayName("When & Verify")
    public void testAddGradeResults() {
        //setup
        when(applicationDao.addGradeResultsForSingleClass(studentOne.getStudentGrades()
                                                                    .getMathGradeResults())).thenReturn(100.00);

        //execution & assertion
        assertEquals(100.00, applicationService.addGradeResultsForSingleClass(studentOne.getStudentGrades()
                                                                                        .getMathGradeResults()));

        //verify (that mocked method was actually called)
        verify(applicationDao).addGradeResultsForSingleClass(studentOne.getStudentGrades()
                                                                       .getMathGradeResults());

        verify(applicationDao, times(1)).addGradeResultsForSingleClass(studentOne.getStudentGrades()
                                                                                 .getMathGradeResults());
    }

    @Test
    @DisplayName("Find Gpa")
    public void testFindGPA() {
        //setup
        when(applicationDao.findGradePointAverage(studentOne.getStudentGrades()
                                                            .getMathGradeResults())).thenReturn(88.8);

        //execution & assertion
        assertEquals(88.8, applicationService.findGradePointAverage(studentOne.getStudentGrades()
                                                                              .getMathGradeResults()));

        //verify
        verify(applicationDao).findGradePointAverage(studentOne.getStudentGrades()
                                                               .getMathGradeResults());

        verify(applicationDao, times(1)).findGradePointAverage(studentOne.getStudentGrades()
                                                                         .getMathGradeResults());
    }

    @Test
    @DisplayName("Check for not null")
    public void testAssertNotNull() {
        when(applicationDao.checkNull(grades.getMathGradeResults())).thenReturn(true);

        assertNotNull(applicationService.checkNull(studentOne.getStudentGrades()
                                                             .getMathGradeResults()));

        verify(applicationDao).checkNull(grades.getMathGradeResults());
    }

    @Test
    @DisplayName("Test with throw exception")
    public void testThrowException() {
        CollegeStudent student = context.getBean("collegeStudent", CollegeStudent.class);

        doThrow(new RuntimeException()).when(applicationDao)
                                       .checkNull(student);
        assertThrows(RuntimeException.class, () -> applicationService.checkNull(student));

        verify(applicationDao, times(1)).checkNull(student);
    }

    @Test
    @DisplayName("Multiple Stubbing")
    public void testMultipleStubbing() {
        CollegeStudent student = context.getBean("collegeStudent", CollegeStudent.class);

        when(applicationDao.checkNull(student)).thenThrow(new RuntimeException())
                                               .thenReturn("Do not throw exception second time");

        assertThrows(RuntimeException.class, () -> applicationService.checkNull(student));
        assertEquals("Do not throw exception second time", applicationService.checkNull(student));

        verify(applicationDao, times(2)).checkNull(student);
    }
}
