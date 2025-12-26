package com.kimlngo.springmvc.repository;

import com.kimlngo.springmvc.models.ScienceGrade;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ScienceGradeDAO extends CrudRepository<ScienceGrade, Integer> {
    List<ScienceGrade> findGradeByStudentId(int id);
}
