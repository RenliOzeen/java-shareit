package ru.practicum.shareit.exceptions;

public class InvalidArgumentsException extends RuntimeException {
    InvalidArgumentsException(String message) {
        super(message);
    }
}
