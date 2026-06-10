package com.courses.service;

import com.courses.entity.*;
import com.courses.exception.BusinessException;
import com.courses.repository.CourseEnrollmentRepository;
import com.courses.repository.LessonProgressRepository;
import com.courses.repository.LessonRepository;
import com.courses.repository.SubmissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock private LessonProgressRepository progressRepository;
    @Mock private LessonRepository lessonRepository;
    @Mock private CourseEnrollmentRepository enrollmentRepository;
    @Mock private SubmissionRepository submissionRepository;

    @InjectMocks private ProgressService progressService;

    @Test
    void markLessonCompleted_shouldFailWhenNotEnrolled() {
        User user = User.builder().id(1L).build();
        Course course = Course.builder().id(10L).build();
        Section section = Section.builder().course(course).build();
        Lesson lesson = Lesson.builder().id(5L).section(section).build();

        when(lessonRepository.findById(5L)).thenReturn(Optional.of(lesson));
        when(enrollmentRepository.existsByUserIdAndCourseId(1L, 10L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> progressService.markLessonCompleted(user, 5L));
        verify(progressRepository, never()).save(any());
    }
}