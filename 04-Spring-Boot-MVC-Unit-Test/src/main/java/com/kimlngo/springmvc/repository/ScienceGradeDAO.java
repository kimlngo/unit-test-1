package com.kimlngo.springmvc.repository;

import com.kimlngo.springmvc.models.Grade;
import com.kimlngo.springmvc.models.ScienceGrade;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ScienceGradeDAO extends CrudRepository<ScienceGrade, Integer> {
    List<Grade> findGradeByStudentId(int id);

    void deleteGradeByStudentId(int id);
}
