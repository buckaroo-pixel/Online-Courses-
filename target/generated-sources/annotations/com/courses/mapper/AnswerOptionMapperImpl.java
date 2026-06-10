package com.courses.mapper;

import com.courses.dto.AnswerOptionDto;
import com.courses.entity.AnswerOption;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-10T13:22:26+0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class AnswerOptionMapperImpl implements AnswerOptionMapper {

    @Override
    public AnswerOptionDto toDto(AnswerOption option) {
        if ( option == null ) {
            return null;
        }

        AnswerOptionDto.AnswerOptionDtoBuilder answerOptionDto = AnswerOptionDto.builder();

        answerOptionDto.text( option.getText() );
        answerOptionDto.id( option.getId() );

        return answerOptionDto.build();
    }

    @Override
    public List<AnswerOptionDto> toDtoList(List<AnswerOption> options) {
        if ( options == null ) {
            return null;
        }

        List<AnswerOptionDto> list = new ArrayList<AnswerOptionDto>( options.size() );
        for ( AnswerOption answerOption : options ) {
            list.add( toDto( answerOption ) );
        }

        return list;
    }
}
