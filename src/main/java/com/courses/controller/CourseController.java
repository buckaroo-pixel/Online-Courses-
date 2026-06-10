package com.courses.controller;

import com.courses.security.SecurityUtils;
import com.courses.service.CourseService;
import com.courses.service.EnrollmentService;
import com.courses.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final LessonService lessonService;

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getPublishedCourses(PageRequest.of(0, 20)));
        return "courses/list";
    }

    @GetMapping("/{id}")
    public String courseDetails(@PathVariable Long id, Model model) {
        Long userId = SecurityUtils.getCurrentUserId();
        var course = courseService.getCourseDetails(id, userId);
        model.addAttribute("course", course);
        if (userId != null) {
            model.addAttribute("enrolled", enrollmentService.isEnrolled(userId, id));
            var lessons = lessonService.getLessonsWithProgress(id, userId);
            model.addAttribute("lessons", lessons);
            model.addAttribute("completedLessonIds", lessons.stream()
                    .filter(com.courses.dto.LessonDto::isCompleted)
                    .map(com.courses.dto.LessonDto::getId)
                    .collect(java.util.stream.Collectors.toSet()));
        }
        return "courses/detail";
    }

    @PostMapping("/{id}/enroll")
    public String enroll(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var user = SecurityUtils.getCurrentUser();
        enrollmentService.enroll(user.getUser(), id);
        redirectAttributes.addFlashAttribute("message", "Вы успешно записались на курс!");
        return "redirect:/courses/" + id;
    }
}