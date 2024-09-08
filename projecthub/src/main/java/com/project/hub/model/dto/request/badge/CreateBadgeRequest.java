package com.project.hub.model.dto.request.badge;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateBadgeRequest {
  private final String name;
  private final String description;
  private final Long requiredProjects;
  private final Long requiredComments;
}
