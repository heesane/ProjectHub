package com.project.hub.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PictureType {
  SYSTEM_ARCHITECTURE("system_architecture"),
  ERD("erd"),
  ;

  private final String folderPrefix;
}
