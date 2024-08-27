package com.project.hub.exceptions.exception;

import com.project.hub.exceptions.ExceptionCode;

public class DuplicatedNicknameException extends BusinessException {

  public DuplicatedNicknameException() {
    super(ExceptionCode.DUPLICATE_NICKNAME);
  }

}
