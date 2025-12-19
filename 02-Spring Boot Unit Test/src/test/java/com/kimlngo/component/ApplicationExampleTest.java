package com.kimlngo.component;

import com.kimlngo.component.models.CollegeStudent;
import com.kimlngo.component.models.StudentGrades;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.List;

//@SpringBootTest(classes = MvcTestingExampleApplication.class) //for different package name, i.e com.kimlngo.component vs com.kimlngo.test
@SpringBootTest //only this is enough if package names are the same
public class ApplicationExampleTest {
    private static int count = 0;

    @Value("${info.school.name}")
    private String schoolName;

    @Value("${info.app.name}")
    private String appName;

    @Value("${info.app.description}")
    private String appDescription;

    @Value("${info.app.version}")
    private String appVersion;

    @Autowired
    private CollegeStudent student;

    @Autowired
    private StudentGrades grades;

    @Autowired
    private ApplicationContext context;

    @BeforeEach
    public void beforeMethod() {
        count++;
        System.out.printf("Testing %s which is %s Version %s. Execution of test method %d", appName, appDescription, appVersion, count);

        student.setFirstname("Thomas");
        student.setLastname("Ngo");
        student.setEmailAddress("kimlngo@paypal.com");
        grades.setMathGradeResults(List.of(100.0, 95.2, 97.5));
        student.setStudentGrades(grades);
    }

    @Test
    public void basicTest() {
    }

    @Test
    @DisplayName("Add grades results for students")
    public void testAddResultsForStudentGrades() {
        Assertions.assertEquals(292.7, grades.addGradeResultsForSingleClass(student.getStudentGrades()
                                                                                   .getMathGradeResults()));
    }

    @Test
    @DisplayName("Add grades results for students not equals")
    public void testAddResultsForStudentGrades_NotEqual() {
        Assertions.assertNotEquals(100, grades.addGradeResultsForSingleClass(student.getStudentGrades()
                                                                                    .getMathGradeResults()));
    }

    @Test
    public void testGradeGreater() {
        Assertions.assertTrue(grades.isGradeGreater(90, 89));
    }

    @Test
    public void testGradeGreater_False() {
        Assertions.assertFalse(grades.isGradeGreater(85, 89));
    }

    @Test
    public void testForNullValue() {
        Assertions.assertNotNull(grades.checkNull(student.getStudentGrades()
                                                         .getMathGradeResults()));
    }

    @Test
    public void testCreateStudentWithoutGrades() {
        CollegeStudent studentTwo = context.getBean("collegeStudent", CollegeStudent.class);
        studentTwo.setFirstname("An");
        studentTwo.setLastname("Tran");
        studentTwo.setEmailAddress("antran@gmail.com");
        Assertions.assertNotNull(studentTwo.getFirstname());
        Assertions.assertNotNull(studentTwo.getLastname());
        Assertions.assertNotNull(studentTwo.getEmailAddress());
        Assertions.assertNull(studentTwo.getStudentGrades());
    }

    @Test
    @DisplayName("Verify students are prototype")
    public void verifyStudentPrototype() {
        CollegeStudent studentTwo = context.getBean("collegeStudent", CollegeStudent.class);

        Assertions.assertNotSame(student, studentTwo);
    }

    @Test
    @DisplayName("Find Grade Point Average")
    public void testFindGradePointAverage() {
        Assertions.assertAll("Testing all assertEquals",
                () -> Assertions.assertEquals(292.7, grades.addGradeResultsForSingleClass(student.getStudentGrades()
                                                                                                 .getMathGradeResults())),
                () -> Assertions.assertEquals(97.57, grades.findGradePointAverage(student.getStudentGrades()
                                                                                         .getMathGradeResults()))

        );
    }
}
