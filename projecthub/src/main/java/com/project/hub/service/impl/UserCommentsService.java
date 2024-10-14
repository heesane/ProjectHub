package com.project.hub.service.impl;

import com.project.hub.aop.badge.BadgeCheck;
import com.project.hub.auth.service.TokenService;
import com.project.hub.entity.Comments;
import com.project.hub.entity.Projects;
import com.project.hub.entity.User;
import com.project.hub.exceptions.ExceptionCode;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.exceptions.exception.TokenNotExistsException;
import com.project.hub.exceptions.exception.UnmatchedUserException;
import com.project.hub.model.documents.ProjectDocuments;
import com.project.hub.model.dto.request.comments.DeleteCommentRequest;
import com.project.hub.model.dto.request.comments.UpdateCommentRequest;
import com.project.hub.model.dto.request.comments.WriteCommentRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.type.ResultCode;
import com.project.hub.repository.document.ProjectDocumentsRepository;
import com.project.hub.repository.jpa.CommentsRepository;
import com.project.hub.service.CommentsService;
import com.project.hub.util.UpdateManager;
import com.project.hub.validator.Validator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommentsService implements CommentsService {

  private final CommentsRepository commentsRepository;
  private final ProjectDocumentsRepository projectDocumentsRepository;
  private final Validator validator;
  private final TokenService tokenService;

  @BadgeCheck
  @Override
  public ResultResponse createComment(WriteCommentRequest request) {

    User commentUser = validator.validateAndGetUser(request.getUserId());

    Projects commentedProject = validator.validateAndGetProject(request.getProjectId());

    ProjectDocuments projectDocuments = projectDocumentsRepository.findById(
        commentedProject.getId()).orElseThrow(
        () -> new NotFoundException(ExceptionCode.PROJECT_DOCUMENTS_NOT_FOUND)
    );

    // 부모 댓글이 있는 경우 부모 댓글이 존재하는지 확인
    Long parentCommentId = request.getParentCommentId();

    Comments newComments;

    // 대댓글인 경우
    if (parentCommentId != null) {
      validator.isCommentExist(parentCommentId);

      Comments parentComment = validator.validateAndGetComment(parentCommentId);

      newComments = Comments.builder()
          .user(commentUser)
          .project(commentedProject)
          .contents(request.getContents())
          .parentComment(parentComment)
          .build();

      commentsRepository.save(newComments);

      UpdateManager.incrementProjectCommentCount(projectDocuments,
          validator.validateAndGetProject(request.getProjectId()));

      projectDocumentsRepository.save(projectDocuments);

      return ResultResponse.of(ResultCode.COMMENT_REPLY_SUCCESS, newComments.getId());
    }
    // 댓글인 경우
    else {
      newComments = Comments.builder()
          .user(commentUser)
          .project(commentedProject)
          .contents(request.getContents())
          .build();

      UpdateManager.incrementProjectCommentCount(projectDocuments, commentedProject);

      commentsRepository.save(newComments);

      projectDocumentsRepository.save(projectDocuments);

      return ResultResponse.of(ResultCode.COMMENT_WRITE_SUCCESS, newComments.getId());
    }
  }

  @Override
  public ResultResponse updateComment(HttpServletRequest request,
      UpdateCommentRequest updateCommentRequest) {

    String userEmail = tokenService.extractEmail(request).orElseThrow(TokenNotExistsException::new);

    User user = validator.validateAndGetUser(updateCommentRequest.getUserId());

    if (!user.getEmail().equals(userEmail)) {
      throw new UnmatchedUserException();
    }

    validator.isProjectExist(updateCommentRequest.getProjectId());

    Comments oldComment = validator.validateAndGetComment(updateCommentRequest.getCommentId());

    oldComment.update(updateCommentRequest);

    return ResultResponse.of(ResultCode.COMMENT_UPDATE_SUCCESS, oldComment.getId());
  }

  @Override
  public ResultResponse deleteComment(HttpServletRequest request,
      DeleteCommentRequest deleteCommentRequest) {

    String userEmail = tokenService.extractEmail(request).orElseThrow(TokenNotExistsException::new);

    validator.isUserExist(deleteCommentRequest.getUserId());

    Comments oldComments = validator.validateAndGetComment(deleteCommentRequest.getCommentId());

    if (!oldComments.getUser().getId().equals(deleteCommentRequest.getUserId())
        || !oldComments.getUser().getEmail().equals(userEmail)) {
      throw new UnmatchedUserException();
    }

    commentsRepository.delete(oldComments);

    return ResultResponse.of(ResultCode.COMMENT_DELETE_SUCCESS, oldComments.getId());
  }
}
