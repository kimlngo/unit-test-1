package com.kimlngo.springmvc.service;

import com.kimlngo.springmvc.models.CollegeStudent;
import com.kimlngo.springmvc.repository.StudentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDAO studentDao;

    public void createStudent(String firstName, String lastName, String email) {
        CollegeStudent student = new CollegeStudent(firstName, lastName, email);
        student.setId(0);
        studentDao.save(student);
    }

    public boolean checkIfStudentIsNull(int id) {
        return studentDao.findById(id)
                         .isPresent();
    }

    public void deleteStudent(int id) {
        if (checkIfStudentIsNull(id)) {
            studentDao.deleteById(id);
        }
    }
}
