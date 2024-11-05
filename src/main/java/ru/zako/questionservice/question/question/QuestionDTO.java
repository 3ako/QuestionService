package ru.zako.questionservice.question.question;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class QuestionDTO {
    private long id;
    private String text;
    private long testId;
    private int maxTry;
    private Date createDate;

    public QuestionDTO(Question question) {
        this.id = question.getId();
        this.text = question.getText();
        this.testId = question.getTest().getId();
        this.maxTry = question.getMaxTry();
        this.createDate = question.getCreateDate();
    }
}
