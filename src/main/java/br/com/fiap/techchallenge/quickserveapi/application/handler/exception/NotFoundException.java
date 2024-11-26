package br.com.fiap.techchallenge.quickserveapi.application.handler.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
