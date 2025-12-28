package com.kimlngo.springmvc;

import com.kimlngo.springmvc.models.*;
import com.kimlngo.springmvc.repository.HistoryGradeDAO;
import com.kimlngo.springmvc.repository.MathGradeDAO;
import com.kimlngo.springmvc.repository.ScienceGradeDAO;
import com.kimlngo.springmvc.repository.StudentDAO;
import com.kimlngo.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

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
    private MathGradeDAO mathGradeDao;

    @Autowired
    private HistoryGradeDAO historyGradeDao;

    @Autowired
    private ScienceGradeDAO scienceGradeDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //SQL Queries
    @Value("${sql.script.create.student}")
    private String createStudentSQL;

    @Value("${sql.script.create.math.grade}")
    private String createMathGradeSQL;

    @Value("${sql.script.create.science.grade}")
    private String createScienceGradeSQL;

    @Value("${sql.script.create.history.grade}")
    private String createHistoryGradeSQL;

    @Value("${sql.script.delete.student}")
    private String deleteStudentSQL;

    @Value("${sql.script.delete.math.grade}")
    private String deleteMathGradeSQL;

    @Value("${sql.script.delete.science.grade}")
    private String deleteScienceGradeSQL;

    @Value("${sql.script.delete.history.grade}")
    private String deleteHistoryGradeSQL;

    @BeforeEach
    public void setupBeforeEach() {
        jdbcTemplate.execute(createStudentSQL);

        jdbcTemplate.execute(createMathGradeSQL);
        jdbcTemplate.execute(createScienceGradeSQL);
        jdbcTemplate.execute(createHistoryGradeSQL);
    }

    @AfterEach
    public void tearDownAfterEach() {
        jdbcTemplate.execute(deleteStudentSQL);
        jdbcTemplate.execute(deleteMathGradeSQL);
        jdbcTemplate.execute(deleteScienceGradeSQL);
        jdbcTemplate.execute(deleteHistoryGradeSQL);
    }

    @Test
    public void testCreateStudentService() {
        studentService.createStudent("ABC", "DEF", "abcdef@gmail.com");

        CollegeStudent student = studentDao.findByEmailAddress("abcdef@gmail.com");

        assertEquals("abcdef@gmail.com", student.getEmailAddress());
    }

    @Test
    public void testIsStudentNullCheck() {
        assertTrue(studentService.isStudentExist(1));
        assertFalse(studentService.isStudentExist(0));
    }

    @Test
    public void deleteStudentService() {
        Optional<CollegeStudent> studentOne = studentDao.findById(1);
        Optional<MathGrade> mathGradeOptional = mathGradeDao.findById(1);
        Optional<ScienceGrade> scienceGradeOptional = scienceGradeDao.findById(1);
        Optional<HistoryGrade> historyGradeOptional = historyGradeDao.findById(1);

        assertTrue(studentOne.isPresent());
        assertTrue(mathGradeOptional.isPresent());
        assertTrue(scienceGradeOptional.isPresent());
        assertTrue(historyGradeOptional.isPresent());

        studentService.deleteStudent(1);

        Optional<CollegeStudent> studentRecheckOpt = studentDao.findById(1);
        mathGradeOptional = mathGradeDao.findById(1);
        scienceGradeOptional = scienceGradeDao.findById(1);
        historyGradeOptional = historyGradeDao.findById(1);

        assertFalse(studentRecheckOpt.isPresent());
        assertFalse(mathGradeOptional.isPresent());
        assertFalse(scienceGradeOptional.isPresent());
        assertFalse(historyGradeOptional.isPresent());
    }

    @Test
    @Sql("/insertData.sql") //BeforeEach will execute first
    public void testGetGradeBookService() {
        List<CollegeStudent> studentList = studentService.getGradebook();
        assertEquals(5, studentList.size());
    }

    @Test
    public void testCreateGradeService() {

        // create math grade for student
        assertTrue(studentService.createGrade(80.5, 1, GradeType.MATH));
        assertTrue(studentService.createGrade(90.5, 1, GradeType.SCIENCE));
        assertTrue(studentService.createGrade(75.2, 1, GradeType.HISTORY));

        // get all the grade from dao
        List<Grade> mathGrades = mathGradeDao.findGradeByStudentId(1);
        List<Grade> scienceGrades = scienceGradeDao.findGradeByStudentId(1);
        List<Grade> historyGrades = historyGradeDao.findGradeByStudentId(1);

        // verify that each subject has two grades
        assertEquals(2, mathGrades.size());
        assertEquals(2, scienceGrades.size());
        assertEquals(2, historyGrades.size());
    }

    @Test
    public void testCreateGradeWithInvalidData() {
        //out-of-range grade
        assertFalse(studentService.createGrade(-1, 1, GradeType.MATH));
        assertFalse(studentService.createGrade(105, 1, GradeType.MATH));

        //non-existing student
        assertFalse(studentService.createGrade(90, 10, GradeType.MATH));
    }

    @Test
    public void testDeleteGrade() {
        int studentId = studentService.deleteGrade(1, GradeType.MATH);
        assertEquals(1, studentId);

        studentId = studentService.deleteGrade(1, GradeType.SCIENCE);
        assertEquals(1, studentId);

        studentId = studentService.deleteGrade(1, GradeType.HISTORY);
        assertEquals(1, studentId);
    }

    @Test
    public void testDeleteGradeWithInvalidId() {
        int studentId = studentService.deleteGrade(0, GradeType.MATH);
        assertEquals(0, studentId);
    }

    @Test
    public void testGetStudentInformation() {
        GradebookCollegeStudent student = studentService.getStudentInformation(1);

        assertNotNull(student);
        assertEquals(1, student.getId());

        assertEquals("test1", student.getFirstname());
        assertEquals("test1", student.getLastname());
        assertEquals("test1@gmail.com", student.getEmailAddress());

        StudentGrades studentGrades = student.getStudentGrades();
        assertEquals(1, studentGrades
                .getMathGradeResults()
                .size());
        assertEquals(1, studentGrades
                .getScienceGradeResults()
                .size());
        assertEquals(1, studentGrades
                .getHistoryGradeResults()
                .size());
    }

    @Test
    public void testGetStudentInfoNonExisting() {
        assertNull(studentService.getStudentInformation(0));
    }
}
