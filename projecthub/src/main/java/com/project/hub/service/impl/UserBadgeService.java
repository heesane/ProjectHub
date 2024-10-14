package com.project.hub.service.impl;

import com.project.hub.entity.Badge;
import com.project.hub.exceptions.ExceptionCode;
import com.project.hub.exceptions.exception.DuplicateBadgeException;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.model.dto.request.badge.CreateBadgeRequest;
import com.project.hub.model.dto.request.badge.DeleteBadgeRequest;
import com.project.hub.model.dto.request.badge.UpdateBadgeRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.type.ResultCode;
import com.project.hub.repository.jpa.BadgeRepository;
import com.project.hub.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBadgeService implements BadgeService {

  private final BadgeRepository badgeRepository;

  @Override
  public ResultResponse getAllBadges() {
    return ResultResponse.of(ResultCode.BADGE_LIST_SUCCESS, badgeRepository.findAll());
  }

  @Override
  @Transactional
  public ResultResponse createNewBadge(CreateBadgeRequest request) {

    if (badgeRepository.existsByName(request.getName())) {
      throw new DuplicateBadgeException(ExceptionCode.BADGE_ALREADY_EXISTS);
    }

    Badge badge = Badge.builder()
        .name(request.getName())
        .description(request.getDescription())
        .requiredProjectCount(request.getRequiredProjects())
        .requiredCommentCount(request.getRequiredComments())
        .build();

    badgeRepository.save(badge);

    return ResultResponse.of(ResultCode.BADGE_CREATE_SUCCESS);
  }

  @Override
  @Transactional
  public ResultResponse updateBadge(UpdateBadgeRequest request) {
    Long oldBadgeId = request.getId();

    Badge badge = badgeRepository.findById(oldBadgeId).orElseThrow(
        () -> new NotFoundException(ExceptionCode.BADGE_NOT_FOUND)
    );

    badge.update(request);

    return ResultResponse.of(ResultCode.BADGE_UPDATE_SUCCESS);
  }

  @Override
  @Transactional
  public ResultResponse deleteBadge(DeleteBadgeRequest request) {

    Long badgeId = request.getId();

    if (!badgeRepository.existsById(badgeId)) {
      throw new NotFoundException(ExceptionCode.BADGE_NOT_FOUND);
    }

    badgeRepository.deleteById(badgeId);

    return ResultResponse.of(ResultCode.BADGE_DELETE_SUCCESS);
  }
}
