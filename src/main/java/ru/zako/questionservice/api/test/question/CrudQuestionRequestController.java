package ru.zako.questionservice.api.test.question;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.zako.questionservice.api.AbstractApiResponse;
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
@SecurityRequirement(name = "Authorization") // Указывает, что для всех методов требуется авторизация
public class CrudQuestionRequestController {
    private final QuestionService questionService;
    private final TestService testService;

    @GetMapping("/getAll")
    @Operation(summary = "Получить все вопросы теста", description = "Возвращает все вопросы, связанные с тестом, если пользователь является его создателем.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Вопросы успешно получены"),
            @ApiResponse(responseCode = "404", description = "Тест не найден"),
            @ApiResponse(responseCode = "403", description = "Нет разрешения на доступ к вопросам")
    })
    @Parameter(name = "Authorization", description = "Токен доступа", in = ParameterIn.HEADER, required = true)
    public ResponseEntity<AbstractApiResponse<?>> getAll(@AuthenticationPrincipal User user,
                                                         @RequestParam long testId) {

        System.out.println("Test: "+ testId);
        final Test test = testService.getById(testId);
        if (test == null) {
            return new ResponseEntity<>(new AbstractApiResponse<>(false, "Test not found"), HttpStatus.NOT_FOUND);
        }
        if (!test.getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AbstractApiResponse<>(false, "No permission"));
        }

        // Инициализируем коллекцию
        Hibernate.initialize(test.getQuestions());

        return ResponseEntity.ok(new AbstractApiResponse<>(true, null, test.getQuestions().stream().map(QuestionDTO::new).toList()));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Удалить вопрос", description = "Удаляет вопрос по его идентификатору, если пользователь является создателем теста.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Вопрос успешно удален"),
            @ApiResponse(responseCode = "404", description = "Вопрос не найден"),
            @ApiResponse(responseCode = "403", description = "Нет разрешения на удаление вопроса")
    })
    @Parameter(name = "Authorization", description = "Токен доступа", in = ParameterIn.HEADER, required = true)
    public ResponseEntity<AbstractApiResponse<?>> delete(@AuthenticationPrincipal User user,
                                                         @RequestParam long questionId) {
        final Question question = questionService.findById(questionId);
        if (question == null) {
            return new ResponseEntity<>(new AbstractApiResponse<>(false, "Question not found"), HttpStatus.NOT_FOUND);
        }
        final Test test = question.getTest();
        if (!test.getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AbstractApiResponse<>(false, "No permission"));
        }

        questionService.delete(questionId);
        return ResponseEntity.ok(new AbstractApiResponse<>(true, null));
    }

    @PostMapping("/create")
    @Operation(summary = "Создать вопрос", description = "Создает новый вопрос для теста, если пользователь является его создателем.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Вопрос успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или тест не найден"),
            @ApiResponse(responseCode = "403", description = "Нет разрешения на создание вопроса")
    })
    @Parameter(name = "Authorization", description = "Токен доступа", in = ParameterIn.HEADER, required = true)
    public ResponseEntity<AbstractApiResponse<?>> create(@AuthenticationPrincipal User user,
                                                         @RequestBody CreateQuestionRequest request) {

        System.out.println("REQ: "+request);
        if (request.testId() < 0 || request.text() == null || request.maxTry() <= 0) {
            return ResponseEntity.badRequest().body(new AbstractApiResponse<>(false, "Invalid data"));
        }

        final Test test = testService.getById(request.testId());
        if (test == null) {
            return ResponseEntity.badRequest().body(new AbstractApiResponse<>(false, "Test not found"));
        }
        if (!Objects.equals(test.getCreator().getId(), user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AbstractApiResponse<>(false, "No permission"));
        }

        final Question question = new Question();
        question.setTest(test);
        question.setText(request.text());
        question.setMaxTry(request.maxTry());
        question.setCreateDate(new Date());

        test.getQuestions().add(question);
        questionService.save(question);

        return ResponseEntity.ok(new AbstractApiResponse<>(true, null, new QuestionDTO(question)));
    }
}