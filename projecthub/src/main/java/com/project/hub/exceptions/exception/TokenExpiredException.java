package com.project.hub.exceptions.exception;

import com.project.hub.exceptions.ExceptionCode;

public class TokenExpiredException extends BusinessException {

  public TokenExpiredException() {
    super(ExceptionCode.TOKEN_EXPIRED);
  }

}