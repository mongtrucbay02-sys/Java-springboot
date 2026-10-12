package com.example.courese.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_categories")
@Getter @Setter
public class CourseCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Tên loại không được để trống")
    private String name;

    private String description;

    @OneToMany(mappedBy = "category")
    private List<Course> courses = new ArrayList<>();
}