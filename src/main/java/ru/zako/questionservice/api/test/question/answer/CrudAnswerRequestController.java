package ru.zako.questionservice.api.test.question.answer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.zako.questionservice.api.AbstractApiResponse;
import ru.zako.questionservice.api.test.question.answer.request.CreateAnswerRequest;
import ru.zako.questionservice.question.answer.Answer;
import ru.zako.questionservice.question.answer.AnswerDTO;
import ru.zako.questionservice.question.answer.AnswerService;
import ru.zako.questionservice.question.question.Question;
import ru.zako.questionservice.question.question.QuestionService;
import ru.zako.questionservice.user.User;

import java.util.List;

@RestController
@RequestMapping("/test/question/answer")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class CrudAnswerRequestController {

    private final QuestionService questionService;
    private final AnswerService answerService;

    @GetMapping("/getall")
    @Operation(summary = "Получить все ответы на вопрос", description = "Возвращает все ответы для заданного вопроса, если пользователь является создателем теста.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответы успешно получены"),
            @ApiResponse(responseCode = "404", description = "Вопрос не найден"),
            @ApiResponse(responseCode = "403", description = "Нет разрешения на доступ к ответам")
    })
    @Parameter(name = "Authorization", description = "Токен доступа", in = ParameterIn.HEADER, required = true)
    public ResponseEntity<AbstractApiResponse<?>> getAll(@AuthenticationPrincipal User user,
                                                         @RequestParam long questionId) {
        final Question question = questionService.findById(questionId);
        if (question == null) {
            return new ResponseEntity<>(new AbstractApiResponse<>(false, "Question not found"), HttpStatus.NOT_FOUND);
        }
        if (!question.getTest().getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AbstractApiResponse<>(false, "No permission"));
        }
        final List<AnswerDTO> answers = question.getAnswers()
                .stream()
                .map(a -> new AnswerDTO(a, false))
                .toList();
        return ResponseEntity.ok(new AbstractApiResponse<>(true, null, answers));
    }

    @PostMapping("/create")
    @Operation(summary = "Создать ответ", description = "Создает новый ответ для заданного вопроса, если пользователь является создателем теста.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ успешно создан"),
            @ApiResponse(responseCode = "404", description = "Вопрос не найден"),
            @ApiResponse(responseCode = "403", description = "Нет разрешения на создание ответа"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @Parameter(name = "Authorization", description = "Токен доступа", in = ParameterIn.HEADER, required = true)
    public ResponseEntity<AbstractApiResponse<?>> create(@AuthenticationPrincipal User user,
                                                         @RequestBody CreateAnswerRequest request) {
        final Question question = questionService.findById(request.questionId());
        if (question == null) {
            return new ResponseEntity<>(new AbstractApiResponse<>(false, "Question not found"), HttpStatus.NOT_FOUND);
        }
        if (!question.getTest().getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AbstractApiResponse<>(false, "No permission"));
        }
        final Answer answer = new Answer();
        answer.setText(request.text());
        answer.setQuestion(question);
        answer.setCorrect(request.isCorrect());

        answerService.save(answer);
        question.getAnswers().add(answer);

        return ResponseEntity.ok(new AbstractApiResponse<>(true, null, new AnswerDTO(answer, true)));
    }
}
