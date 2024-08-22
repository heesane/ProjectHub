package com.project.hub.exception.exception;

import com.project.hub.exception.ExceptionCode;

public class TokenExpiredException extends BusinessException {

  public TokenExpiredException() {
    super(ExceptionCode.TOKEN_EXPIRED);
  }

}