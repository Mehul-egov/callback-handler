package org.qwikpe.callback.handler.exception;

import lombok.Getter;

@Getter
public class CallbackResponseNotAsExpectedException extends RuntimeException{

    public CallbackResponseNotAsExpectedException(String errorReason) {
        super(errorReason);
    }
}
