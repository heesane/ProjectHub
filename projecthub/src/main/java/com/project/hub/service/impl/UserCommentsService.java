package com.project.hub.service.impl;

import com.project.hub.entity.Comments;
import com.project.hub.entity.Projects;
import com.project.hub.entity.User;
import com.project.hub.model.dto.request.comments.DeleteCommentRequest;
import com.project.hub.model.dto.request.comments.UpdateCommentRequest;
import com.project.hub.model.dto.request.comments.WriteCommentRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.type.ResultCode;
import com.project.hub.repository.CommentsRepository;
import com.project.hub.service.CommentsService;
import com.project.hub.validator.CommentValidator;
import com.project.hub.validator.ProjectValidator;
import com.project.hub.validator.UserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommentsService implements CommentsService {

  private final CommentsRepository commentsRepository;
  private final UserValidator userValidator;
  private final ProjectValidator projectValidator;
  private final CommentValidator commentValidator;

  @Override
  public ResultResponse createComment(WriteCommentRequest request) {

    User commentUser = userValidator.validateAndGetUser(request.getUserId());

    Projects commentedProject = projectValidator.validateAndGetProject(request.getProjectId());

    // 부모 댓글이 있는 경우 부모 댓글이 존재하는지 확인
    Long parentCommentId = request.getParentCommentId();

    Comments newComments;

    // 대댓글인 경우
    if (parentCommentId != null) {
      commentValidator.isCommentExist(parentCommentId);

      Comments parentComment = commentValidator.validateAndGetComment(parentCommentId);

      newComments = Comments.builder()
          .user(commentUser)
          .project(commentedProject)
          .contents(request.getContents())
          .parentComment(parentComment)
          .build();

      commentsRepository.save(newComments);

      parentComment.reply(newComments);

      return ResultResponse.of(ResultCode.COMMENT_REPLY_SUCCESS, newComments.getId());
    }
    // 댓글인 경우
    else {
      newComments = Comments.builder()
          .user(commentUser)
          .project(commentedProject)
          .contents(request.getContents())
          .build();

      commentsRepository.save(newComments);
      return ResultResponse.of(ResultCode.COMMENT_WRITE_SUCCESS, newComments.getId());
    }
  }

  @Override
  public ResultResponse updateComment(UpdateCommentRequest request) {

    userValidator.isUserExist(request.getUserId());

    projectValidator.isProjectExist(request.getProjectId());

    Comments oldComment = commentValidator.validateAndGetComment(request.getCommentId());

    oldComment.update(request);

    return ResultResponse.of(ResultCode.COMMENT_UPDATE_SUCCESS, oldComment.getId());
  }

  @Override
  public ResultResponse deleteComment(DeleteCommentRequest request) {

    userValidator.isUserExist(request.getUserId());

    Comments oldComments = commentValidator.validateAndGetComment(request.getCommentId());

    if(!oldComments.getUser().getId().equals(request.getUserId())){
      throw new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다.");
    }

    commentsRepository.delete(oldComments);

    return ResultResponse.of(ResultCode.COMMENT_DELETE_SUCCESS, oldComments.getId());
  }
}
