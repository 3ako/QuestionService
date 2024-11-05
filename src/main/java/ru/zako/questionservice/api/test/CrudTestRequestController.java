package ru.zako.questionservice.api.test;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.zako.questionservice.api.ApiResponse;
import ru.zako.questionservice.api.test.request.CreateTestRequest;
import ru.zako.questionservice.api.test.request.DeleteTestRequest;
import ru.zako.questionservice.api.test.request.EditTestRequest;
import ru.zako.questionservice.question.test.Test;
import ru.zako.questionservice.question.test.TestDTO;
import ru.zako.questionservice.question.test.TestService;
import ru.zako.questionservice.user.User;

import java.util.Date;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class CrudTestRequestController {

    private final TestService testService;

    private Test validateTestOwnership(User user, long testId) {
        if (testId < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }
        final Test test = testService.getById(testId);
        if (test == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Test not found");
        }
        if (!Objects.equals(test.getCreator().getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permissions");
        }
        return test;
    }

    @PostMapping("/edit")
    public ResponseEntity<ApiResponse<?>> edit(@AuthenticationPrincipal User user, @RequestBody EditTestRequest request) {
        Test test = validateTestOwnership(user, request.id());

        test.setTitle(request.title());
        test.setAmountQuestions(request.amountQuestions());
        testService.save(test);

        return ResponseEntity.ok(new ApiResponse<>(true, null));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> create(@AuthenticationPrincipal User user, @RequestBody CreateTestRequest request) {
        if (request.title() == null || request.amountQuestions() <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Invalid data"));
        }
        final Test test = new Test();
        test.setTitle(request.title());
        test.setAmountQuestions(request.amountQuestions());
        test.setCreator(user);
        test.setCreateDate(new Date());

        testService.save(test);
        return ResponseEntity.ok(new ApiResponse<>(true, null, new TestDTO(test)));
    }

    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<?>> delete(@AuthenticationPrincipal User user, @RequestBody DeleteTestRequest request) {
        Test test = validateTestOwnership(user, request.id());
        testService.delete(test.getId());

        return ResponseEntity.ok(new ApiResponse<>(true, null));
    }
}