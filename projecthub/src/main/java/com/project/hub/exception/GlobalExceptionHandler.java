package com.project.hub.exception;

import com.project.hub.exception.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ExceptionResponse> handleException(Exception e) {
    log.error(e.getMessage(), e);
    ExceptionResponse response = ExceptionResponse.of(ExceptionCode.INTERNAL_SERVER_ERROR);
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(BusinessException.class)
  protected ResponseEntity<ExceptionResponse> handleRuntimeException(BusinessException e) {
    final ExceptionCode errorCode = e.getExceptionCode();
    final ExceptionResponse response =
        ExceptionResponse.builder()
            .errorMessage(errorCode.getMessage())
            .businessCode(errorCode.getCode())
            .build();
    log.warn(e.getMessage());
    return ResponseEntity.status(errorCode.getStatus()).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    final ExceptionResponse response = ExceptionResponse.of(ExceptionCode.INPUT_INVALID_VALUE,
        e.getBindingResult());
    log.warn(e.getMessage());
    return ResponseEntity.status(ExceptionCode.INPUT_INVALID_VALUE.getStatus()).body(response);
  }
}
