package br.com.person.exception;

public class AgePersonValidationException extends RuntimeException {
    public AgePersonValidationException(String message) {
        super(message);
    }
}
