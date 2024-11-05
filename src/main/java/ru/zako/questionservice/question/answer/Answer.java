package ru.zako.questionservice.question.answer;

import jakarta.persistence.*;
import lombok.Data;
import ru.zako.questionservice.question.question.Question;

@Entity @Data
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private Question question;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private boolean isCorrect;
}