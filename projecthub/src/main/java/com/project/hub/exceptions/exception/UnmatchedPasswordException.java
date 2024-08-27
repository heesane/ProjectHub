package com.project.hub.exceptions.exception;

import com.project.hub.exceptions.ExceptionCode;

public class UnmatchedPasswordException extends BusinessException {

  public UnmatchedPasswordException() {
    super(ExceptionCode.UNMATCHED_PASSWORD);
  }
}
