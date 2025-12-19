package com.kimlngo.test;

import com.kimlngo.component.MvcTestingExampleApplication;
import com.kimlngo.component.dao.ApplicationDao;
import com.kimlngo.component.models.CollegeStudent;
import com.kimlngo.component.models.StudentGrades;
import com.kimlngo.component.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = MvcTestingExampleApplication.class)
public class MockAnnotationTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    CollegeStudent studentOne;

    @Autowired
    StudentGrades grades;

    @Mock
    private ApplicationDao applicationDao;

    @InjectMocks
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
}
