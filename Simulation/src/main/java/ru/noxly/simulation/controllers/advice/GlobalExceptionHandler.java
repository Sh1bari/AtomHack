package ru.noxly.simulation.controllers.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.noxly.simulation.exceptions.AppError;
import ru.noxly.simulation.exceptions.GlobalAppException;
import ru.noxly.validation.exceptions.ValidationException;

import java.util.stream.Collectors;

/**
 * @author Vladimir Krasnov
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler({
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<AppError> onValidationExceptions(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new AppError(400, exception.getMessage()));
    }

    @ExceptionHandler({
            ValidationException.class
    })
    public ResponseEntity<AppError> onValidationExceptions(ValidationException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new AppError(409, "Ошибки бизнес-логики", exception.getErrorMessages()));
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<AppError> onValidationExceptionsMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getAllErrors().stream()
                .map(o -> o.getDefaultMessage() + ", ")
                .collect(Collectors.joining());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new AppError(400, message.substring(0, message.length() - 2)));
    }

    @ExceptionHandler({
            GlobalAppException.class
    })
    public ResponseEntity<AppError> onGlobalAppException(GlobalAppException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new AppError(ex.getStatus(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleException(Exception e, HttpServletRequest request) {
        AppError error = new AppError(500, e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
