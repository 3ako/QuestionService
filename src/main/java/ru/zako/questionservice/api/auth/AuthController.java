package ru.zako.questionservice.api.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.zako.questionservice.api.AbstractApiResponse;
import ru.zako.questionservice.api.auth.request.AuthRequest;
import ru.zako.questionservice.user.User;
import ru.zako.questionservice.user.UserService;
import ru.zako.questionservice.util.JwtUtil;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    private final JwtUtil jwtUtil;

    @Operation(summary = "Аутентификация пользователя",
            description = "Аутентифицирует пользователя по имени пользователя и паролю, возвращает JWT токен при успешной аутентификации.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Аутентификация успешна, возвращен JWT токен",
                    content = @Content(
                            schema = @Schema(implementation = AbstractApiResponse.class) // Указание класса ответа
                    )),
            @ApiResponse(responseCode = "401",
                    description = "Неверные имя пользователя или пароль",
                    content = @Content(
                            schema = @Schema(implementation = AbstractApiResponse.class) // Указание класса ответа
                    ))
    })
    @PostMapping("/login")
    public ResponseEntity<AbstractApiResponse<?>> authenticate(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Данные для аутентификации") AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.getUserByUsername(userDetails.getUsername()); // Получаем объект User из базы данных

            assert user != null;
            String token = jwtUtil.generateAccessToken(user);

            return new ResponseEntity<>(new AbstractApiResponse<>(true, null, token), HttpStatus.OK); // Возвращаем токен
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(new AbstractApiResponse<>(false, e.getMessage(), null), HttpStatus.UNAUTHORIZED);
        }
    }

}