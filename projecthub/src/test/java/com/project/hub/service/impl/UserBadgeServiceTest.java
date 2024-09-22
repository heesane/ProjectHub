package com.project.hub.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.project.hub.entity.Badge;
import com.project.hub.model.dto.request.badge.CreateBadgeRequest;
import com.project.hub.model.dto.request.badge.DeleteBadgeRequest;
import com.project.hub.model.dto.request.badge.UpdateBadgeRequest;
import com.project.hub.model.type.ResultCode;
import com.project.hub.repository.jpa.BadgeRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserBadgeServiceTest {

  @Mock
  private BadgeRepository badgeRepository;

  @InjectMocks
  private UserBadgeService userBadgeService;

  @BeforeEach
  void setUp() {
  }

  @Test
  @DisplayName("새로운 뱃지 생성")
  void createNewBadge() {
    // given
    CreateBadgeRequest request = new CreateBadgeRequest("name", "description", 1L, 1L);
    // when
    when(badgeRepository.existsByName(request.getName())).thenReturn(false);
    // then
    assertEquals(
        ResultCode.BADGE_CREATE_SUCCESS.getMessage(),
        userBadgeService.createNewBadge(request).getMessage()
    );
  }

  @Test
  @DisplayName("뱃지 수정")
  void updateBadge() {
    // given
    Badge oldBadge = Badge.builder()
        .name("oldName")
        .description("oldDescription")
        .requiredProjectCount(3L)
        .requiredCommentCount(5L)
        .build();
    UpdateBadgeRequest request = new UpdateBadgeRequest("name", "description", 1L, 1L,1L);

    // when
    when(badgeRepository.findById(request.getId())).thenReturn(Optional.ofNullable(oldBadge));

    // then
    assertEquals(
        ResultCode.BADGE_UPDATE_SUCCESS.getMessage(),
        userBadgeService.updateBadge(request).getMessage()
    );
  }

  @Test
  @DisplayName("뱃지 삭제")
  void deleteBadge() {
    // given
    DeleteBadgeRequest request = new DeleteBadgeRequest(1L);
    Badge badge = Badge.builder()
        .name("name")
        .description("description")
        .requiredProjectCount(1L)
        .requiredCommentCount(1L)
        .build();

    // when
    when(badgeRepository.existsById(request.getId())).thenReturn(true);

    // then
    assertEquals(
        ResultCode.BADGE_DELETE_SUCCESS.getMessage(),
        userBadgeService.deleteBadge(request).getMessage()
    );
  }
}