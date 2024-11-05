package ru.zako.questionservice.question.test;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log
public class TestService {
    private final TestRepository testRepository;

    public List<Test> getUserCreatedTests(long creatorId) {
        return testRepository.findAllByCreator(creatorId);
    }

    @Transactional
    public Test getById(Long id) {
        Test test = testRepository.findById(id).orElse(null);
        if (test != null) {
            test.getQuestions().size(); // Принудительная инициализация
        }
        return test;
    }

    public void delete(long id) {
        testRepository.deleteById(id);
    }

    public void removeTest(long testId) {
        testRepository.deleteById(testId);
    }

    public void save(Test test) {
        testRepository.save(test);
    }
}
