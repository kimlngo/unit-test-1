package com.kimlngo.springmvc;

import com.kimlngo.springmvc.models.CollegeStudent;
import com.kimlngo.springmvc.models.GradebookCollegeStudent;
import com.kimlngo.springmvc.repository.StudentDAO;
import com.kimlngo.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradebookControllerTest {

    private static MockHttpServletRequest request;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentAndGradeService studentCreateServiceMock;

    @Autowired
    private StudentDAO studentDao;

    @BeforeAll
    public static void setupBeforeAll() {
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "Ben");
        request.setParameter("lastname", "Afflect");
        request.setParameter("emailAddress", "ben.afflect@gmail.com");
    }

    @BeforeEach
    public void setupBeforeEach() {
        jdbc.execute("insert into student(firstname, lastname, email_address) " +
                "values ('test1', 'test1', 'test1@gmail.com')");
    }

    @AfterEach
    public void cleanUpAfterEach() {
        jdbc.execute("delete from student");
        jdbc.execute("alter table student alter column id restart with 1");
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
}
