package ru.zako.questionservice.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbstractApiResponse<T> {

    @Schema(description = "Флаг успешного выполнения запроса")
    private boolean success;

    @Schema(description = "Сообщение с результатом")
    private String message;

    @Schema(description = "Данные ответа, например, токен доступа")
    private T data;

    public AbstractApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public AbstractApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}