package com.courses.controller;

import com.courses.entity.enums.Role;
import com.courses.security.CustomUserDetails;
import com.courses.security.SecurityUtils;
import com.courses.service.EnrollmentService;
import com.courses.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final EnrollmentService enrollmentService;
    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        CustomUserDetails user = SecurityUtils.getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        if (user.hasRole(Role.STUDENT)) {
            model.addAttribute("enrollments", enrollmentService.getUserEnrollments(user.getId()));
            model.addAttribute("stats", statisticsService.getStudentStats(user.getId()));
        }

        if (user.hasRole(Role.TEACHER) || user.hasRole(Role.ADMIN)) {
            model.addAttribute("popularCourses", statisticsService.getPopularCourses(5));
            model.addAttribute("pendingCount",
                    statisticsService.getPendingSubmissionsCount());
        }

        return "dashboard";
    }
}