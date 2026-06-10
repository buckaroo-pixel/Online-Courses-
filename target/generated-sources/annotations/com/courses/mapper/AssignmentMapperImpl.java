package com.courses.mapper;

import com.courses.dto.AssignmentDto;
import com.courses.entity.Assignment;
import com.courses.entity.Lesson;
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
public class AssignmentMapperImpl implements AssignmentMapper {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public AssignmentDto toDto(Assignment assignment) {
        if ( assignment == null ) {
            return null;
        }

        AssignmentDto.AssignmentDtoBuilder assignmentDto = AssignmentDto.builder();

        assignmentDto.lessonId( assignmentLessonId( assignment ) );
        assignmentDto.id( assignment.getId() );
        assignmentDto.title( assignment.getTitle() );
        assignmentDto.description( assignment.getDescription() );
        assignmentDto.type( assignment.getType() );
        assignmentDto.maxScore( assignment.getMaxScore() );
        assignmentDto.questions( questionMapper.toDtoList( assignment.getQuestions() ) );

        return assignmentDto.build();
    }

    @Override
    public List<AssignmentDto> toDtoList(List<Assignment> assignments) {
        if ( assignments == null ) {
            return null;
        }

        List<AssignmentDto> list = new ArrayList<AssignmentDto>( assignments.size() );
        for ( Assignment assignment : assignments ) {
            list.add( toDto( assignment ) );
        }

        return list;
    }

    private Long assignmentLessonId(Assignment assignment) {
        Lesson lesson = assignment.getLesson();
        if ( lesson == null ) {
            return null;
        }
        return lesson.getId();
    }
}
