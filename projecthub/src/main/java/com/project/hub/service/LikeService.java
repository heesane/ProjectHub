package com.project.hub.service;

import com.project.hub.model.dto.request.likes.BaseLikeRequest;
import com.project.hub.model.dto.response.ResultResponse;

public interface LikeService {

  ResultResponse like(BaseLikeRequest projectLikeRequest);
}
