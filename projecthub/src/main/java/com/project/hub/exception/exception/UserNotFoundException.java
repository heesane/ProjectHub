package com.project.hub.exception.exception;

import com.project.hub.exception.ExceptionCode;

public class UserNotFoundException extends BusinessException {

  public UserNotFoundException() {
    super(ExceptionCode.USER_NOT_FOUND);
  }
}
