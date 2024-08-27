package com.project.hub.exceptions.exception;

import com.project.hub.exceptions.ExceptionCode;

public class TokenNotExistsException extends BusinessException {

  public TokenNotExistsException() {
    super(ExceptionCode.TOKEN_ACCESS_NOT_EXISTS);
  }

}
