package com.project.hub.model.dto.request.projects;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProjectRequest {

  @Min(1)
  private final Long id;

}
