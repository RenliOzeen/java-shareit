package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PROTECTED)
public class UserDto {
    Long id;
    String name;
    @NotEmpty
    @Email(message = "incorrect email")
    String email;
}
