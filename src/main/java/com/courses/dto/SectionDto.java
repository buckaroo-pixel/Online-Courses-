package com.courses.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SectionDto {

    private Long id;
    private String title;
    private int orderIndex;
    private List<LessonDto> lessons;
}