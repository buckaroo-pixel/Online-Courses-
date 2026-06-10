package com.courses.mapper;

import com.courses.dto.CourseDto;
import com.courses.dto.SectionDto;
import com.courses.entity.Course;
import com.courses.entity.Section;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SectionMapper.class})
public interface CourseMapper {

    @Mapping(target = "teacherName", source = "teacher.fullName")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "sectionCount", expression = "java(course.getSections() != null ? course.getSections().size() : 0)")
    @Mapping(target = "lessonCount", source = "course", qualifiedByName = "countLessons")
    @Mapping(target = "enrollmentCount", ignore = true)
    @Mapping(target = "progressPercent", ignore = true)
    CourseDto toDto(Course course);

    @Mapping(target = "sections", source = "sections")
    @Mapping(target = "teacherName", source = "teacher.fullName")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "sectionCount", expression = "java(course.getSections() != null ? course.getSections().size() : 0)")
    @Mapping(target = "lessonCount", source = "course", qualifiedByName = "countLessons")
    @Mapping(target = "enrollmentCount", ignore = true)
    @Mapping(target = "progressPercent", ignore = true)
    CourseDto toDetailDto(Course course);

    @Named("countLessons")
    default int countLessons(Course course) {
        if (course.getSections() == null) {
            return 0;
        }
        return course.getSections().stream()
                .mapToInt(s -> s.getLessons() != null ? s.getLessons().size() : 0)
                .sum();
    }
}