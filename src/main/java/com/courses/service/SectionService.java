package com.courses.service;

import com.courses.entity.Course;
import com.courses.entity.Section;
import com.courses.exception.ResourceNotFoundException;
import com.courses.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final CourseService courseService;

    @Transactional(readOnly = true)
    public List<Section> getByCourseId(Long courseId) {
        return sectionRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
    }

    @Transactional
    @CacheEvict(value = {"courses", "courseDetails"}, allEntries = true)
    public Section createSection(Long courseId, String title, int orderIndex, Long teacherId) {
        Course course = courseService.getCourseEntity(courseId);
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new ResourceNotFoundException("Нет доступа");
        }
        Section section = Section.builder()
                .course(course)
                .title(title)
                .orderIndex(orderIndex)
                .build();
        return sectionRepository.save(section);
    }

    @Transactional
    @CacheEvict(value = {"courses", "courseDetails"}, allEntries = true)
    public void deleteSection(Long sectionId, Long teacherId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Раздел не найден"));
        if (!section.getCourse().getTeacher().getId().equals(teacherId)) {
            throw new ResourceNotFoundException("Нет доступа");
        }
        sectionRepository.delete(section);
    }
}