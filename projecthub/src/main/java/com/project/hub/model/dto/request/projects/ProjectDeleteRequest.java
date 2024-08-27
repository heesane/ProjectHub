package com.project.hub.model.dto.request.projects;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectDeleteRequest {

  @Min(1)
  private final Long userId;

  @Min(1)
  private final Long projectId;

}
