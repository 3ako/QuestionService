package ru.zako.questionservice.question.answer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;


    public void save(Answer answer) {
        this.answerRepository.save(answer);
    }
}
