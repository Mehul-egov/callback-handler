package org.qwikpe.callback.handler.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final int errorCode;

    public CustomException(String errorReason, int errorCode) {
        super(errorReason);
        this.errorCode = errorCode;
    }
}
