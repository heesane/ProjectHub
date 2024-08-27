package com.project.hub.model.dto.request.projects;

import com.project.hub.model.type.Sorts;
import lombok.Getter;

@Getter
public class MyProjectListRequest extends ProjectListRequest {

  private final Long userId;

  public MyProjectListRequest(int page, int size, Sorts sort, Long userId) {
    super(page, size, sort);
    this.userId = userId;
  }
}
