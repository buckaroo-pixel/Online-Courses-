package com.courses.service;

import com.courses.dto.CourseDto;
import com.courses.entity.Course;
import com.courses.entity.User;
import com.courses.exception.BusinessException;
import com.courses.exception.ResourceNotFoundException;
import com.courses.mapper.CourseMapper;
import com.courses.repository.CourseEnrollmentRepository;
import com.courses.repository.CourseRepository;
import com.courses.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final CourseMapper courseMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "courses", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<CourseDto> getPublishedCourses(Pageable pageable) {
        return courseRepository.findByPublishedTrue(pageable)
                .map(course -> enrichCourseDto(courseMapper.toDto(course), null));
    }

    @Transactional(readOnly = true)
    public Page<CourseDto> getTeacherCourses(Long teacherId, Pageable pageable) {
        return courseRepository.findByTeacherId(teacherId, pageable)
                .map(course -> enrichCourseDto(courseMapper.toDto(course), null));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "courseDetails", key = "#id + '-' + (#userId != null ? #userId : 'guest')")
    public CourseDto getCourseDetails(Long id, Long userId) {
        Course course = courseRepository.findByIdWithContent(id)
                .orElseThrow(() -> new ResourceNotFoundException("Курс не найден"));
        CourseDto dto = courseMapper.toDetailDto(course);
        return enrichCourseDto(dto, userId);
    }

    @Transactional
    @CacheEvict(value = {"courses", "courseDetails", "popularCourses"}, allEntries = true)
    public Course createCourse(String title, String description, User teacher) {
        Course course = Course.builder()
                .title(title)
                .description(description)
                .teacher(teacher)
                .published(false)
                .build();
        return courseRepository.save(course);
    }

    @Transactional
    @CacheEvict(value = {"courses", "courseDetails", "popularCourses"}, allEntries = true)
    public Course updateCourse(Long id, String title, String description, Long teacherId) {
        Course course = getCourseEntity(id);
        validateTeacherAccess(course, teacherId);
        course.setTitle(title);
        course.setDescription(description);
        return courseRepository.save(course);
    }

    @Transactional
    @CacheEvict(value = {"courses", "courseDetails", "popularCourses"}, allEntries = true)
    public void deleteCourse(Long id, Long teacherId) {
        Course course = getCourseEntity(id);
        validateTeacherAccess(course, teacherId);
        courseRepository.delete(course);
    }

    @Transactional
    @CacheEvict(value = {"courses", "courseDetails", "popularCourses"}, allEntries = true)
    public void publishCourse(Long id, Long teacherId, boolean published) {
        Course course = getCourseEntity(id);
        validateTeacherAccess(course, teacherId);
        course.setPublished(published);
        courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public Course getCourseEntity(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Курс не найден"));
    }

    private void validateTeacherAccess(Course course, Long teacherId) {
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new BusinessException("Нет прав для редактирования этого курса");
        }
    }

    private CourseDto enrichCourseDto(CourseDto dto, Long userId) {
        dto.setEnrollmentCount((int) enrollmentRepository.countByCourseId(dto.getId()));

        if (userId != null) {
            enrollmentRepository.findByUserIdAndCourseId(userId, dto.getId())
                    .ifPresent(e -> dto.setProgressPercent(e.getProgressPercent()));
        }
        return dto;
    }
}