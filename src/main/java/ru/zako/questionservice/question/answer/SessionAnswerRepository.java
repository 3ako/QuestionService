package ru.zako.questionservice.question.answer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionAnswerRepository extends JpaRepository<SessionAnswer, Long> {
}