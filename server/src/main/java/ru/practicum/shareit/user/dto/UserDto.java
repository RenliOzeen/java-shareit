package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PROTECTED)
public class UserDto {
    Long id;
    @NotBlank
    String name;
    @NotBlank
    @Email(message = "incorrect email")
    String email;
}
