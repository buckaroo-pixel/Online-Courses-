package com.courses.dto;

import com.courses.entity.enums.AssignmentType;
import com.courses.entity.enums.SubmissionStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AssignmentDto {

    private Long id;
    private String title;
    private String description;
    private AssignmentType type;
    private int maxScore;
    private Long lessonId;
    private List<QuestionDto> questions;
    private SubmissionStatus lastSubmissionStatus;
    private Integer lastScore;
}