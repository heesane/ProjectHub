package com.project.hub.exception.exception;

import com.project.hub.exception.ExceptionCode;

public class UnmatchedPasswordException extends BusinessException {

  public UnmatchedPasswordException() {
    super(ExceptionCode.UNMATCHED_PASSWORD);
  }
}
