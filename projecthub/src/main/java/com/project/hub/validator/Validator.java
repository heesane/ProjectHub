package com.project.hub.validator;

import com.project.hub.entity.Comments;
import com.project.hub.entity.Projects;
import com.project.hub.entity.User;
import com.project.hub.exceptions.ExceptionCode;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.repository.jpa.CommentsRepository;
import com.project.hub.repository.jpa.ProjectRepository;
import com.project.hub.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Validator {

  private final CommentsRepository commentsRepository;

  private final ProjectRepository projectRepository;

  private final UserRepository userRepository;

  public Comments validateAndGetComment(Long commentId) {
    return commentsRepository.findById(commentId).orElseThrow(
        () -> new NotFoundException(ExceptionCode.COMMENTS_NOT_FOUND)
    );
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
}
