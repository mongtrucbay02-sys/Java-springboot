package com.example.courese.controller;

import com.example.courese.entity.Course;
import com.example.courese.entity.CourseImage;
import com.example.courese.exception.NotFoundException;
import com.example.courese.repository.CourseCategoryRepository;
import com.example.courese.repository.CourseImageRepository;
import com.example.courese.repository.CourseRepository;
import com.example.courese.repository.CourseSpecs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseRepository courseRepo;
    private final CourseCategoryRepository categoryRepo;
    private final CourseImageRepository imageRepo;

    // ===== Danh sách, tìm kiếm, lọc, sắp xếp =====
    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Integer categoryId,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) BigDecimal minFee,
                       @RequestParam(required = false) BigDecimal maxFee,
                       @RequestParam(required = false) String sort,
                       Model model) {

        if (minFee != null && maxFee != null && minFee.compareTo(maxFee) > 0) {
            model.addAttribute("error", "Học phí tối thiểu không được lớn hơn học phí tối đa");
            model.addAttribute("courses", List.of());
        } else {
            Boolean active = "active".equals(status) ? Boolean.TRUE
                           : "inactive".equals(status) ? Boolean.FALSE : null;
            String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

            Sort s = switch (sort == null ? "" : sort) {
                case "fee_asc" -> Sort.by("tuitionFee").ascending();
                case "fee_desc" -> Sort.by("tuitionFee").descending();
                case "duration_asc" -> Sort.by("duration").ascending();
                default -> Sort.by("id");
            };

            model.addAttribute("courses",
                courseRepo.findAll(CourseSpecs.filter(kw, categoryId, active, minFee, maxFee), s));
        }

        // giữ lại điều kiện đã nhập
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);
        model.addAttribute("minFee", minFee);
        model.addAttribute("maxFee", maxFee);
        model.addAttribute("sort", sort);
        model.addAttribute("categories", categoryRepo.findAll());
        return "courses/list";
    }

    // ===== Chi tiết =====
    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("course", findCourse(id));
        return "courses/detail";
    }

    // ===== Create =====
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("categories", categoryRepo.findAll());
        return "courses/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("course") Course course,
                         BindingResult br, Model model) {
        validateBusiness(course, br);
        if (br.hasErrors()) {
            model.addAttribute("categories", categoryRepo.findAll());
            return "courses/form";
        }
        course.setCategory(categoryRepo.findById(course.getCategory().getId()).get());
        courseRepo.save(course);
        return "redirect:/courses";
    }

    // ===== Edit =====
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("course", findCourse(id));
        model.addAttribute("categories", categoryRepo.findAll());
        return "courses/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Integer id,
                       @Valid @ModelAttribute("course") Course course,
                       BindingResult br, Model model) {
        Course existing = findCourse(id);
        course.setId(id);
        validateBusiness(course, br);
        if (br.hasErrors()) {
            model.addAttribute("categories", categoryRepo.findAll());
            return "courses/form";
        }
        existing.setName(course.getName().trim());
        existing.setTuitionFee(course.getTuitionFee());
        existing.setDuration(course.getDuration());
        existing.setActive(course.isActive());
        existing.setDescription(course.getDescription());
        existing.setCategory(categoryRepo.findById(course.getCategory().getId()).get());
        courseRepo.save(existing);
        return "redirect:/courses";
    }

    // ===== Ảnh =====
    @PostMapping("/{id}/images")
    public String addImage(@PathVariable Integer id,
                           @RequestParam String imageUrl,
                           RedirectAttributes ra) {
        Course course = findCourse(id);
        if (imageUrl == null || imageUrl.isBlank()) {
            ra.addFlashAttribute("error", "URL ảnh không được để trống");
        } else {
            CourseImage img = new CourseImage();
            img.setImageUrl(imageUrl.trim());
            img.setCourse(course);
            imageRepo.save(img);
        }
        return "redirect:/courses/" + id;
    }

    @PostMapping("/{id}/images/{imageId}/thumbnail")
    @Transactional
    public String setThumbnail(@PathVariable Integer id, @PathVariable Integer imageId) {
        findCourse(id);
        CourseImage img = imageRepo.findById(imageId)
            .filter(i -> i.getCourse().getId().equals(id))
            .orElseThrow(() -> new NotFoundException("Không tìm thấy ảnh có Id = " + imageId));

        imageRepo.clearThumbnail(id);          // ảnh đại diện cũ -> ảnh thường
        img = imageRepo.findById(imageId).get();
        img.setThumbnail(true);                // ảnh mới -> đại diện
        imageRepo.save(img);
        return "redirect:/courses/" + id;
    }

    // ===== Helper =====
    private Course findCourse(Integer id) {
        return courseRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy khóa học có Id = " + id));
    }

    private void validateBusiness(Course c, BindingResult br) {
        if (c.getCategory() == null || c.getCategory().getId() == null) {
            br.rejectValue("category", "", "Phải chọn loại khóa học");
            return;
        }
        Integer catId = c.getCategory().getId();
        if (!categoryRepo.existsById(catId)) {
            br.rejectValue("category", "", "Loại khóa học không tồn tại");
            return;
        }
        if (c.getName() != null && !c.getName().isBlank()) {
            String name = c.getName().trim();
            boolean dup = (c.getId() == null)
                ? courseRepo.existsByNameIgnoreCaseAndCategoryId(name, catId)
                : courseRepo.existsByNameIgnoreCaseAndCategoryIdAndIdNot(name, catId, c.getId());
            if (dup) br.rejectValue("name", "", "Tên khóa học đã tồn tại trong loại này");
        }
    }
}