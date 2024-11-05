package ru.zako.questionservice.question.test;

import jakarta.persistence.*;
import lombok.*;
import ru.zako.questionservice.question.question.Question;
import ru.zako.questionservice.user.User;

import java.util.*;

@Entity @Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "test", fetch = FetchType.LAZY) // Убедитесь, что используется LAZY
    private Set<Question> questions;// Инициализация коллекции


    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(nullable = false)
    private int amountQuestions;

//    @OneToMany(mappedBy = "test")
//    private Set<TestSession> testSessions;

    @Column(nullable = false)
    private Date createDate;
}
