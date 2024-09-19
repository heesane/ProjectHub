package com.project.hub.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchType {
  LATEST("id"),
  POPULAR("likeCount"),
  COMMENT("commentsCount");

  private final String sort;
}
