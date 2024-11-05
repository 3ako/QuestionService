package ru.zako.questionservice.question.test;

import jakarta.persistence.*;
import ru.zako.questionservice.question.answer.SessionAnswer;
import ru.zako.questionservice.user.User;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class TestSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @OneToMany(mappedBy = "session")
    private Set<SessionAnswer> sessionAnswers;
}