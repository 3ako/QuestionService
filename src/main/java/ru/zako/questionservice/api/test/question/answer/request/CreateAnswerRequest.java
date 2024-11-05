package ru.zako.questionservice.api.test.question.answer.request;

public record CreateAnswerRequest(long questionId, String text, boolean isCorrect) {
}
