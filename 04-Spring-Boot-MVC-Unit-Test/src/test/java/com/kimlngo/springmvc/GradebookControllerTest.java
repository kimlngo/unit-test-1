package com.kimlngo.springmvc;

import com.kimlngo.springmvc.models.CollegeStudent;
import com.kimlngo.springmvc.models.GradebookCollegeStudent;
import com.kimlngo.springmvc.models.MathGrade;
import com.kimlngo.springmvc.repository.MathGradeDAO;
import com.kimlngo.springmvc.repository.StudentDAO;
import com.kimlngo.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradebookControllerTest {

    private static final String DELETE_PATH = "/grades/{id}/{gradeType}";
    private static MockHttpServletRequest request;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentAndGradeService studentCreateServiceMock;

    @Autowired
    private StudentDAO studentDao;

    @Autowired
    private MathGradeDAO mathGradeDAO;

    @Autowired
    private StudentAndGradeService studentService;

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

    @BeforeAll
    public static void setupBeforeAll() {
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "Ben");
        request.setParameter("lastname", "Afflect");
        request.setParameter("emailAddress", "ben.afflect@gmail.com");
    }

    @BeforeEach
    public void setupBeforeEach() {
        jdbc.execute(createStudentSQL);

        jdbc.execute(createMathGradeSQL);
        jdbc.execute(createScienceGradeSQL);
        jdbc.execute(createHistoryGradeSQL);
    }

    @AfterEach
    public void cleanUpAfterEach() {
        jdbc.execute(deleteStudentSQL);
        jdbc.execute(deleteMathGradeSQL);
        jdbc.execute(deleteScienceGradeSQL);
        jdbc.execute(deleteHistoryGradeSQL);
    }

    @Test
    public void testGetStudentHttpRequest() throws Exception {
        CollegeStudent stdOne = new GradebookCollegeStudent("first", "last", "first_last@gmail.com");
        CollegeStudent stdTwo = new GradebookCollegeStudent("one", "two", "one_two@gmail.com");

        List<CollegeStudent> stdList = List.of(stdOne, stdTwo);
        when(studentCreateServiceMock.getGradebook()).thenReturn(stdList);

        assertIterableEquals(stdList, studentCreateServiceMock.getGradebook());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                                     .andExpect(status().isOk())
                                     .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");
    }

    @Test
    public void testCreateStudentHttpRequest() throws Exception {
        CollegeStudent studentOne = new GradebookCollegeStudent("Matt", "Damon", "matt.damon@gmail.com");
        List<CollegeStudent> studentList = List.of(studentOne);

        when(studentCreateServiceMock.getGradebook()).thenReturn(studentList);

        assertIterableEquals(studentList, studentCreateServiceMock.getGradebook());

        MvcResult mvcResult = this.mockMvc.perform(post("/")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .param("firstname", request.getParameterValues("firstname"))
                                          .param("lastname", request.getParameterValues("lastname"))
                                          .param("emailAddress", request.getParameterValues("emailAddress")))
                                          .andExpect(status().isOk())
                                          .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");

        CollegeStudent studentBen = studentDao.findByEmailAddress("ben.afflect@gmail.com");

        assertNotNull(studentBen);
    }

    @Test
    public void testDeleteStudent() throws Exception {
        assertTrue(studentDao.findById(1)
                             .isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}", 1))
                                     .andExpect(status().isOk())
                                     .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");
        assertFalse(studentDao.findById(1)
                              .isPresent());
    }

    @Test
    public void testDeleteStudentHttpRequestError() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}", 0))
                                     .andExpect(status().isOk())
                                     .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void testGetStudentInformation() throws Exception {
        //check if student exist
        assertTrue(studentDao.findById(1)
                             .isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}", 1))
                                     .andExpect(status().isOk())
                                     .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "studentInformation");
    }

    @Test
    public void testGetStudentInformation_nonExistStudent() throws Exception {
        //check if student exist
        assertFalse(studentDao.findById(0)
                              .isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}", 0))
                                     .andExpect(status().isOk())
                                     .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void testCreateValidGradeHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1)
                             .isPresent());
        GradebookCollegeStudent student = studentService.getStudentInformation(1);

        assertEquals(1, student.getStudentGrades()
                               .getMathGradeResults()
                               .size());

        MvcResult mvcResult = mockMvc.perform(post("/grades")
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .param("grade", "85.5")
                                             .param("gradeType", "math")
                                             .param("studentId", "1"))
                                     .andExpect(status().isOk())
                                     .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "studentInformation");

        student = studentService.getStudentInformation(1);
        assertEquals(2, student.getStudentGrades()
                               .getMathGradeResults()
                               .size());
    }

    @Test
    public void testCreateGradeOn_InvalidStudent() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/grades")
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .param("grade", "85.5")
                                             .param("gradeType", "math")
                                             .param("studentId", "0"))
                                     .andExpect(status().isOk())
                                     .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void testCreateGradeOn_InvalidSubject() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/grades")
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .param("grade", "85.5")
                                             .param("gradeType", "literature")
                                             .param("studentId", "1"))
                                     .andExpect(status().isOk())
                                     .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void testDeleteAValidGradeHttpRequest() throws Exception {
        Optional<MathGrade> mathGrade = mathGradeDAO.findById(1);
        assertTrue(mathGrade.isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(DELETE_PATH, 1, "math"))
                                     .andExpect(status().isOk())
                                     .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "studentInformation");

        mathGrade = mathGradeDAO.findById(1);
        assertFalse(mathGrade.isPresent());
    }

    @Test
    public void testDeleteAnInvalidGradeHttpRequest() throws Exception {
        Optional<MathGrade> mathGradeOptional = mathGradeDAO.findById(2);

        assertFalse(mathGradeOptional.isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(DELETE_PATH, 2, "science"))
                                     .andExpect(status().isOk())
                                     .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void testDeleteInvalidSubject() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(DELETE_PATH, 1, "literature"))
                                     .andExpect(status().isOk())
                                     .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }
}
