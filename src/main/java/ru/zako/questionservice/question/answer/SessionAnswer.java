package ru.zako.questionservice.question.answer;

import jakarta.persistence.*;
import lombok.Data;
import ru.zako.questionservice.question.test.TestSession;
import ru.zako.questionservice.question.question.Question;

import java.time.LocalDateTime;

@Entity @Data
public class SessionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private TestSession session;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
}