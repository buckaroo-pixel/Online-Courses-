package com.courses.controller;

import com.courses.entity.enums.AssignmentType;
import com.courses.entity.enums.LessonType;
import com.courses.security.SecurityUtils;
import com.courses.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

    private final CourseService courseService;
    private final SectionService sectionService;
    private final LessonService lessonService;
    private final AssignmentService assignmentService;
    private final StatisticsService statisticsService;

    @GetMapping("/courses")
    public String myCourses(Model model) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        model.addAttribute("courses", courseService.getTeacherCourses(teacherId, PageRequest.of(0, 50)));
        return "teacher/courses";
    }

    @GetMapping("/courses/new")
    public String newCourseForm() {
        return "teacher/course-form";
    }

    @PostMapping("/courses")
    public String createCourse(@RequestParam String title,
                               @RequestParam String description,
                               RedirectAttributes redirectAttributes) {
        var user = SecurityUtils.getCurrentUser();
        var course = courseService.createCourse(title, description, user.getUser());
        redirectAttributes.addFlashAttribute("message", "Курс создан!");
        return "redirect:/teacher/courses/" + course.getId() + "/edit";
    }

    @GetMapping("/courses/{id}/edit")
    public String editCourse(@PathVariable Long id, Model model) {
        Long userId = SecurityUtils.getCurrentUserId();
        model.addAttribute("course", courseService.getCourseDetails(id, userId));
        model.addAttribute("sections", sectionService.getByCourseId(id));
        return "teacher/course-edit";
    }

    @PostMapping("/courses/{id}")
    public String updateCourse(@PathVariable Long id,
                               @RequestParam String title,
                               @RequestParam String description,
                               RedirectAttributes redirectAttributes) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        courseService.updateCourse(id, title, description, teacherId);
        redirectAttributes.addFlashAttribute("message", "Курс обновлён");
        return "redirect:/teacher/courses/" + id + "/edit";
    }

    @PostMapping("/courses/{id}/publish")
    public String publishCourse(@PathVariable Long id,
                                @RequestParam boolean published,
                                RedirectAttributes redirectAttributes) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        courseService.publishCourse(id, teacherId, published);
        redirectAttributes.addFlashAttribute("message", published ? "Курс опубликован" : "Курс снят с публикации");
        return "redirect:/teacher/courses/" + id + "/edit";
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        courseService.deleteCourse(id, teacherId);
        redirectAttributes.addFlashAttribute("message", "Курс удалён");
        return "redirect:/teacher/courses";
    }

    @PostMapping("/courses/{courseId}/sections")
    public String addSection(@PathVariable Long courseId,
                             @RequestParam String title,
                             @RequestParam int orderIndex,
                             RedirectAttributes redirectAttributes) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        sectionService.createSection(courseId, title, orderIndex, teacherId);
        redirectAttributes.addFlashAttribute("message", "Раздел добавлен");
        return "redirect:/teacher/courses/" + courseId + "/edit";
    }

    @PostMapping("/sections/{sectionId}/lessons")
    public String addLesson(@PathVariable Long sectionId,
                            @RequestParam String title,
                            @RequestParam String content,
                            @RequestParam LessonType type,
                            @RequestParam(required = false) String mediaUrl,
                            @RequestParam int orderIndex,
                            @RequestParam(required = false) Integer duration,
                            @RequestParam Long courseId,
                            RedirectAttributes redirectAttributes) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        lessonService.createLesson(sectionId, title, content, type, mediaUrl, orderIndex, duration, teacherId);
        redirectAttributes.addFlashAttribute("message", "Урок добавлен");
        return "redirect:/teacher/courses/" + courseId + "/edit";
    }

    @PostMapping("/lessons/{lessonId}/assignments")
    public String addAssignment(@PathVariable Long lessonId,
                                @RequestParam String title,
                                @RequestParam String description,
                                @RequestParam AssignmentType type,
                                @RequestParam int maxScore,
                                @RequestParam Long courseId,
                                RedirectAttributes redirectAttributes) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        assignmentService.createAssignment(lessonId, title, description, type, maxScore, teacherId);
        redirectAttributes.addFlashAttribute("message", "Задание добавлено");
        return "redirect:/teacher/courses/" + courseId + "/edit";
    }

    @GetMapping("/submissions")
    public String pendingSubmissions(Model model) {
        model.addAttribute("submissions", assignmentService.getPendingSubmissions());
        return "teacher/submissions";
    }

    @PostMapping("/submissions/{id}/grade")
    public String gradeSubmission(@PathVariable Long id,
                                  @RequestParam int score,
                                  @RequestParam String feedback,
                                  RedirectAttributes redirectAttributes) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        assignmentService.gradeSubmission(id, score, feedback, teacherId);
        redirectAttributes.addFlashAttribute("message", "Работа проверена");
        return "redirect:/teacher/submissions";
    }

    @GetMapping("/statistics")
    public String statistics(@RequestParam(required = false) Long courseId, Model model) {
        model.addAttribute("popularCourses", statisticsService.getPopularCourses(10));
        if (courseId != null) {
            model.addAttribute("studentStats", statisticsService.getCourseStudentStats(courseId));
            model.addAttribute("selectedCourseId", courseId);
        }
        Long teacherId = SecurityUtils.getCurrentUserId();
        model.addAttribute("myCourses", courseService.getTeacherCourses(teacherId, PageRequest.of(0, 50)));
        return "teacher/statistics";
    }
}