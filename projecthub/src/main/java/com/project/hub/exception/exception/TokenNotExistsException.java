package com.project.hub.exception.exception;

import com.project.hub.exception.ExceptionCode;

public class TokenNotExistsException extends BusinessException {

  public TokenNotExistsException() {
    super(ExceptionCode.TOKEN_ACCESS_NOT_EXISTS);
  }

}
