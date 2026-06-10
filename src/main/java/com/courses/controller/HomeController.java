package com.courses.controller;

import com.courses.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CourseService courseService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("courses", courseService.getPublishedCourses(PageRequest.of(0, 6)));
        return "index";
    }
}