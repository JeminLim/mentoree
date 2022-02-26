package com.mentoree.global.advice;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.mentoree.global.advice.response.ErrorCode;
import com.mentoree.global.advice.response.ErrorResponse;
import com.mentoree.global.exception.BindingFailureException;
import com.mentoree.global.exception.DuplicateExistException;
import com.mentoree.global.exception.InvalidTokenException;
import com.mentoree.global.exception.NoAuthorityException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    private Logger paramLogger = LoggerFactory.getLogger("ErrorDetail");

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<ErrorResponse> handleBadCredentialException(BadCredentialsException e) {
        log.error("[BadCredentialsException] error: {}", e.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.BAD_CREDENTIALS);
        return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.error("[IllegalStateException] error : {}" ,e.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.ILLEGAL_STATEMENT);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindingFailureException.class)
    protected ResponseEntity<ErrorResponse> handleBindingFailureException(BindingFailureException e) {
        BindingResult bindingResult = e.getBindingResult();
        paramLogger.error("Binding failure : {}", Objects.requireNonNull(bindingResult.getTarget()).getClass().getSimpleName());

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            paramLogger.error("[Target field] - {}", fieldError.getField());
            paramLogger.error("[Param input] - {}", fieldError.getRejectedValue());
        }
        final ErrorResponse response = ErrorResponse.of(ErrorCode.ILLEGAL_PARAMS, bindingResult);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidFormatException.class, InvalidNullException.class})
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(MismatchedInputException e) {

        paramLogger.warn("Parameter convert exception");

        // 포멧 문제일 경우
        if(e instanceof InvalidFormatException) {
            InvalidFormatException exception = (InvalidFormatException) e;
            paramLogger.error("User input : {}", exception.getValue());
            paramLogger.error("Target type : {}", exception.getTargetType());

            final ErrorResponse response = ErrorResponse.of(ErrorCode.ILLEGAL_PARAMS, exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // null 값이 왔을 경우
        if(e instanceof InvalidNullException) {
            InvalidNullException exception = (InvalidNullException) e;
            paramLogger.error("Invalid null occur : {}", exception.getPropertyName());

            final ErrorResponse response = ErrorResponse.of(ErrorCode.ILLEGAL_PARAMS);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        final ErrorResponse response = ErrorResponse.of(ErrorCode.ILLEGAL_PARAMS);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTokenException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException e) {
        log.error("[InvalidTokenException] : {}", e.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_TOKEN, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DuplicateExistException.class)
    protected ResponseEntity<ErrorResponse> handleDuplicateExistException(DuplicateExistException e) {
        log.error("[DuplicateExistException] Duplicate occur : {}", e.getDuplicateEntity().getSimpleName());
        log.error("[DuplicateExistException] error : {}", e.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.ILLEGAL_PARAMS, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoAuthorityException.class)
    protected ResponseEntity<ErrorResponse> handleNoAuthorityException(NoAuthorityException e) {
        log.error("[NoAuthorityException] error : {}", e.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NO_AUTHORITY, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
