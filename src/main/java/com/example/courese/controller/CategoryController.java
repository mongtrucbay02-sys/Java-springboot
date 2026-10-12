package com.example.courese.controller;

import com.example.courese.entity.CourseCategory;
import com.example.courese.exception.NotFoundException;
import com.example.courese.repository.CourseCategoryRepository;
import com.example.courese.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CourseCategoryRepository categoryRepo;
    private final CourseRepository courseRepo;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryRepo.findAll());
        return "categories/list";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        CourseCategory cat = categoryRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy loại khóa học có Id = " + id));

        if (courseRepo.existsByCategoryId(id)) {
            ra.addFlashAttribute("error",
                "Không thể xóa loại \"" + cat.getName() + "\" vì vẫn còn khóa học thuộc loại này");
        } else {
            categoryRepo.delete(cat);
            ra.addFlashAttribute("success", "Đã xóa loại khóa học");
        }
        return "redirect:/categories";
    }
}