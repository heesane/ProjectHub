package com.project.hub.model.dto.request.badge;

import lombok.Getter;

@Getter
public class UpdateBadgeRequest extends CreateBadgeRequest {

  private final Long id;

  public UpdateBadgeRequest(String name, String description, Long requiredProjects,
      Long requiredComments, Long id) {
    super(name, description, requiredProjects, requiredComments);
    this.id = id;
  }
}
