package com.kimlngo.springmvc.controller;

import com.kimlngo.springmvc.models.*;
import com.kimlngo.springmvc.service.StudentAndGradeService;
import com.kimlngo.springmvc.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GradebookController {

    @Autowired
    private Gradebook gradebook;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private Validator validator;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getStudents(Model m) {
        List<CollegeStudent> gradebook = studentService.getGradebook();
        m.addAttribute("students", gradebook);
        return "index";
    }

    @PostMapping(value = "/")
    public String createStudents(@ModelAttribute("student") CollegeStudent student, Model m) {
        studentService.createStudent(student.getFirstname(),
                student.getLastname(),
                student.getEmailAddress());
        List<CollegeStudent> studentList = studentService.getGradebook();
        m.addAttribute("students", studentList);
        return "index";
    }

    @GetMapping("/delete/student/{id}")
    public String deleteStudent(@PathVariable int id, Model m) {
        //check if student exists before deletion
        if (!validator.isStudentIdExist(id)) {
            return "error";
        }

        studentService.deleteStudent(id);
        List<CollegeStudent> students = studentService.getGradebook();
        m.addAttribute("students", students);
        return "index";
    }

    @GetMapping("/studentInformation/{id}")
    public String studentInformation(@PathVariable int id, Model m) {
        if (!validator.isStudentIdExist(id)) {
            return "error";
        }

        studentService.configureStudentInfoModel(id, m);

        return "studentInformation";
    }

    @PostMapping(value = "/grades")
    public String createGrade(@RequestParam("grade") double grade,
                              @RequestParam("gradeType") String gradeType,
                              @RequestParam("studentId") int studentId,
                              Model m) {
        if (!studentService.isStudentExist(studentId) || GradeType.findGradeType(gradeType) == null)
            return "error";

        boolean isSuccess = studentService.createGrade(grade, studentId, GradeType.findGradeType(gradeType));

        if (!isSuccess)
            return "error";

        studentService.configureStudentInfoModel(studentId, m);

        return "studentInformation";
    }

    @GetMapping(value = "/grades/{id}/{gradeType}")
    public String deleteGrade(@PathVariable int id, @PathVariable String gradeType, Model m) {
        if (!validator.isStudentIdExist(id) || validator.isGradeTypeValid(gradeType))
            return "error";

        int studentId = studentService.deleteGrade(id, GradeType.findGradeType(gradeType));

        if (studentId == 0)
            return "error";

        studentService.configureStudentInfoModel(id, m);
        return "studentInformation";
    }
}
