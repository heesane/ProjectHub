package com.project.hub.exception.exception;

import com.project.hub.exception.ExceptionCode;

public class UnmatchedUserException extends BusinessException {

  public UnmatchedUserException() {
    super(ExceptionCode.UNMATCHED_PROJECT_OWNER);
  }
}
