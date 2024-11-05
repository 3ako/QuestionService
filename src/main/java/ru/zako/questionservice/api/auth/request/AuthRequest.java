package ru.zako.questionservice.api.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запрос на аутентификацию пользователя")
public class AuthRequest {
    @Schema(description = "Имя пользователя", example = "user123")
    private String username;

    @Schema(description = "Пароль пользователя", example = "password123")
    private String password;
}
