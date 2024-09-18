package com.project.hub.service.impl;

import com.project.hub.auth.service.TokenService;
import com.project.hub.entity.ProjectLikes;
import com.project.hub.entity.Projects;
import com.project.hub.entity.User;
import com.project.hub.exceptions.exception.TokenNotExistsException;
import com.project.hub.exceptions.exception.UnmatchedUserException;
import com.project.hub.model.dto.request.user.UpdateUserProfileRequest;
import com.project.hub.model.dto.request.user.UpdateUserProjectVisibleRequest;
import com.project.hub.model.dto.response.projects.ListShortProjectDetail;
import com.project.hub.model.dto.response.user.Profile;
import com.project.hub.model.mapper.ShortProjectDetail;
import com.project.hub.service.UserService;
import com.project.hub.validator.Validator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final Validator validator;
  private final TokenService tokenService;

  @Override
  public Profile myProfile(HttpServletRequest request, HttpServletResponse response, Long userId) {

    String userEmail = tokenService.extractEmail(request).orElseThrow(TokenNotExistsException::new);

    User user = validator.validateAndGetUser(userId);

    if (!userEmail.equals(user.getEmail())) {
      throw new UnmatchedUserException();
    }

    List<ShortProjectDetail> userProjectList = user.getProjects().stream()
        .map(ShortProjectDetail::new)
        .toList();

    return Profile.builder()
        .email(user.getEmail())
        .nickname((user.getNickname()))
        .commentCounts(validator.validateAndGetUserCommentsCount(userId))
        .projectCounts((long) userProjectList.size())
        .projects(new ListShortProjectDetail(userProjectList))
        .likedProjects(
            new ListShortProjectDetail(
                validator.validateAndGetProjectLike(userId).stream()
                    .map(ProjectLikes::getProjectId)
                    .map(validator::validateAndGetProject)
                    .map(ShortProjectDetail::new)
                    .toList()
            )
        )
        .badge(user.getBadge() == null ? "없음" : user.getBadge().getName())
        .build();

  }

  @Transactional
  @Override
  public String changeNickname(HttpServletRequest request,
      UpdateUserProfileRequest updateUserProfileRequest) {

    String userEmail = tokenService.extractEmail(request).orElseThrow(TokenNotExistsException::new);

    User user = validator.validateAndGetUser(updateUserProfileRequest.getUserId());

    if (!user.getEmail().equals(userEmail)) {
      throw new UnmatchedUserException();
    }

    user.updateNickname(updateUserProfileRequest.getNickname());
    return user.getNickname();
  }

  @Transactional
  @Override
  public String changeVisible(HttpServletRequest request,
      UpdateUserProjectVisibleRequest updateUserProjectVisibleRequest) {

    String userEmail = tokenService.extractEmail(request).orElseThrow(TokenNotExistsException::new);

    User user = validator.validateAndGetUser(updateUserProjectVisibleRequest.getUserId());

    if (!user.getEmail().equals(userEmail)) {
      throw new UnmatchedUserException();
    }

    Projects project = validator.validateAndGetProject(
        updateUserProjectVisibleRequest.getProjectId());
    project.updateVisible();
    return user.getNickname() + "님의 프로젝트가 " + (project.isVisible() ? "공개" : "비공개") + "로 변경되었습니다.";
  }

}
