package com.courses.service;

import com.courses.dto.LessonDto;
import com.courses.entity.Lesson;
import com.courses.entity.Section;
import com.courses.entity.enums.LessonType;
import com.courses.exception.ResourceNotFoundException;
import com.courses.mapper.LessonMapper;
import com.courses.repository.LessonProgressRepository;
import com.courses.repository.LessonRepository;
import com.courses.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final SectionRepository sectionRepository;
    private final LessonProgressRepository progressRepository;
    private final LessonMapper lessonMapper;

    @Transactional(readOnly = true)
    public LessonDto getLesson(Long lessonId, Long userId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));
        LessonDto dto = lessonMapper.toDto(lesson);
        if (userId != null) {
            progressRepository.findByUserIdAndLessonId(userId, lessonId)
                    .ifPresent(p -> dto.setCompleted(p.isCompleted()));
        }
        return dto;
    }

    @Transactional
    @CacheEvict(value = {"courses", "courseDetails"}, allEntries = true)
    public Lesson createLesson(Long sectionId, String title, String content, LessonType type,
                               String mediaUrl, int orderIndex, Integer duration, Long teacherId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Раздел не найден"));
        if (!section.getCourse().getTeacher().getId().equals(teacherId)) {
            throw new ResourceNotFoundException("Нет доступа");
        }
        Lesson lesson = Lesson.builder()
                .section(section)
                .title(title)
                .content(content)
                .type(type)
                .mediaUrl(mediaUrl)
                .orderIndex(orderIndex)
                .durationMinutes(duration)
                .build();
        return lessonRepository.save(lesson);
    }

    @Transactional(readOnly = true)
    public List<LessonDto> getLessonsWithProgress(Long courseId, Long userId) {
        List<Lesson> lessons = lessonRepository.findAllByCourseId(courseId);
        List<Long> lessonIds = lessons.stream().map(Lesson::getId).toList();
        Map<Long, Boolean> progressMap = progressRepository.findByUserIdAndLessonIdIn(userId, lessonIds)
                .stream()
                .collect(Collectors.toMap(p -> p.getLesson().getId(), p -> p.isCompleted()));

        return lessons.stream()
                .map(lesson -> {
                    LessonDto dto = lessonMapper.toDto(lesson);
                    dto.setCompleted(progressMap.getOrDefault(lesson.getId(), false));
                    return dto;
                })
                .toList();
    }
}