package ru.noxly.simulation.exceptions;

import lombok.Data;

import java.util.Date;

/**
 * @author Vladimir Krasnov
 */
@Data
public abstract class GlobalAppException extends RuntimeException {
    protected int status;
    protected String message;
    protected Date timestamp;

    public GlobalAppException(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}
