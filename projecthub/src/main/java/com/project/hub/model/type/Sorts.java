package com.project.hub.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Sorts {
  LATEST("최신순"),
  LIKE("좋아요순"),
  COMMENTS("댓글순"),
  ;

  private final String sort;
}
