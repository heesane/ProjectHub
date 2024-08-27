package com.project.hub.exceptions.exception;

import com.project.hub.exceptions.ExceptionCode;

public class DuplicatedEmailException extends BusinessException {

  public DuplicatedEmailException() {
    super(ExceptionCode.DUPLICATE_EMAIL);
  }
}
