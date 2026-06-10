package com.courses.mapper;

import com.courses.dto.SectionDto;
import com.courses.entity.Section;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-10T13:22:27+0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class SectionMapperImpl implements SectionMapper {

    @Autowired
    private LessonMapper lessonMapper;

    @Override
    public SectionDto toDto(Section section) {
        if ( section == null ) {
            return null;
        }

        SectionDto.SectionDtoBuilder sectionDto = SectionDto.builder();

        sectionDto.id( section.getId() );
        sectionDto.title( section.getTitle() );
        sectionDto.orderIndex( section.getOrderIndex() );
        sectionDto.lessons( lessonMapper.toDtoList( section.getLessons() ) );

        return sectionDto.build();
    }

    @Override
    public List<SectionDto> toDtoList(List<Section> sections) {
        if ( sections == null ) {
            return null;
        }

        List<SectionDto> list = new ArrayList<SectionDto>( sections.size() );
        for ( Section section : sections ) {
            list.add( toDto( section ) );
        }

        return list;
    }
}
