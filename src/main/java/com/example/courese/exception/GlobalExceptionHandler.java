package com.example.courese.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public String notFound(NotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error-page";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String badParam(Model model) {
        model.addAttribute("message", "Tham số không hợp lệ");
        return "error-page";
    }
}