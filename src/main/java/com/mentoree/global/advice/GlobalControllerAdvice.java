package com.mentoree.global.advice;

import com.mentoree.global.exception.ErrorCode;
import com.mentoree.global.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<ErrorResponse> handleBadCredentialException(BadCredentialsException e) {
        log.error("[BadCredentialsException] error: ", e.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.BAD_CREDENTIALS);
        return new ResponseEntity<ErrorResponse>(response,HttpStatus.UNAUTHORIZED);
    }

}
