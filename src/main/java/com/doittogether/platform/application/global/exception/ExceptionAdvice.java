package com.doittogether.platform.application.global.exception;

import com.doittogether.platform.application.global.code.ExceptionCode;
import com.doittogether.platform.application.global.response.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse<Void>> handleUnexpectedException(final Exception exception) {
        log.error("Unexpected exception occurred: [{}]", exception.getMessage(), exception);
        final ExceptionCode undefinedExceptionCode = ExceptionCode._INTERNAL_SERVER_ERROR;
        final ExceptionResponse<Void> body = ExceptionResponse.onFailure(undefinedExceptionCode);
        return new ResponseEntity<>(body, undefinedExceptionCode.getHttpStatus());
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ExceptionResponse<Void>> handleGeneralException(final GlobalException exception) {
        log.error("GlobalException occurred: [{}] - Code: {}", exception.getMessage(), exception.getExceptionCode().getCode(), exception);
        final ExceptionResponse<Void> body = ExceptionResponse.onFailure(exception.getExceptionCode());
        return new ResponseEntity<>(body, exception.getExceptionCode().getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse<Void>> handleValidException(final MethodArgumentNotValidException exception) {
        final ExceptionCode validExceptionCode = ExceptionCode.NOT_VALIDATE_FILED;

        String fieldMessage = validExceptionCode.getMessage();
        if (exception.getDetailMessageArguments() != null && exception.getDetailMessageArguments().length > 1) {
            fieldMessage = Objects.toString(exception.getDetailMessageArguments()[1], fieldMessage);
        }

        final ExceptionCode validExceptionCodeWithFieldMessage = validExceptionCode.withUpdateMessage(fieldMessage);
        log.error("Validation error occurred: [{}] - Field Message: {}", exception.getMessage(), fieldMessage, exception);

        final ExceptionResponse<Void> body = ExceptionResponse.onFailure(validExceptionCodeWithFieldMessage);
        return new ResponseEntity<>(body, validExceptionCodeWithFieldMessage.getHttpStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse<Void>> handleIllegalArgumentException(final IllegalArgumentException exception) {
        final ExceptionCode badRequestCode = ExceptionCode._BAD_REQUEST.withUpdateMessage(exception.getMessage());
        log.error("IllegalArgumentException occurred: [{}] - Updated Message: {}", exception.getMessage(), badRequestCode.getMessage(), exception);

        final ExceptionResponse<Void> body = ExceptionResponse.onFailure(badRequestCode);
        return new ResponseEntity<>(body, badRequestCode.getHttpStatus());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionResponse<Void>> handleNoResourceFoundException(final NoResourceFoundException exception) {
        final ExceptionCode notFoundCode = ExceptionCode._NOT_FOUND.withUpdateMessage(exception.getMessage());
        log.error("NoResourceFoundException occurred: [{}] - Updated Message: {}", exception.getMessage(), notFoundCode.getMessage(), exception);

        final ExceptionResponse<Void> body = ExceptionResponse.onFailure(notFoundCode);
        return new ResponseEntity<>(body, notFoundCode.getHttpStatus());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ExceptionResponse<Void>> handleSecurityException(final SecurityException exception) {
        final ExceptionCode unauthorizedCode = ExceptionCode._UNAUTHORIZED.withUpdateMessage(exception.getMessage());
        log.error("SecurityException occurred: [{}] - Updated Message: {}", exception.getMessage(), unauthorizedCode.getMessage(), exception);

        final ExceptionResponse<Void> body = ExceptionResponse.onFailure(unauthorizedCode);
        return new ResponseEntity<>(body, unauthorizedCode.getHttpStatus());
    }
}

