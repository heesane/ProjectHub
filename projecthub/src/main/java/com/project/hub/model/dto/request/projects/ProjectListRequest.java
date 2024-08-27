package com.project.hub.model.dto.request.projects;

import com.project.hub.model.type.Sorts;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectListRequest {

  private int page = 0;
  private int size = 5;
  private Sorts sort = Sorts.LATEST;
}
