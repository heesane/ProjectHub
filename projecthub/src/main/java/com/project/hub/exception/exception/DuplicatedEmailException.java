package com.project.hub.exception.exception;

import com.project.hub.exception.ExceptionCode;

public class DuplicatedEmailException extends BusinessException {

  public DuplicatedEmailException() {
    super(ExceptionCode.DUPLICATE_EMAIL);
  }
}
