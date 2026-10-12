package com.example.courese.repository;

import com.example.courese.entity.Course;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CourseSpecs {

    public static Specification<Course> filter(String keyword, Integer categoryId, Boolean active,
                                            BigDecimal minFee, BigDecimal maxFee) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (keyword != null)
                p.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
            if (categoryId != null)
                p.add(cb.equal(root.get("category").get("id"), categoryId));
            if (active != null)
                p.add(cb.equal(root.get("active"), active));
            if (minFee != null)
                p.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("tuitionFee"), minFee));
            if (maxFee != null)
                p.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("tuitionFee"), maxFee));
            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}