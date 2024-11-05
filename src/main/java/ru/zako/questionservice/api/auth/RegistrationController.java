package ru.zako.questionservice.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.zako.questionservice.api.ApiResponse;
import ru.zako.questionservice.user.UserDto;
import ru.zako.questionservice.user.UserService;

@RestController @RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody UserDto userDto) {
        try {
            if (userService.existsByUsername(userDto.getUsername())) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Username already exists"), HttpStatus.BAD_REQUEST);
            }

            userService.registerUser(userDto);
            return new ResponseEntity<>(new ApiResponse<>(true, "User registered successfully"), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred during registration"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}