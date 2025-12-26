package com.kimlngo.springmvc.service;

import com.kimlngo.springmvc.models.CollegeStudent;
import com.kimlngo.springmvc.repository.StudentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDAO studentDao;

    public void createStudent(String firstName, String lastName, String emailAddress) {
        CollegeStudent student = new CollegeStudent(firstName, lastName, emailAddress);
        student.setId(0);
        studentDao.save(student);
    }

    public boolean isStudentExist(int id) {
        return studentDao.findById(id)
                         .isPresent();
    }

    public void deleteStudent(int id) {
        if (isStudentExist(id)) {
            studentDao.deleteById(id);
        }
    }

    public List<CollegeStudent> getGradebook() {
        List<CollegeStudent> studentList = new ArrayList<>();
        studentDao.findAll()
                  .forEach(studentList::add);
        return studentList;
    }
}
