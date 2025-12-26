package com.kimlngo.springmvc.service;

import com.kimlngo.springmvc.models.*;
import com.kimlngo.springmvc.repository.HistoryGradeDAO;
import com.kimlngo.springmvc.repository.MathGradeDAO;
import com.kimlngo.springmvc.repository.ScienceGradeDAO;
import com.kimlngo.springmvc.repository.StudentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDAO studentDao;

    @Autowired
    private MathGradeDAO mathGradeDao;

    @Autowired
    private ScienceGradeDAO scienceGradeDao;

    @Autowired
    private HistoryGradeDAO historyGradeDao;

    //entity
    @Autowired
    @Qualifier("mathGrade")
    private MathGrade mathGrade;

    @Autowired
    @Qualifier("scienceGrade")
    private ScienceGrade scienceGrade;

    @Autowired
    @Qualifier("historyGrade")
    private HistoryGrade historyGrade;

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

    public boolean createGrade(double grade, int studentId, GradeType type) {
        if (!isStudentExist(studentId))
            return false;

        if (grade < 0 || grade > 100)
            return false;

        switch (type) {
            case MATH -> {
                mathGrade.setId(0);
                mathGrade.setStudentId(studentId);
                mathGrade.setGrade(grade);
                mathGradeDao.save(mathGrade);

                return true;
            }
            case SCIENCE -> {
                scienceGrade.setId(0);
                scienceGrade.setStudentId(studentId);
                scienceGrade.setGrade(grade);
                scienceGradeDao.save(scienceGrade);

                return true;
            }
            case HISTORY -> {
                historyGrade.setId(0);
                historyGrade.setStudentId(studentId);
                historyGrade.setGrade(grade);
                historyGradeDao.save(historyGrade);

                return true;
            }
        }
        return false;
    }
}
