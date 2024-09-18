package com.project.hub.validator;

import com.project.hub.entity.CommentLikes;
import com.project.hub.entity.Comments;
import com.project.hub.entity.ProjectLikes;
import com.project.hub.entity.Projects;
import com.project.hub.entity.User;
import com.project.hub.exceptions.ExceptionCode;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.repository.jpa.BadgeRepository;
import com.project.hub.repository.jpa.CommentsLikeRepository;
import com.project.hub.repository.jpa.CommentsRepository;
import com.project.hub.repository.jpa.ProjectRepository;
import com.project.hub.repository.jpa.ProjectsLikeRepository;
import com.project.hub.repository.jpa.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Validator {

  private final CommentsRepository commentsRepository;

  private final ProjectRepository projectRepository;

  private final UserRepository userRepository;

  private final ProjectsLikeRepository projectsLikeRepository;

  private final CommentsLikeRepository commentsLikeRepository;

  private final BadgeRepository badgeRepository;

  public Comments validateAndGetComment(Long commentId) {
    return commentsRepository.findById(commentId).orElseThrow(
        () -> new NotFoundException(ExceptionCode.COMMENTS_NOT_FOUND)
    );
  }

  public Long validateAndGetUserCommentsCount(Long userId) {
    return commentsRepository.countByUserId(userId);
  }

  public void isCommentExist(Long commentId) {
    if (!commentsRepository.existsById(commentId)) {
      throw new NotFoundException(ExceptionCode.COMMENTS_NOT_FOUND);
    }
  }

  public Projects validateAndGetProject(Long projectId) {
    // 프로젝트 정보 검증
    return projectRepository.findByIdWithDetail(projectId).orElseThrow(
        () -> new NotFoundException(ExceptionCode.PROJECT_NOT_FOUND));
  }

  public void isProjectExist(Long projectId) {
    if (!projectRepository.existsById(projectId)) {
      throw new NotFoundException(ExceptionCode.PROJECT_NOT_FOUND);
    }
  }

  public User validateAndGetUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(
            () -> new NotFoundException(ExceptionCode.USER_NOT_FOUND)
        );
  }

  public void isUserExist(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new NotFoundException(ExceptionCode.USER_NOT_FOUND);
    }
  }

  public List<ProjectLikes> validateAndGetProjectLike(Long userId) {
    if(!userRepository.existsById(userId)) {
      throw new NotFoundException(ExceptionCode.USER_NOT_FOUND);
    }
    return projectsLikeRepository.findAllByUserId(userId);
  }

  public List<CommentLikes> validateAndGetCommentLike(Long userId) {
    if(!userRepository.existsById(userId)) {
      throw new NotFoundException(ExceptionCode.USER_NOT_FOUND);
    }
    return commentsLikeRepository.findAllByUserId(userId);
  }
}
