package ru.zako.questionservice.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserDto {
    @Schema(description = "Имя пользователя", example = "user123")
    private String username;
    @Schema(description = "Пароль пользователя", example = "password123")
    private String password;
}