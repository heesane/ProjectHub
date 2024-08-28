package com.project.hub.model.dto.request.likes;

import com.project.hub.model.type.LikeType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BaseLikeRequest {

  private final Long userId;

  private final LikeType likeType;
}
