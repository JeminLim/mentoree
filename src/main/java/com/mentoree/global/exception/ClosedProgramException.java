package com.mentoree.global.exception;

public class ClosedProgramException extends RuntimeException {

    public ClosedProgramException() {
        super();
    }

    public ClosedProgramException(String message) {
        super(message);
    }

    public ClosedProgramException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClosedProgramException(Throwable cause) {
        super(cause);
    }

}
