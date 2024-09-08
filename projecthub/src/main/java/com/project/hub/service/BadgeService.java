package com.project.hub.service;

import com.project.hub.model.dto.request.badge.CreateBadgeRequest;
import com.project.hub.model.dto.request.badge.DeleteBadgeRequest;
import com.project.hub.model.dto.request.badge.UpdateBadgeRequest;
import com.project.hub.model.dto.response.ResultResponse;

public interface BadgeService {

  ResultResponse getAllBadges();

  ResultResponse createNewBadge(CreateBadgeRequest request);

  ResultResponse updateBadge(UpdateBadgeRequest request);

  ResultResponse deleteBadge(DeleteBadgeRequest request);
}
