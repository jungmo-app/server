package jungmo.server.global.error.exception;

import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.ErrorResponse;

import java.util.List;

public class AuthenticateException extends BusinessException{
    public AuthenticateException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public AuthenticateException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthenticateException(ErrorCode errorCode, List<ErrorResponse.FieldError> errors) {
        super(errorCode, errors);
    }
}
