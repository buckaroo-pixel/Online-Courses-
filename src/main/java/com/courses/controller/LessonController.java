package com.courses.controller;

import com.courses.service.AssignmentService;
import com.courses.service.LessonService;
import com.courses.service.ProgressService;
import com.courses.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lessons")
public class LessonController {

    private final LessonService lessonService;
    private final AssignmentService assignmentService;
    private final ProgressService progressService;

    @GetMapping("/{id}")
    public String viewLesson(@PathVariable Long id, Model model) {
        Long userId = SecurityUtils.getCurrentUserId();
        var lesson = lessonService.getLesson(id, userId);
        model.addAttribute("lesson", lesson);
        model.addAttribute("assignments", assignmentService.getAssignmentsByLesson(id, userId));
        return "lessons/view";
    }

    @PostMapping("/{id}/complete")
    public String completeLesson(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var user = SecurityUtils.getCurrentUser();
        progressService.markLessonCompleted(user.getUser(), id);
        redirectAttributes.addFlashAttribute("message", "Урок отмечен как пройденный!");
        var lesson = lessonService.getLesson(id, user.getId());
        return "redirect:/courses/" + lesson.getCourseId();
    }
}