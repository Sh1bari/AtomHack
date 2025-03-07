package ru.noxly.simulation.exceptions;

public class GeneralException extends GlobalAppException {
    public GeneralException(int status, String message) {
        super(status, message);
    }
}
