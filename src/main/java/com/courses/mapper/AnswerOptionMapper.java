package com.courses.mapper;

import com.courses.dto.AnswerOptionDto;
import com.courses.entity.AnswerOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnswerOptionMapper {

    @Mapping(target = "text", source = "text")
    AnswerOptionDto toDto(AnswerOption option);

    List<AnswerOptionDto> toDtoList(List<AnswerOption> options);
}