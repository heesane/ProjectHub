package com.project.hub.exception.exception;

import com.project.hub.exception.ExceptionCode;

public class DuplicatedNicknameException extends BusinessException {

  public DuplicatedNicknameException() {
    super(ExceptionCode.DUPLICATE_NICKNAME);
  }

}
