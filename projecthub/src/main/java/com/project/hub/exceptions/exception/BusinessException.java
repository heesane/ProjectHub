package com.project.hub.exceptions.exception;

import com.project.hub.exceptions.ExceptionCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final ExceptionCode exceptionCode;

  public BusinessException(ExceptionCode exceptionCode) {
    super(exceptionCode.getMessage());
    this.exceptionCode = exceptionCode;
  }
}
