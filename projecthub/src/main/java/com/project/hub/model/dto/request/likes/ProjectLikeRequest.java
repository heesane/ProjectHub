package com.project.hub.model.dto.request.likes;

import com.project.hub.model.type.LikeType;
import lombok.Getter;

@Getter
public class ProjectLikeRequest extends BaseLikeRequest {

  private final Long projectId;

  public ProjectLikeRequest(Long userId, LikeType likeType, Long projectId) {
    super(userId, likeType);
    this.projectId = projectId;
  }
}
