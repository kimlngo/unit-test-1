package com.kimlngo.springmvc.controller;

import com.kimlngo.springmvc.models.*;
import com.kimlngo.springmvc.service.StudentAndGradeService;
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
        if (!studentService.isStudentExist(id)) {
            return "error";
        }

        studentService.deleteStudent(id);
        List<CollegeStudent> students = studentService.getGradebook();
        m.addAttribute("students", students);
        return "index";
    }

    @GetMapping("/studentInformation/{id}")
    public String studentInformation(@PathVariable int id, Model m) {
        return "studentInformation";
    }

}
