package ru.zako.questionservice.api.test.question;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.zako.questionservice.api.ApiResponse;
import ru.zako.questionservice.api.test.question.request.CreateQuestionRequest;
import ru.zako.questionservice.question.question.Question;
import ru.zako.questionservice.question.question.QuestionDTO;
import ru.zako.questionservice.question.question.QuestionService;
import ru.zako.questionservice.question.test.Test;
import ru.zako.questionservice.question.test.TestService;
import ru.zako.questionservice.user.User;

import java.util.Date;
import java.util.Objects;

@RestController
@RequestMapping("/test/question")
@RequiredArgsConstructor
public class CrudQuestionRequestController {
    private final QuestionService questionService;
    private final TestService testService;


    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<?>> getAll(@AuthenticationPrincipal User user, @RequestParam long testId) {
        final Test test = testService.getById(testId);
        if (test == null) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Test not found"), HttpStatus.NOT_FOUND);
        }
        if (!test.getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "No permission"));
        }

        // Инициализируем коллекцию
        Hibernate.initialize(test.getQuestions());

        return ResponseEntity.ok(new ApiResponse<>(true, null, test.getQuestions().stream().map(QuestionDTO::new).toList()));
    }

    @GetMapping("/delete")
    public ResponseEntity<ApiResponse<?>> delete(@AuthenticationPrincipal User user, @RequestParam long questionId) {
        final Question question = questionService.findById(questionId);
        if (question == null) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Question not found"), HttpStatus.NOT_FOUND);
        }
        final Test test = question.getTest();
        if (!test.getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "No permission"));
        }

        questionService.delete(questionId);
        return ResponseEntity.ok(new ApiResponse<>(true, null));
    }

    @GetMapping("/create")
    public ResponseEntity<ApiResponse<?>> create(@AuthenticationPrincipal User user, @ModelAttribute CreateQuestionRequest request) {

        if (request.testId() < 0 || request.text() == null || request.maxTry() <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Invalid data"));
        }

        final Test test = testService.getById(request.testId());
        if (test == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Test not found"));
        }
        if (!Objects.equals(test.getCreator().getId(), user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "No permission"));
        }

        final Question question = new Question();
        question.setTest(test);
        question.setText(request.text());
        question.setMaxTry(request.maxTry());
        question.setCreateDate(new Date());

        test.getQuestions().add(question);
        questionService.save(question);

        return ResponseEntity.ok(new ApiResponse<>(true, null, new QuestionDTO(question)));
    }
}
