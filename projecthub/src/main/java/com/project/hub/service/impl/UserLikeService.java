package com.project.hub.service.impl;

import com.project.hub.entity.CommentLikes;
import com.project.hub.entity.Comments;
import com.project.hub.entity.ProjectLikes;
import com.project.hub.entity.Projects;
import com.project.hub.exceptions.ExceptionCode;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.model.dto.request.likes.BaseLikeRequest;
import com.project.hub.model.dto.request.likes.CommentLikeRequest;
import com.project.hub.model.dto.request.likes.ProjectLikeRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.type.LikeType;
import com.project.hub.model.type.ResultCode;
import com.project.hub.repository.CommentsLikeRepository;
import com.project.hub.repository.CommentsRepository;
import com.project.hub.repository.ProjectRepository;
import com.project.hub.repository.ProjectsLikeRepository;
import com.project.hub.service.LikeService;
import com.project.hub.util.UpdateManager;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.KeyScanOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLikeService implements LikeService {

  private final CommentsLikeRepository commentsLikeRepository;
  private final ProjectsLikeRepository projectsLikeRepository;
  private final CommentsRepository commentsRepository;
  private final ProjectRepository projectsRepository;
  private final RedisTemplate<String, String> redisTemplate;

  private final static String LIKE_KEY_PREFIX = "like:";
  private final static String COMMENT_LIKE_KEY = "comment:";
  private final static String PROJECT_LIKE_KEY = "project:";

  @Override
  public ResultResponse like(BaseLikeRequest baseLikeRequest) {
    String key;
    LikeType likeType = baseLikeRequest.getLikeType();
    String userId = baseLikeRequest.getUserId().toString();

    if (likeType.equals(LikeType.PROJECT)) {
      key = LIKE_KEY_PREFIX + PROJECT_LIKE_KEY
          + ((ProjectLikeRequest) baseLikeRequest).getProjectId();
    } else {
      key = LIKE_KEY_PREFIX + COMMENT_LIKE_KEY
          + ((CommentLikeRequest) baseLikeRequest).getCommentId();
    }

    return toggleLike(key, userId);
  }

  private ResultResponse toggleLike(String key, String userId) {

    boolean isLiked = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId));

    Long userIdLong = Long.parseLong(userId);

    if (isLiked) {

      redisTemplate.opsForSet().remove(key, userId);

      //////임시
      long targetId = Long.parseLong(key.split(":")[2]);

      if (isProjectLike(key)) {
        projectsLikeRepository.deleteByProjectIdAndUserId(targetId, userIdLong);
      } else {
        commentsLikeRepository.deleteByCommentIdAndUserId(targetId, userIdLong);
      }

      return ResultResponse.of(isProjectLike(key) ? ResultCode.PROJECT_DISLIKE_SUCCESS
          : ResultCode.COMMENT_DISLIKE_SUCCESS);

    } else {
      redisTemplate.opsForSet().add(key, userId);
      return ResultResponse.of(
          isProjectLike(key) ? ResultCode.PROJECT_LIKE_SUCCESS : ResultCode.COMMENT_LIKE_SUCCESS);
    }
  }


  @Scheduled(cron = "0 0 * * * *")
  public void saveLikeCount() {
    updateLikes(LikeType.PROJECT);
    updateLikes(LikeType.COMMENT);
  }

  public void updateLikes(LikeType likeType) {

    String pattern = likeType == LikeType.PROJECT ? LIKE_KEY_PREFIX + PROJECT_LIKE_KEY + "*"
        : LIKE_KEY_PREFIX + COMMENT_LIKE_KEY + "*";

    ScanOptions options = KeyScanOptions.scanOptions(DataType.SET).match(pattern).count(10).build();

    try (Cursor<String> cursor = redisTemplate.opsForSet().scan(pattern, options)) {
      cursor.forEachRemaining(this::updateLikeCount);
    } catch (Exception e) {
      log.error("Error during scanning Redis keys", e);
    }
  }

  @Transactional
  public void updateLikeCount(String fullKey) {

    // fullKey = like:project:1 or like:comment:1

    // Key에 따른 LikeType 설정
    // like:project:1 -> LikeType.PROJECT, like:comment:1 -> LikeType.COMMENT
    LikeType likeType = fullKey.contains(PROJECT_LIKE_KEY) ? LikeType.PROJECT : LikeType.COMMENT;

    // 전체 Key에서 ProjectId 추출
    // like:project:1 -> 1, like:comment:1 -> 1
    Long likeTargetId = Long.parseLong(fullKey.split(":")[2]);

    // Redis에서 해당 키 값의 member 수 조회
    // like:project:1 -> ProjectId 1의 좋아요 수
    Long likeCount = getLikeCount(fullKey);

    // 프로젝트 엔티티 조회 후 좋아요 수 업데이트
    if (likeType == LikeType.PROJECT) {
      Projects project = projectsRepository.findById(likeTargetId).orElseThrow(
          () -> new NotFoundException(ExceptionCode.PROJECT_NOT_FOUND)
      );

      UpdateManager.updateProjectLikeCount(project, likeCount);

      getLikeUser(fullKey).forEach(
          userId -> updateProjectLikeTable(likeTargetId, Long.parseLong(userId)));
      // 업데이트 된 Project Entity 저장
      projectsRepository.save(project);
    } else {
      Comments comments = commentsRepository.findById(likeTargetId).orElseThrow(
          () -> new NotFoundException(ExceptionCode.COMMENTS_NOT_FOUND)
      );
      UpdateManager.updateCommentLikeCount(comments,likeCount);

      getLikeUser(fullKey).forEach(
          userId -> updateCommentLikeTable(likeTargetId, Long.parseLong(userId)));
      // 업데이트 된 Comment Entity 저장
      commentsRepository.save(comments);
    }
  }

  @Transactional
  public void updateProjectLikeTable(Long projectId, Long userId) {
    // redis에 이미 좋아요가 기록되어 있고, DB에도 이미 좋아요가 기록되어 있을 경우
    if (projectsLikeRepository.existsByProjectIdAndUserId(projectId, userId)) {
      log.debug("User {} already liked project {}", userId, projectId);

    }
    // redis에 좋아요가 기록되어 있지만, DB에는 좋아요가 기록되어 있지 않을 경우
    else {
      ProjectLikes newProjectLike = ProjectLikes.builder()
          .projectId(projectId)
          .userId(userId)
          .build();
      projectsLikeRepository.save(newProjectLike);
    }

  }

  @Transactional
  public void updateCommentLikeTable(Long commentId, Long userId) {
    if (commentsLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
      log.debug("User {} already liked comment {}", userId, commentId);

    } else {
      CommentLikes newCommentLike = CommentLikes.builder()
          .commentId(commentId)
          .userId(userId)
          .build();
      commentsLikeRepository.save(newCommentLike);
    }
  }

  private Long getLikeCount(String key) {
    return redisTemplate.opsForSet().size(key);
  }

  private Set<String> getLikeUser(String key) {
    return redisTemplate.opsForSet().members(key);
  }

  private boolean isProjectLike(String key) {
    return key.contains(PROJECT_LIKE_KEY);
  }
}
