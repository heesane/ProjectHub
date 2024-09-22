package com.project.hub.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.hub.entity.ProjectLikes;
import com.project.hub.entity.Projects;
import com.project.hub.model.dto.request.likes.BaseLikeRequest;
import com.project.hub.model.dto.request.likes.CommentLikeRequest;
import com.project.hub.model.dto.request.likes.ProjectLikeRequest;
import com.project.hub.model.type.LikeType;
import com.project.hub.model.type.ResultCode;
import com.project.hub.repository.jpa.CommentsLikeRepository;
import com.project.hub.repository.jpa.CommentsRepository;
import com.project.hub.repository.jpa.ProjectRepository;
import com.project.hub.repository.jpa.ProjectsLikeRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

@ExtendWith(MockitoExtension.class)
class UserLikeServiceTest {

  @Mock
  private CommentsLikeRepository commentsLikeRepository;

  @Mock
  private ProjectsLikeRepository projectsLikeRepository;

  @Mock
  private CommentsRepository commentsRepository;

  @Mock
  private ProjectRepository projectsRepository;

  @Mock
  private RedisTemplate<String, String> redisTemplate;

  @Mock
  private SetOperations<String, String> setOperations;

  @InjectMocks
  private UserLikeService userLikeService;

  private final static String LIKE_KEY_PREFIX = "like:";
  private final static String COMMENT_LIKE_KEY = "comment:";
  private final static String PROJECT_LIKE_KEY = "project:";

  @BeforeEach
  void setUp() {
    when(redisTemplate.opsForSet()).thenReturn(setOperations);
  }

  @Test
  @DisplayName("프로젝트 좋아요 기능 - like")
  void projectLike() {

    // given
    BaseLikeRequest baseLikeRequest = new ProjectLikeRequest(1L, LikeType.PROJECT,1L);
    String key = LIKE_KEY_PREFIX + PROJECT_LIKE_KEY + ((ProjectLikeRequest) baseLikeRequest).getProjectId();

    // when
    when(setOperations.isMember(key, baseLikeRequest.getUserId().toString())).thenReturn(false);
    when(setOperations.add(key, baseLikeRequest.getUserId().toString())).thenReturn(1L);

    // then
    assertEquals(
        userLikeService.like(baseLikeRequest).getMessage()
        , ResultCode.PROJECT_LIKE_SUCCESS.getMessage()
    );
  }

  @Test
  @DisplayName("프로젝트 좋아요 기능 - dislike")
  void projectDislike() {

    // given
    BaseLikeRequest baseLikeRequest = new ProjectLikeRequest(1L, LikeType.PROJECT,1L);
    String key = LIKE_KEY_PREFIX + PROJECT_LIKE_KEY + ((ProjectLikeRequest) baseLikeRequest).getProjectId();

    // when
    when(setOperations.isMember(key, baseLikeRequest.getUserId().toString())).thenReturn(true);
    when(setOperations.remove(key, baseLikeRequest.getUserId().toString())).thenReturn(1L);

    // then
    assertEquals(
        userLikeService.like(baseLikeRequest).getMessage()
        , ResultCode.PROJECT_DISLIKE_SUCCESS.getMessage()
    );
  }

  @Test
  @DisplayName("댓글 좋아요 기능 - like")
  void commentLike() {

    // given
    BaseLikeRequest baseLikeRequest = new CommentLikeRequest(1L, LikeType.COMMENT,1L);
    String key = LIKE_KEY_PREFIX + COMMENT_LIKE_KEY + ((CommentLikeRequest) baseLikeRequest).getCommentId();

    // when
    when(setOperations.isMember(key, baseLikeRequest.getUserId().toString())).thenReturn(false);
    when(setOperations.add(key, baseLikeRequest.getUserId().toString())).thenReturn(1L);

    // then
    assertEquals(
        userLikeService.like(baseLikeRequest).getMessage()
        , ResultCode.COMMENT_LIKE_SUCCESS.getMessage()
    );
  }

  @Test
  @DisplayName("댓글 좋아요 기능 - dislike")
  void commentDislike() {

    // given
    BaseLikeRequest baseLikeRequest = new CommentLikeRequest(1L, LikeType.COMMENT,1L);
    String key = LIKE_KEY_PREFIX + COMMENT_LIKE_KEY + ((CommentLikeRequest) baseLikeRequest).getCommentId();

    // when
    when(setOperations.isMember(key, baseLikeRequest.getUserId().toString())).thenReturn(true);
    when(setOperations.remove(key, baseLikeRequest.getUserId().toString())).thenReturn(1L);

    // then
    assertEquals(
        userLikeService.like(baseLikeRequest).getMessage()
        , ResultCode.COMMENT_DISLIKE_SUCCESS.getMessage()
    );
  }

  @Test
  void saveLikeCount() {
    // given
    LikeType likeType = LikeType.PROJECT;
    List<Long> projectIds = Arrays.asList(1L, 2L, 3L);
    when(projectsRepository.findAllIdWithDetail()).thenReturn(projectIds);

    // Mock Redis 키와 값
    for (Long projectId : projectIds) {
      String key = LIKE_KEY_PREFIX + PROJECT_LIKE_KEY + projectId;
      when(setOperations.members(key)).thenReturn(Collections.singleton("1")); // 사용자 ID 1이 좋아요
    }

    // Mock Project 엔티티와 저장
    for (Long projectId : projectIds) {
      Projects project = Projects.builder()
          .id(projectId)
          .title("Project " + projectId)
          .build();
      when(projectsRepository.findById(projectId)).thenReturn(Optional.of(project));
    }

    // when
    userLikeService.updateLikes(likeType);

    // then
    for (Long projectId : projectIds) {
      verify(projectsRepository, times(1)).findById(projectId);
      verify(projectsLikeRepository, times(3)).saveAndFlush(any(ProjectLikes.class));
    }
  }
}