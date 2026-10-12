package com.example.courese.repository;

import com.example.courese.entity.CourseImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseImageRepository extends JpaRepository<CourseImage, Integer> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE CourseImage i SET i.thumbnail = false WHERE i.course.id = :courseId")
    void clearThumbnail(@Param("courseId") Integer courseId);
}