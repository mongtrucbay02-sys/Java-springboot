package com.example.courese.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter @Setter
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Tên khóa học không được để trống")
    private String name;

    @NotNull(message = "Học phí bắt buộc nhập")
    @DecimalMin(value = "0.0", inclusive = false, message = "Học phí phải lớn hơn 0")
    @Column(name = "tuition_fee")
    private BigDecimal tuitionFee;

    @NotNull(message = "Thời lượng bắt buộc nhập")
    @Min(value = 1, message = "Thời lượng phải lớn hơn 0")
    private Integer duration;

    @Column(name = "is_active")
    private boolean active = true;

    private String description;

    @ManyToOne
    @JoinColumn(name = "course_category_id")
    private CourseCategory category;

    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<CourseImage> images = new ArrayList<>();

    // Chức năng 3: chỉ tính khi hiển thị, không có cột trong DB
    public String getDurationType() {
        return (duration != null && duration <= 30) ? "Ngắn hạn" : "Dài hạn";
    }

    public String getStatusText() {
        return active ? "Đang tuyển sinh" : "Ngừng tuyển sinh";
    }

    public CourseImage getThumbnail() {
        return images.stream().filter(CourseImage::isThumbnail).findFirst().orElse(null);
    }
}