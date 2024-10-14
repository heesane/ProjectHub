package com.project.hub.exceptions.exception;

import com.project.hub.exceptions.ExceptionCode;

public class DuplicateBadgeException extends BusinessException {

  public DuplicateBadgeException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
