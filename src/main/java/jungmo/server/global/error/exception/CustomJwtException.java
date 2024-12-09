package jungmo.server.global.error.exception;

import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.ErrorResponse;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CustomJwtException extends RuntimeException{

    private ErrorCode errorCode;
    public CustomJwtException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomJwtException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
