package com.courses.mapper;

import com.courses.dto.CourseDto;
import com.courses.entity.Course;
import com.courses.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-10T13:22:27+0300",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class CourseMapperImpl implements CourseMapper {

    @Autowired
    private SectionMapper sectionMapper;

    @Override
    public CourseDto toDto(Course course) {
        if ( course == null ) {
            return null;
        }

        CourseDto.CourseDtoBuilder courseDto = CourseDto.builder();

        courseDto.teacherName( courseTeacherFullName( course ) );
        courseDto.teacherId( courseTeacherId( course ) );
        courseDto.lessonCount( countLessons( course ) );
        courseDto.id( course.getId() );
        courseDto.title( course.getTitle() );
        courseDto.description( course.getDescription() );
        courseDto.published( course.isPublished() );
        courseDto.createdAt( course.getCreatedAt() );
        courseDto.sections( sectionMapper.toDtoList( course.getSections() ) );

        courseDto.sectionCount( course.getSections() != null ? course.getSections().size() : 0 );

        return courseDto.build();
    }

    @Override
    public CourseDto toDetailDto(Course course) {
        if ( course == null ) {
            return null;
        }

        CourseDto.CourseDtoBuilder courseDto = CourseDto.builder();

        courseDto.sections( sectionMapper.toDtoList( course.getSections() ) );
        courseDto.teacherName( courseTeacherFullName( course ) );
        courseDto.teacherId( courseTeacherId( course ) );
        courseDto.lessonCount( countLessons( course ) );
        courseDto.id( course.getId() );
        courseDto.title( course.getTitle() );
        courseDto.description( course.getDescription() );
        courseDto.published( course.isPublished() );
        courseDto.createdAt( course.getCreatedAt() );

        courseDto.sectionCount( course.getSections() != null ? course.getSections().size() : 0 );

        return courseDto.build();
    }

    private String courseTeacherFullName(Course course) {
        User teacher = course.getTeacher();
        if ( teacher == null ) {
            return null;
        }
        return teacher.getFullName();
    }

    private Long courseTeacherId(Course course) {
        User teacher = course.getTeacher();
        if ( teacher == null ) {
            return null;
        }
        return teacher.getId();
    }
}
