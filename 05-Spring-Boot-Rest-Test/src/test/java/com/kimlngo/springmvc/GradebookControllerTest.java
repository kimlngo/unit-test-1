package com.kimlngo.springmvc;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimlngo.springmvc.models.CollegeStudent;
import com.kimlngo.springmvc.models.MathGrade;
import com.kimlngo.springmvc.repository.HistoryGradesDao;
import com.kimlngo.springmvc.repository.MathGradesDao;
import com.kimlngo.springmvc.repository.ScienceGradesDao;
import com.kimlngo.springmvc.repository.StudentDao;
import com.kimlngo.springmvc.service.StudentAndGradeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@Transactional
public class GradebookControllerTest {

    private static MockHttpServletRequest request;

    @PersistenceContext
    private EntityManager entityManager;

    @Mock
    StudentAndGradeService studentCreateServiceMock;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradesDao mathGradeDao;

    @Autowired
    private ScienceGradesDao scienceGradeDao;

    @Autowired
    private HistoryGradesDao historyGradeDao;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private CollegeStudent student;

    @Value("${sql.script.create.student}")
    private String sqlAddStudent;

    @Value("${sql.script.create.math.grade}")
    private String sqlAddMathGrade;

    @Value("${sql.script.create.science.grade}")
    private String sqlAddScienceGrade;

    @Value("${sql.script.create.history.grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;

    public static final MediaType APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON;

    @BeforeAll
    public static void setupBeforeAll() {
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "Mike");
        request.setParameter("lastname", "Thompson");
        request.setParameter("emailAddress", "mike.t@gmail.com");
    }

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }

    private void verifyStudentExist(Integer studentId) {
        assertTrue(studentDao.findById(studentId)
                             .isPresent());
    }

    private void verifyStudentNonExist(Integer studentId) {
        assertFalse(studentDao.findById(studentId)
                              .isPresent());
    }

    @Test
    public void testGetStudentHttpRequest() throws Exception {
        student.setFirstname("first");
        student.setLastname("last");
        student.setEmailAddress("first.last@gmail.com");
        entityManager.persist(student);
        entityManager.flush();

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(APPLICATION_JSON_UTF8))
               .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)));
    }

    @Test
    public void testCreateStudent() throws Exception {
        student.setFirstname("first");
        student.setLastname("last");
        student.setEmailAddress("first.last@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/")
                                              .contentType(APPLICATION_JSON_UTF8)
                                              .content(objectMapper.writeValueAsString(student)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)));

        //double verify
        CollegeStudent verifyStudent = studentDao.findByEmailAddress("first.last@gmail.com");
        assertNotNull(verifyStudent);
    }

    @Test
    public void testDeleteStudent() throws Exception {
        verifyStudentExist(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/student/1"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$", hasSize(0)));

        //double verify
        verifyStudentNonExist(1);
    }

    @Test
    public void testDeleteNonExistStudent() throws Exception {
        verifyStudentNonExist(0);

        mockMvc.perform(MockMvcRequestBuilders.delete("/student/0"))
               .andExpect(status().is4xxClientError())
               .andExpect(content().contentType(APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.status", is(404)))
               .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void testGetStudentInformation() throws Exception {
        verifyStudentExist(1);

        mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/1"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.id", is(1)))
               .andExpect(jsonPath("$.firstname", is("Eric")))
               .andExpect(jsonPath("$.lastname", is("Roby")))
               .andExpect(jsonPath("$.emailAddress", is("eric.roby@luv2code_school.com")))
        ;
    }

    @Test
    public void testGetNonExistStudentInformation() throws Exception {
        verifyStudentNonExist(0);

        mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/0"))
               .andExpect(status().is4xxClientError())
               .andExpect(content().contentType(APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.status", is(404)))
               .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void testCreateGrade() throws Exception {
        verifyStudentExist(1);

        mockMvc.perform(MockMvcRequestBuilders.post("/grades")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .param("grade", "85.0")
                                              .param("gradeType", "math")
                                              .param("studentId", "1"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.id", is(1)))
               .andExpect(jsonPath("$.firstname", is("Eric")))
               .andExpect(jsonPath("$.lastname", is("Roby")))
               .andExpect(jsonPath("$.emailAddress", is("eric.roby@luv2code_school.com")))
               .andExpect(jsonPath("$.studentGrades.mathGradeResults", hasSize(2)));
    }

    @Test
    public void testCreateGradeStudentNotFound() throws Exception {
        verifyStudentNonExist(0);

        mockMvc.perform(MockMvcRequestBuilders.post("/grades")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .param("grade", "85.0")
                                              .param("gradeType", "math")
                                              .param("studentId", "0"))
               .andExpect(status().is4xxClientError())
               .andExpect(content().contentType(APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.status", is(404)))
               .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void testCreateGradeInvalidSubject() throws Exception {
        verifyStudentExist(1);

        mockMvc.perform(MockMvcRequestBuilders.post("/grades")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .param("grade", "85.0")
                                              .param("gradeType", "sport")
                                              .param("studentId", "1"))
               .andExpect(status().is4xxClientError())
               .andExpect(content().contentType(APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.status", is(404)))
               .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void testDeleteGrade() throws Exception {
        Optional<MathGrade> mathGradeOptional = mathGradeDao.findById(1);
        assertTrue(mathGradeOptional.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.delete("/grades/{id}/{gradeType}", 1, "math")
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.id", is(1)))
               .andExpect(jsonPath("$.firstname", is("Eric")))
               .andExpect(jsonPath("$.lastname", is("Roby")))
               .andExpect(jsonPath("$.emailAddress", is("eric.roby@luv2code_school.com")))
               .andExpect(jsonPath("$.studentGrades.mathGradeResults", hasSize(0)));
    }

    @Test
    public void testDeleteNonExistGrade() throws Exception {
        Optional<MathGrade> mathGradeOptional = mathGradeDao.findById(0);
        assertFalse(mathGradeOptional.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.delete("/grades/{id}/{gradeType}", 0, "math")
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().is4xxClientError())
               .andExpect(content().contentType(APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.status", is(404)))
               .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void testDeleteInvalidGradeType() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/grades/{id}/{gradeType}", 1, "literature")
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().is4xxClientError())
               .andExpect(content().contentType(APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.status", is(404)))
               .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }
}
