package com.courses.mapper;

import com.courses.dto.SectionDto;
import com.courses.entity.Section;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LessonMapper.class})
public interface SectionMapper {

    SectionDto toDto(Section section);

    List<SectionDto> toDtoList(List<Section> sections);
}