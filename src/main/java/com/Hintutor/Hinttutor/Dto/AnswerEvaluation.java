package com.Hintutor.Hinttutor.Dto;

public record AnswerEvaluation(
        boolean understood,
        String feedback,
        String nextHint
) {}