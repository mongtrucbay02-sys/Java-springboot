package com.example.courese.repository;

import com.example.courese.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseRepository extends JpaRepository<Course, Integer>, JpaSpecificationExecutor<Course> {
    boolean existsByCategoryId(Integer categoryId);
    boolean existsByNameIgnoreCaseAndCategoryId(String name, Integer categoryId);
    boolean existsByNameIgnoreCaseAndCategoryIdAndIdNot(String name, Integer categoryId, Integer id);
}