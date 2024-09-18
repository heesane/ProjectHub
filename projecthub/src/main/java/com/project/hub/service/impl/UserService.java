package com.project.hub.service.impl;

import com.project.hub.entity.ProjectLikes;
import com.project.hub.entity.Projects;
import com.project.hub.entity.User;
import com.project.hub.model.dto.request.user.UpdateUserProfileRequest;
import com.project.hub.model.dto.request.user.UpdateUserProjectVisibleRequest;
import com.project.hub.model.dto.response.projects.ListShortProjectDetail;
import com.project.hub.model.dto.response.user.Profile;
import com.project.hub.model.mapper.ShortProjectDetail;
import com.project.hub.validator.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements com.project.hub.service.UserService {

  private final Validator validator;

  @Override
  public Profile myProfile(Long userId) {

    User user = validator.validateAndGetUser(userId);

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
        .badge(user.getBadge().getName())
        .build();

  }

  @Transactional
  @Override
  public String changeNickname(UpdateUserProfileRequest request) {

      User user = validator.validateAndGetUser(request.getUserId());
      user.updateNickname(request.getNickname());
      return user.getNickname();
  }

  @Transactional
  @Override
  public String changeVisible(UpdateUserProjectVisibleRequest request) {

      User user = validator.validateAndGetUser(request.getUserId());
      Projects project = validator.validateAndGetProject(request.getProjectId());
      project.updateVisible();
      return user.getNickname() + "님의 프로젝트가 " + (project.isVisible() ? "공개" : "비공개") + "로 변경되었습니다.";
  }

}
