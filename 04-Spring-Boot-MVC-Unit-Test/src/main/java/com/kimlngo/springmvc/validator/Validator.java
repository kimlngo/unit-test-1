package com.kimlngo.springmvc.validator;

import com.kimlngo.springmvc.models.GradeType;
import com.kimlngo.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Validator {

    @Autowired
    private StudentAndGradeService studentService;

    /**
     *
     * @param studentId
     * @return true if student id exist
     * Otherwise false
     */
    public boolean isStudentIdExist(int studentId) {
        return studentService.isStudentExist(studentId);
    }

    public boolean isGradeTypeValid(String gradeType) {
        return GradeType.findGradeType(gradeType) == null;
    }
}
