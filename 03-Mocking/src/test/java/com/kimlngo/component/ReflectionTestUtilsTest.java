package com.kimlngo.component;

import com.kimlngo.component.models.CollegeStudent;
import com.kimlngo.component.models.StudentGrades;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

@SpringBootTest
public class ReflectionTestUtilsTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    CollegeStudent student;

    @Autowired
    StudentGrades grades;

    @BeforeEach
    public void studentBeforeEach() {
        student.setFirstname("ABC");
        student.setLastname("DEF");
        student.setEmailAddress("abcdef@gmail.com");
        student.setStudentGrades(grades);

        ReflectionTestUtils.setField(student, "id", 1);
        ReflectionTestUtils.setField(student, "studentGrades", new StudentGrades(
                List.of(100.0, 85.0, 76.50, 91.75)
        ));
    }

    @Test
    public void testGetPrivateField() {
        Assertions.assertEquals(1, ReflectionTestUtils.getField(student, "id"));
    }

    @Test
    public void testInvokePrivateMethod() {
        Assertions.assertEquals("ABC 1", ReflectionTestUtils.invokeMethod(student, "getFirstNameAndId"));
    }
}
