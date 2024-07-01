package org.qwikpe.callback.handler.exception;

import lombok.Getter;

public class CustomException extends RuntimeException{

    @Getter
    private final int errorCode;

    public CustomException(String errorReason, int errorCode) {
        super(errorReason);
        this.errorCode = errorCode;
    }
}
