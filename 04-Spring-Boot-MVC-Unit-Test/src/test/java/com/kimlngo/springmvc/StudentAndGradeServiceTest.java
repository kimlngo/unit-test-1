package com.kimlngo.springmvc;

import com.kimlngo.springmvc.models.CollegeStudent;
import com.kimlngo.springmvc.repository.StudentDAO;
import com.kimlngo.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application.properties")
public class StudentAndGradeServiceTest {

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDAO studentDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setupDatabase() {
        jdbcTemplate.execute("insert into student(firstname, lastname, email_address) " +
                "values ('test1', 'test1', 'test1@gmail.com')");
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbcTemplate.execute("delete from student");
        jdbcTemplate.execute("alter table student alter column id restart with 1");
    }

    @Test
    public void testCreateStudentService() {
        studentService.createStudent("ABC", "DEF", "abcdef@gmail.com");

        CollegeStudent student = studentDao.findByEmailAddress("abcdef@gmail.com");

        assertEquals("abcdef@gmail.com", student.getEmailAddress());
    }

    @Test
    public void testIsStudentNullCheck() {
        assertTrue(studentService.checkIfStudentIsNull(1));
        assertFalse(studentService.checkIfStudentIsNull(0));
    }

    @Test
    public void deleteStudentService() {
        Optional<CollegeStudent> studentOne = studentDao.findById(1);
        assertTrue(studentOne.isPresent());
        studentService.deleteStudent(1);

        Optional<CollegeStudent> studentRecheckOpt = studentDao.findById(1);
        assertFalse(studentRecheckOpt.isPresent());
    }

    @Test
    @Sql("/insertData.sql") //BeforeEach will execute first
    public void testGetGradeBookService() {
        Iterable<CollegeStudent> collegeStudentIterable = studentService.getGradebook();

        List<CollegeStudent> studentList = new ArrayList<>();
        collegeStudentIterable.iterator()
                              .forEachRemaining(studentList::add);

        assertEquals(5, studentList.size());
    }
}
