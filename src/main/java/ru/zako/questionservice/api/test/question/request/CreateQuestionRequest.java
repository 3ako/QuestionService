package ru.zako.questionservice.api.test.question.request;


public record CreateQuestionRequest(long testId, String text, int maxTry) {
}
