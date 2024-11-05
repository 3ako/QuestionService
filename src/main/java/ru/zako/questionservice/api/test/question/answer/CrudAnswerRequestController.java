package ru.zako.questionservice.api.test.question.answer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.zako.questionservice.api.ApiResponse;
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
public class CrudAnswerRequestController {

    private final QuestionService questionService;
    private final AnswerService answerService;

    @GetMapping("/getall")
    public ResponseEntity<ApiResponse<?>> getAll(@AuthenticationPrincipal User user, @RequestParam long questionId) {
        final Question question = questionService.findById(questionId);
        if (question == null) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Question not found"), HttpStatus.NOT_FOUND);
        }
        if (!question.getTest().getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "No permission"));
        }
        final List<AnswerDTO> answers = question.getAnswers()
                .stream()
                .map(a -> new AnswerDTO(a, false))
                .toList();
        return ResponseEntity.ok(new ApiResponse<>(true, null, answers));
    }

    @GetMapping("/create")
    public ResponseEntity<ApiResponse<?>> create(@AuthenticationPrincipal User user, @ModelAttribute CreateAnswerRequest request) {
        final Question question = questionService.findById(request.questionId());
        if (question == null) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Question not found"), HttpStatus.NOT_FOUND);
        }
        if (!question.getTest().getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "No permission"));
        }
        final Answer answer = new Answer();
        answer.setText(request.text());
        answer.setQuestion(question);
        answer.setCorrect(request.isCorrect());

        answerService.save(answer);
        question.getAnswers().add(answer);

        return ResponseEntity.ok(new ApiResponse<>(true, null, new AnswerDTO(answer, true)));
    }

}
