package com.kimlngo.springmvc.repository;

import com.kimlngo.springmvc.models.HistoryGrade;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HistoryGradeDAO extends CrudRepository<HistoryGrade, Integer> {
    List<HistoryGrade> findGradeByStudentId(int id);
}
