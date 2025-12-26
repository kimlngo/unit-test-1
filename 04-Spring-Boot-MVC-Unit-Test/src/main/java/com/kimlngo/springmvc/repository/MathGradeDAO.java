package com.kimlngo.springmvc.repository;

import com.kimlngo.springmvc.models.MathGrade;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MathGradeDAO extends CrudRepository<MathGrade, Integer> {

    List<MathGrade> findGradeByStudentId(int id);

    void deleteGradeByStudentId(int id);
}
