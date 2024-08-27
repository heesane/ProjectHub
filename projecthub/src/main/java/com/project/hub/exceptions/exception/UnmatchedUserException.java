package com.project.hub.exceptions.exception;

import com.project.hub.exceptions.ExceptionCode;

public class UnmatchedUserException extends BusinessException {

  public UnmatchedUserException() {
    super(ExceptionCode.UNMATCHED_PROJECT_OWNER);
  }
}
