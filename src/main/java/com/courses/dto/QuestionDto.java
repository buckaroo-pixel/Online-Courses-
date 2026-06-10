package com.courses.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionDto {

    private Long id;
    private String text;
    private List<AnswerOptionDto> options;
}