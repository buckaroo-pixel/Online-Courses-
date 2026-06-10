package com.courses.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerOptionDto {

    private Long id;
    private String text;
}