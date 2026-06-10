package com.courses.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentStatsDto {

    private Long userId;
    private String fullName;
    private int enrolledCourses;
    private int completedLessons;
    private int totalScore;
    private double averageProgress;
}