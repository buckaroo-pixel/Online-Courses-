package com.courses.mapper;

import com.courses.dto.QuestionDto;
import com.courses.entity.Question;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-10T13:22:26+0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class QuestionMapperImpl implements QuestionMapper {

    @Autowired
    private AnswerOptionMapper answerOptionMapper;

    @Override
    public QuestionDto toDto(Question question) {
        if ( question == null ) {
            return null;
        }

        QuestionDto.QuestionDtoBuilder questionDto = QuestionDto.builder();

        questionDto.id( question.getId() );
        questionDto.text( question.getText() );
        questionDto.options( answerOptionMapper.toDtoList( question.getOptions() ) );

        return questionDto.build();
    }

    @Override
    public List<QuestionDto> toDtoList(List<Question> questions) {
        if ( questions == null ) {
            return null;
        }

        List<QuestionDto> list = new ArrayList<QuestionDto>( questions.size() );
        for ( Question question : questions ) {
            list.add( toDto( question ) );
        }

        return list;
    }
}
