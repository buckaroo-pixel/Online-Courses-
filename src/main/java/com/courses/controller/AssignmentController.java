package com.courses.controller;

import com.courses.security.SecurityUtils;
import com.courses.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @GetMapping("/{id}")
    public String viewAssignment(@PathVariable Long id, Model model) {
        Long userId = SecurityUtils.getCurrentUserId();
        model.addAttribute("assignment", assignmentService.getAssignment(id, userId));
        return "assignments/view";
    }

    @PostMapping("/{id}/quiz")
    public String submitQuiz(@PathVariable Long id,
                             @RequestParam Map<String, String> params,
                             RedirectAttributes redirectAttributes) {
        var user = SecurityUtils.getCurrentUser();
        Map<Long, Long> answers = new HashMap<>();
        params.forEach((key, value) -> {
            if (key.startsWith("question_")) {
                Long questionId = Long.parseLong(key.substring("question_".length()));
                answers.put(questionId, Long.parseLong(value));
            }
        });
        var submission = assignmentService.submitQuiz(user.getUser(), id, answers);
        redirectAttributes.addFlashAttribute("message",
                "Тест сдан! Ваш результат: " + submission.getScore() + " баллов");
        return "redirect:/assignments/" + id;
    }

    @PostMapping("/{id}/practical")
    public String submitPractical(@PathVariable Long id,
                                  @RequestParam String answerText,
                                  RedirectAttributes redirectAttributes) {
        var user = SecurityUtils.getCurrentUser();
        assignmentService.submitPractical(user.getUser(), id, answerText);
        redirectAttributes.addFlashAttribute("message", "Работа отправлена на проверку!");
        return "redirect:/assignments/" + id;
    }
}