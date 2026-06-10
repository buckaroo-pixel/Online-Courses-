package com.courses.mapper;

import com.courses.dto.AssignmentDto;
import com.courses.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {QuestionMapper.class})
public interface AssignmentMapper {

    @Mapping(target = "lessonId", source = "lesson.id")
    @Mapping(target = "lastSubmissionStatus", ignore = true)
    @Mapping(target = "lastScore", ignore = true)
    AssignmentDto toDto(Assignment assignment);

    List<AssignmentDto> toDtoList(List<Assignment> assignments);
}