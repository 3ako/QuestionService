package ru.zako.questionservice.question.question;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public void save(Question question) {
        questionRepository.save(question);
    }

    public void delete(long id) {
        questionRepository.deleteById(id);
    }

    public List<Question> getByTestId(long testId) {
        return questionRepository.findByTestId(testId);
    }

    @Nullable
    public Question findById(long id) {
        return questionRepository.findById(id).orElse(null);
    }
}
