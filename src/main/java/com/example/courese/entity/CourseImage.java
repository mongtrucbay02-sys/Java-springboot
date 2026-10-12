package com.example.courese.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "course_images")
@Getter @Setter
public class CourseImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_thumbnail")
    private boolean thumbnail;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}