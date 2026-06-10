package com.courses.service;

import com.courses.entity.Course;
import com.courses.entity.CourseEnrollment;
import com.courses.entity.User;
import com.courses.exception.BusinessException;
import com.courses.repository.CourseEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final CourseEnrollmentRepository enrollmentRepository;
    private final CourseService courseService;

    @Transactional
    @CacheEvict(value = {"courses", "courseDetails", "popularCourses"}, allEntries = true)
    public CourseEnrollment enroll(User user, Long courseId) {
        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new BusinessException("Вы уже записаны на этот курс");
        }
        Course course = courseService.getCourseEntity(courseId);
        if (!course.isPublished()) {
            throw new BusinessException("Курс ещё не опубликован");
        }
        CourseEnrollment enrollment = CourseEnrollment.builder()
                .user(user)
                .course(course)
                .enrolledAt(LocalDateTime.now())
                .progressPercent(0)
                .totalScore(0)
                .build();
        return enrollmentRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    public List<CourseEnrollment> getUserEnrollments(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean isEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }
}