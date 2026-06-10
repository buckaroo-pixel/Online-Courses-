package com.courses.mapper;

import com.courses.dto.LessonDto;
import com.courses.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    @Mapping(target = "completed", ignore = true)
    @Mapping(target = "sectionId", source = "section.id")
    @Mapping(target = "courseId", source = "section.course.id")
    LessonDto toDto(Lesson lesson);

    List<LessonDto> toDtoList(List<Lesson> lessons);
}