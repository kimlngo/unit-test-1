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
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private StudentGrades studentGrades;

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
            mathGradeDao.deleteGradeByStudentId(id);
            scienceGradeDao.deleteGradeByStudentId(id);
            historyGradeDao.deleteGradeByStudentId(id);
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

    public int deleteGrade(int id, GradeType gradeType) {
        int sId = 0;

        switch (gradeType) {
            case MATH -> {
                Optional<MathGrade> mathOpt = mathGradeDao.findById(id);
                if (mathOpt.isEmpty())
                    return sId;

                sId = mathOpt.get()
                             .getStudentId();
                mathGradeDao.deleteById(id);
            }
            case SCIENCE -> {
                Optional<ScienceGrade> scienceOpt = scienceGradeDao.findById(id);
                if (scienceOpt.isEmpty())
                    return sId;

                sId = scienceOpt.get()
                                .getStudentId();
                scienceGradeDao.deleteById(id);
            }
            case HISTORY -> {
                Optional<HistoryGrade> historyOpt = historyGradeDao.findById(id);
                if (historyOpt.isEmpty())
                    return sId;

                sId = historyOpt.get()
                                .getStudentId();
                historyGradeDao.deleteById(id);
            }
        }
        return sId;
    }

    public GradebookCollegeStudent getStudentInformation(int id) {
        Optional<CollegeStudent> studentOpt = studentDao.findById(id);

        if (studentOpt.isEmpty())
            return null;

        //looking for grades
        List<Grade> mathGrades = mathGradeDao.findGradeByStudentId(id);
        List<Grade> scienceGrades = scienceGradeDao.findGradeByStudentId(id);
        List<Grade> historyGrades = historyGradeDao.findGradeByStudentId(id);

        studentGrades.setMathGradeResults(mathGrades);
        studentGrades.setScienceGradeResults(scienceGrades);
        studentGrades.setHistoryGradeResults(historyGrades);

        CollegeStudent student = studentOpt.get();
        return new GradebookCollegeStudent(student.getId(), student.getFirstname(), student.getLastname(),
                student.getEmailAddress(), studentGrades);
    }

    public void configureStudentInfoModel(int id, Model m) {
        //Adding averages for math, science and history
        GradebookCollegeStudent collegeStudent = getStudentInformation(id);
        m.addAttribute("student", collegeStudent);

        StudentGrades studentGrades = collegeStudent.getStudentGrades();

        m.addAttribute("mathAverage",
                studentGrades.getMathGradeResults()
                             .isEmpty() ?
                        "N/A" :
                        studentGrades.findGPA(studentGrades.getMathGradeResults())
        );

        m.addAttribute("scienceAverage",
                studentGrades.getScienceGradeResults()
                             .isEmpty() ?
                        "N/A" :
                        studentGrades.findGPA(studentGrades.getScienceGradeResults())
        );

        m.addAttribute("historyAverage",
                studentGrades.getHistoryGradeResults()
                             .isEmpty() ?
                        "N/A" :
                        studentGrades.findGPA(studentGrades.getHistoryGradeResults())
        );
    }
}
