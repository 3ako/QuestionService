package ru.zako.questionservice.question.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor @Getter
public class TestDTO {
    private long id;
    private String title;
    private int amountQuestions;
    private Date createDate;

    public TestDTO(Test test) {
        this.id = test.getId();
        this.title = test.getTitle();
        this.amountQuestions = test.getAmountQuestions();
        this.createDate = test.getCreateDate();
    }
}
