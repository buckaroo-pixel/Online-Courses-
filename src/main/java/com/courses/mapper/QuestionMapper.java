package com.courses.mapper;

import com.courses.dto.QuestionDto;
import com.courses.entity.Question;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AnswerOptionMapper.class})
public interface QuestionMapper {

    QuestionDto toDto(Question question);

    List<QuestionDto> toDtoList(List<Question> questions);
}