package com.arka99.projectreactordemo.exception;

public class ReactorException extends Throwable {
    private Throwable exception;
    private String message;

    public ReactorException(Throwable exception, String message) {
        this.exception = exception;
        this.message = message;

    }
}