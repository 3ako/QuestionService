package ru.zako.questionservice.api.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.zako.questionservice.api.AbstractApiResponse;
import ru.zako.questionservice.user.UserDto;
import ru.zako.questionservice.user.UserService;

@RestController @RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;


    @Operation(summary = "Регистрация пользователя",
            description = "Регистрации нового пользователя с указанием имени пользователя и пароля.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Пользователь успешно зарегистрирован",
                    content = @Content(
                            schema = @Schema(implementation = AbstractApiResponse.class)
                    )),
            @ApiResponse(responseCode = "400",
                    description = "Имя пользователя уже существует",
                    content = @Content(
                            schema = @Schema(implementation = AbstractApiResponse.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Произошла ошибка во время регистрации",
                    content = @Content(
                            schema = @Schema(implementation = AbstractApiResponse.class)
                    ))
    })
    @PostMapping("/register")
    public ResponseEntity<AbstractApiResponse<?>> registerUser(@RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные для регистрации пользователя") UserDto userDto) {
        try {
            if (userService.existsByUsername(userDto.getUsername())) {
                return new ResponseEntity<>(new AbstractApiResponse<>(false, "Username already exists"), HttpStatus.BAD_REQUEST);
            }

            userService.registerUser(userDto);
            return new ResponseEntity<>(new AbstractApiResponse<>(true, "User registered successfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new AbstractApiResponse<>(false, "An error occurred during registration"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}