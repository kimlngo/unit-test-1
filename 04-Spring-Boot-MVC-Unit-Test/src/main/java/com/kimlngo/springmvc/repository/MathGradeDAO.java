package com.kimlngo.springmvc.repository;

import com.kimlngo.springmvc.models.Grade;
import com.kimlngo.springmvc.models.MathGrade;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MathGradeDAO extends CrudRepository<MathGrade, Integer> {

    List<Grade> findGradeByStudentId(int id);

    void deleteGradeByStudentId(int studentId);
}
