package com.courses.dto;

import com.courses.entity.enums.LessonType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LessonDto {

    private Long id;
    private String title;
    private String content;
    private LessonType type;
    private String mediaUrl;
    private int orderIndex;
    private Integer durationMinutes;
    private boolean completed;
    private Long sectionId;
    private Long courseId;
}