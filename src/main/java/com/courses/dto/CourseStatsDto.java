package com.courses.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseStatsDto {

    private Long courseId;
    private String courseTitle;
    private long enrollmentCount;
    private double averageProgress;
}