package com.project.hub.model.dto.response.projects;

import com.project.hub.model.mapper.ProjectDetail;
import lombok.Getter;

@Getter
public class ProjectDetailResponse {

  private final ProjectDetail projectDetail;

  public ProjectDetailResponse(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

}
