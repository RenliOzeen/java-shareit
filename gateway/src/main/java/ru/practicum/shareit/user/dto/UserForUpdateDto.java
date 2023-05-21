package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;

@Value
@Builder
public class UserForUpdateDto {
    Long id;
    String name;
    @Email
    String email;
}
