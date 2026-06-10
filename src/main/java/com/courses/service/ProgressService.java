package com.courses.service;

import com.courses.entity.CourseEnrollment;
import com.courses.entity.Lesson;
import com.courses.entity.LessonProgress;
import com.courses.entity.User;
import com.courses.exception.BusinessException;
import com.courses.repository.CourseEnrollmentRepository;
import com.courses.repository.LessonProgressRepository;
import com.courses.repository.LessonRepository;
import com.courses.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final LessonProgressRepository progressRepository;
    private final LessonRepository lessonRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;

    @Transactional
    @CacheEvict(value = {"courses", "courseDetails"}, allEntries = true)
    public void markLessonCompleted(User user, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new BusinessException("Урок не найден"));

        Long courseId = lesson.getSection().getCourse().getId();
        if (!enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new BusinessException("Сначала запишитесь на курс");
        }

        LessonProgress progress = progressRepository.findByUserIdAndLessonId(user.getId(), lessonId)
                .orElse(LessonProgress.builder()
                        .user(user)
                        .lesson(lesson)
                        .build());

        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        progressRepository.save(progress);

        recalculateCourseProgress(user.getId(), courseId);
    }

    @Transactional
    public void recalculateCourseProgress(Long userId, Long courseId) {
        long totalLessons = lessonRepository.countByCourseId(courseId);
        if (totalLessons == 0) {
            return;
        }
        long completed = progressRepository.countCompletedByUserAndCourse(userId, courseId);
        int progressPercent = (int) ((completed * 100) / totalLessons);
        int totalScore = submissionRepository.sumScoresByUserAndCourse(userId, courseId);

        enrollmentRepository.findByUserIdAndCourseId(userId, courseId).ifPresent(enrollment -> {
            enrollment.setProgressPercent(progressPercent);
            enrollment.setTotalScore(totalScore);
            enrollmentRepository.save(enrollment);
        });
    }
}