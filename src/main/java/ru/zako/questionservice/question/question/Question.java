package ru.zako.questionservice.question.question;

import jakarta.persistence.*;
import lombok.*;
import ru.zako.questionservice.question.answer.SessionAnswer;
import ru.zako.questionservice.question.test.Test;
import ru.zako.questionservice.question.answer.Answer;

import java.util.Date;
import java.util.Set;

@Entity @Getter
@AllArgsConstructor @NoArgsConstructor
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    private Test test;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private int maxTry;

    @Column(nullable = false)
    private Date createDate;

    @OneToMany(mappedBy = "question")
    private Set<Answer> answers;

    @OneToMany(mappedBy = "question")
    private Set<SessionAnswer> sessionAnswers;
}