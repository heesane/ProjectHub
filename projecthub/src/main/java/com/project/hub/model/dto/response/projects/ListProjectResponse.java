package com.project.hub.model.dto.response.projects;

import com.project.hub.model.mapper.ShortProjectDetail;
import java.util.List;
import lombok.Getter;

@Getter
public class ListProjectResponse {

  private final List<ShortProjectDetail> projectDetails;

  public ListProjectResponse(List<ShortProjectDetail> projectDetails) {
    this.projectDetails = projectDetails;
  }
}
