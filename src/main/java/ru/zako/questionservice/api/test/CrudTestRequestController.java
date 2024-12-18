package ru.zako.questionservice.api.test;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.zako.questionservice.api.AbstractApiResponse;
import ru.zako.questionservice.api.test.request.CreateTestRequest;
import ru.zako.questionservice.api.test.request.EditTestRequest;
import ru.zako.questionservice.question.test.Test;
import ru.zako.questionservice.question.test.TestDTO;
import ru.zako.questionservice.question.test.TestService;
import ru.zako.questionservice.user.User;

import java.util.Date;
import java.util.List;
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

    @GetMapping("/getall")
    @Operation(summary = "Редактировать тест", description = "Получает все тесты, которые создавались пользователем.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успех"),
            @ApiResponse(responseCode = "403", description = "Нет разрешения")
    })
    @Parameter(name = "Authorization", description = "Токен доступа", in = ParameterIn.HEADER, required = true)
    public ResponseEntity<AbstractApiResponse<?>> getall(@AuthenticationPrincipal User user) {

        List<TestDTO> tests = testService.getAllByUser(user.getId())
                .stream()
                .map(TestDTO::new)
                .toList();

        return ResponseEntity.ok(new AbstractApiResponse<>(true, null, tests));
    }

    @PostMapping("/edit")
    @Operation(summary = "Редактировать тест", description = "Редактирует существующий тест, если пользователь является его создателем.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тест успешно отредактирован"),
            @ApiResponse(responseCode = "400", description = "Некорректный ID или тест не найден"),
            @ApiResponse(responseCode = "403", description = "Нет разрешения на редактирование теста")
    })
    @Parameter(name = "Authorization", description = "Токен доступа", in = ParameterIn.HEADER, required = true)
    public ResponseEntity<AbstractApiResponse<?>> edit(@AuthenticationPrincipal User user, @RequestBody EditTestRequest request) {
        Test test = validateTestOwnership(user, request.id());

        test.setTitle(request.title());
        test.setAmountQuestions(request.amountQuestions());
        testService.save(test);

        return ResponseEntity.ok(new AbstractApiResponse<>(true, null));
    }

    @PostMapping("/create")
    @Operation(summary = "Создать тест", description = "Создает новый тест от имени текущего пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тест успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @Parameter(name = "Authorization", description = "Токен доступа", in = ParameterIn.HEADER, required = true)
    public ResponseEntity<AbstractApiResponse<?>> create(@AuthenticationPrincipal User user, @RequestBody CreateTestRequest request) {
        if (request.title() == null || request.amountQuestions() <= 0) {
            return ResponseEntity.badRequest().body(new AbstractApiResponse<>(false, "Invalid data"));
        }
        final Test test = new Test();
        test.setTitle(request.title());
        test.setAmountQuestions(request.amountQuestions());
        test.setCreator(user);
        test.setCreateDate(new Date());

        testService.save(test);
        return ResponseEntity.ok(new AbstractApiResponse<>(true, null, new TestDTO(test)));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Удалить тест", description = "Удаляет тест, если пользователь является его создателем.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тест успешно удален"),
            @ApiResponse(responseCode = "400", description = "Некорректный ID или тест не найден"),
            @ApiResponse(responseCode = "403", description = "Нет разрешения на удаление теста")
    })
    @Parameter(name = "Authorization", description = "Токен доступа", in = ParameterIn.HEADER, required = true)
    public ResponseEntity<AbstractApiResponse<?>> delete(@AuthenticationPrincipal User user, long id) {
        Test test = validateTestOwnership(user, id);
        testService.delete(test.getId());

        return ResponseEntity.ok(new AbstractApiResponse<>(true, null));
    }
}