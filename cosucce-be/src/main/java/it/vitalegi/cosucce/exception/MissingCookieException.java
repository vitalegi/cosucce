package it.vitalegi.cosucce.exception;

public class MissingCookieException extends RuntimeException {
    public MissingCookieException(String message) {
        super(message);
    }
}
