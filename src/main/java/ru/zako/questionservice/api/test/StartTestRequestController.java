package ru.zako.questionservice.api.test;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zako.questionservice.api.ApiResponse;
import ru.zako.questionservice.question.test.TestService;

@RestController
@RequestMapping("/start")
@RequiredArgsConstructor
public class StartTestRequestController {

    private final TestService testService;
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse<?>> start(@PathVariable("id") long id) {
//
//    }

}
