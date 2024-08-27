package com.project.hub.exceptions.exception;

import com.project.hub.exceptions.ExceptionCode;

public class NotFoundException extends BusinessException{

  public NotFoundException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
