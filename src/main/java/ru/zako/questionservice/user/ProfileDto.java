package ru.zako.questionservice.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ProfileDto {
    private long id;
    private String email;

    public ProfileDto(User user) {
        this.id = user.getId();
        this.email = user.getUsername();
    }
}
