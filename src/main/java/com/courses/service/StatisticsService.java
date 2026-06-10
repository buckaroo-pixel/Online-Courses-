package com.courses.service;

import com.courses.dto.CourseStatsDto;
import com.courses.dto.StudentStatsDto;
import com.courses.entity.CourseEnrollment;
import com.courses.repository.CourseEnrollmentRepository;
import com.courses.repository.CourseRepository;
import com.courses.repository.LessonProgressRepository;
import com.courses.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final CourseEnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final LessonProgressRepository progressRepository;
    private final SubmissionRepository submissionRepository;

    @Transactional(readOnly = true)
    public StudentStatsDto getStudentStats(Long userId) {
        List<CourseEnrollment> enrollments = enrollmentRepository.findByUserId(userId);
        int totalScore = enrollments.stream().mapToInt(CourseEnrollment::getTotalScore).sum();
        double avgProgress = enrollments.isEmpty() ? 0
                : enrollments.stream().mapToInt(CourseEnrollment::getProgressPercent).average().orElse(0);

        long completedLessons = progressRepository.countByUserIdAndCompletedTrue(userId);

        String fullName = enrollments.isEmpty() ? "Студент"
                : enrollments.getFirst().getUser().getFullName();

        return StudentStatsDto.builder()
                .userId(userId)
                .fullName(fullName)
                .enrolledCourses(enrollments.size())
                .completedLessons((int) completedLessons)
                .totalScore(totalScore)
                .averageProgress(avgProgress)
                .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "popularCourses")
    public List<CourseStatsDto> getPopularCourses(int limit) {
        return courseRepository.findPopularCourses(PageRequest.of(0, limit)).stream()
                .map(row -> CourseStatsDto.builder()
                        .courseId((Long) row[0])
                        .courseTitle((String) row[1])
                        .enrollmentCount((Long) row[2])
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getCourseStudentStats(Long courseId) {
        return enrollmentRepository.findStudentStatsByCourse(courseId);
    }

    @Transactional(readOnly = true)
    public int getPendingSubmissionsCount() {
        return submissionRepository.findPendingPracticalSubmissions().size();
    }
}