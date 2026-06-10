package com.courses.mapper;

import com.courses.dto.LessonDto;
import com.courses.entity.Course;
import com.courses.entity.Lesson;
import com.courses.entity.Section;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-10T13:22:27+0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class LessonMapperImpl implements LessonMapper {

    @Override
    public LessonDto toDto(Lesson lesson) {
        if ( lesson == null ) {
            return null;
        }

        LessonDto.LessonDtoBuilder lessonDto = LessonDto.builder();

        lessonDto.sectionId( lessonSectionId( lesson ) );
        lessonDto.courseId( lessonSectionCourseId( lesson ) );
        lessonDto.id( lesson.getId() );
        lessonDto.title( lesson.getTitle() );
        lessonDto.content( lesson.getContent() );
        lessonDto.type( lesson.getType() );
        lessonDto.mediaUrl( lesson.getMediaUrl() );
        lessonDto.orderIndex( lesson.getOrderIndex() );
        lessonDto.durationMinutes( lesson.getDurationMinutes() );

        return lessonDto.build();
    }

    @Override
    public List<LessonDto> toDtoList(List<Lesson> lessons) {
        if ( lessons == null ) {
            return null;
        }

        List<LessonDto> list = new ArrayList<LessonDto>( lessons.size() );
        for ( Lesson lesson : lessons ) {
            list.add( toDto( lesson ) );
        }

        return list;
    }

    private Long lessonSectionId(Lesson lesson) {
        Section section = lesson.getSection();
        if ( section == null ) {
            return null;
        }
        return section.getId();
    }

    private Long lessonSectionCourseId(Lesson lesson) {
        Section section = lesson.getSection();
        if ( section == null ) {
            return null;
        }
        Course course = section.getCourse();
        if ( course == null ) {
            return null;
        }
        return course.getId();
    }
}
