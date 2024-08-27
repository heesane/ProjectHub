package com.project.hub.exception.exception;

import com.project.hub.exception.ExceptionCode;

public class ProjectNotFoundException extends BusinessException {

  public ProjectNotFoundException() {
    super(ExceptionCode.PROJECT_NOT_FOUND);
  }
}
