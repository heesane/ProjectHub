package com.project.hub.service.impl;

import static com.project.hub.model.type.UserRole.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.hub.auth.service.TokenService;
import com.project.hub.entity.Comments;
import com.project.hub.entity.Projects;
import com.project.hub.entity.User;
import com.project.hub.exceptions.ExceptionCode;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.model.documents.ProjectDocuments;
import com.project.hub.model.dto.request.comments.DeleteCommentRequest;
import com.project.hub.model.dto.request.comments.UpdateCommentRequest;
import com.project.hub.model.dto.request.comments.WriteCommentRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.type.ResultCode;
import com.project.hub.model.type.Skills;
import com.project.hub.model.type.Tools;
import com.project.hub.repository.document.ProjectDocumentsRepository;
import com.project.hub.repository.jpa.CommentsRepository;
import com.project.hub.validator.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class UserCommentsServiceTest {

  @Mock
  private CommentsRepository commentsRepository;

  @Mock
  private ProjectDocumentsRepository projectDocumentsRepository;

  @Mock
  private Validator validator;

  @Mock
  private TokenService tokenService;

  @InjectMocks
  private UserCommentsService userCommentsService;

  @Test
  @DisplayName("댓글 작성")
  void createComment() {
    // given
    WriteCommentRequest request = new WriteCommentRequest(1L, 2L, "ㅎㅇㅎㅇ", null);

    User user = User.builder()
        .email("login@gmail.com")
        .nickname("login")
        .password("login")
        .role(USER)
        .build();

    Projects projects = Projects.builder()
        .id(1L)
        .title("title")
        .subject("subject")
        .skills(List.of(Skills.JAVA, Skills.JAVASCRIPT))
        .tools(List.of(Tools.NOTION))
        .user(user)
        .build();

    ProjectDocuments projectDocuments = new ProjectDocuments().of(projects);

    // when
    when(validator.validateAndGetUser(request.getUserId())).thenReturn(user);
    when(validator.validateAndGetProject(request.getProjectId())).thenReturn(projects);
    when(projectDocumentsRepository.findById(projects.getId())).thenReturn(
        Optional.of(projectDocuments));

    // then
    assertEquals(
        userCommentsService.createComment(request).getMessage(),
        ResultCode.COMMENT_WRITE_SUCCESS.getMessage());
  }

  @Test
  @DisplayName("대댓글 작성")
  void createReply() {
    // given
    WriteCommentRequest request = new WriteCommentRequest(1L, 2L, "ㅎㅇㅎㅇ", 1L);

    User user = User.builder()
        .email("login@gmail.com")
        .nickname("login")
        .password("login")
        .role(USER)
        .build();

    Projects projects = Projects.builder()
        .id(1L)
        .title("title")
        .subject("subject")
        .skills(List.of(Skills.JAVA, Skills.JAVASCRIPT))
        .tools(List.of(Tools.NOTION))
        .user(user)
        .build();

    ProjectDocuments projectDocuments = new ProjectDocuments().of(projects);

    Comments parentComment = Comments.builder()
        .id(1L)
        .contents("ㅎㅇㅎㅇ")
        .user(user)
        .project(projects)
        .parentComment(null)
        .likes(0L)
        .replies(new ArrayList<>())
        .build();

    Comments parentComment2 = Comments.builder()
        .id(1L)
        .contents("ㅎㅇㅎㅇ")
        .user(user)
        .project(projects)
        .parentComment(null)
        .likes(0L)
        .replies(
            new ArrayList<>(
                List.of(
                    Comments.builder()
                        .id(2L)
                        .contents("ㅎㅇㅎㅇ")
                        .user(user)
                        .project(projects)
                        .parentComment(parentComment)
                        .likes(0L)
                        .replies(new ArrayList<>())
                        .build()
                )
            )
        )
        .build();

    // when
    when(validator.validateAndGetUser(request.getUserId())).thenReturn(user);

    when(validator.validateAndGetProject(request.getProjectId())).thenReturn(projects);

    when(projectDocumentsRepository.findById(projects.getId())).thenReturn(
        Optional.of(projectDocuments));

    when(validator.validateAndGetComment(request.getParentCommentId())).thenReturn(parentComment);

    when(commentsRepository.findById(request.getParentCommentId())).thenReturn(
        Optional.ofNullable(parentComment2));

    // ACT
    ResultResponse result = userCommentsService.createComment(request);

    // then
    assertEquals(
        ResultCode.COMMENT_REPLY_SUCCESS.getMessage(),
        result.getMessage()
    );
    assertEquals(
        1,
        commentsRepository.findById(request.getParentCommentId()).get().getReplies().size()
    );
  }

  @Test
  @DisplayName("댓글 수정")
  void updateComment() {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    UpdateCommentRequest commentRequest = new UpdateCommentRequest(1L, 2L, "하위", 1L);

    User user = User.builder()
        .email("login@gmail.com")
        .nickname("login")
        .password("login")
        .role(USER)
        .build();

    Projects projects = Projects.builder()
        .id(1L)
        .title("title")
        .subject("subject")
        .skills(List.of(Skills.JAVA, Skills.JAVASCRIPT))
        .tools(List.of(Tools.NOTION))
        .user(user)
        .build();

    Comments comments = Comments.builder()
        .id(1L)
        .contents("ㅎㅇㅎㅇ")
        .user(user)
        .project(projects)
        .parentComment(null)
        .likes(0L)
        .replies(List.of())
        .build();
    // when
    when(tokenService.extractEmail(request)).thenReturn(Optional.of(user.getEmail()));
    when(validator.validateAndGetUser(commentRequest.getUserId())).thenReturn(user);
    when(validator.validateAndGetComment(commentRequest.getCommentId())).thenReturn(comments);

    // then
    assertEquals(
        userCommentsService.updateComment(request, commentRequest).getMessage(),
        ResultCode.COMMENT_UPDATE_SUCCESS.getMessage());
  }

  @Test
  @DisplayName("댓글 삭제")
  void deleteComment() {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    DeleteCommentRequest deleteCommentRequest = new DeleteCommentRequest(1L, 2L);

    User user = User.builder()
        .id(2L)
        .email("login@gmail.com")
        .nickname("login")
        .password("login")
        .role(USER)
        .build();

    Projects projects = Projects.builder()
        .id(1L)
        .title("title")
        .subject("subject")
        .skills(List.of(Skills.JAVA, Skills.JAVASCRIPT))
        .tools(List.of(Tools.NOTION))
        .user(user)
        .build();

    Comments comments = Comments.builder()
        .id(1L)
        .contents("ㅎㅇㅎㅇ")
        .user(user)
        .project(projects)
        .parentComment(null)
        .likes(0L)
        .replies(List.of())
        .build();

    // when
    when(tokenService.extractEmail(request)).thenReturn(Optional.of(user.getEmail()));
    when(validator.validateAndGetComment(deleteCommentRequest.getCommentId())).thenReturn(comments);

    // then
    assertEquals(
        userCommentsService.deleteComment(request, deleteCommentRequest).getMessage(),
        ResultCode.COMMENT_DELETE_SUCCESS.getMessage());
  }
}