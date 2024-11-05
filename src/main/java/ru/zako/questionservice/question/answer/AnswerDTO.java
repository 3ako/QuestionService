package ru.zako.questionservice.question.answer;

public class AnswerDTO {
    private long id;
    private long questionId;
    private String text;
    private Boolean isCorrect;

    public AnswerDTO(Answer answer, boolean saveCorrect) {
        this.id = answer.getId();
        this.questionId = answer.getQuestion().getId();
        this.text = answer.getText();
        if (saveCorrect)
            this.isCorrect = answer.isCorrect();
    }
}
