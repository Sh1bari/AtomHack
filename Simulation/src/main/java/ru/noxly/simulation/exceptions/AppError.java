package ru.noxly.simulation.exceptions;

import lombok.Data;
import ru.noxly.validation.exceptions.ValidationErrorDto;

import java.util.Date;
import java.util.List;

/**
 * @author Vladimir Krasnov
 */
@Data
public class AppError {
    private int status;
    private String message;
    private Date timestamp;
    private List<ValidationErrorDto> errors;

    public AppError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }

    public AppError(int status, String message, List<ValidationErrorDto> errors) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
        this.errors = errors;
    }
}
