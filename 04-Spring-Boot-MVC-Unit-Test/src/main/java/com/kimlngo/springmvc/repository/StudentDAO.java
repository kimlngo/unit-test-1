package com.kimlngo.springmvc.repository;

import com.kimlngo.springmvc.models.CollegeStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentDAO extends CrudRepository<CollegeStudent, Integer> {

    public CollegeStudent findByEmailAddress(String email);
}
