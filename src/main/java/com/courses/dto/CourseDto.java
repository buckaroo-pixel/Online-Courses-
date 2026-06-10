package com.courses.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CourseDto {

    private Long id;
    private String title;
    private String description;
    private boolean published;
    private String teacherName;
    private Long teacherId;
    private LocalDateTime createdAt;
    private int sectionCount;
    private int lessonCount;
    private int enrollmentCount;
    private int progressPercent;
    private List<SectionDto> sections;
}