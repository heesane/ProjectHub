package com.project.hub.service.impl;

import static com.project.hub.model.type.UserRole.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.project.hub.auth.service.TokenService;
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
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private Validator validator;

  @Mock
  private TokenService tokenService;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  @DisplayName("내 프로필 조회하기")
  void myProfile() {

    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    Long userId = 1L;
    User user = User.builder()
        .email("login@gmail.com")
        .nickname("login")
        .password("login")
        .projects(
            List.of(
                Projects.builder().id(1L).title("project1").subject("subject1").build(),
                Projects.builder().id(2L).title("project2").subject("subject2").build(),
                Projects.builder().id(3L).title("project3").subject("subject3").build()
            )
        )
        .role(USER)
        .build();

    List<ShortProjectDetail> userProjectList = user.getProjects().stream()
        .map(ShortProjectDetail::new)
        .toList();

    List<ProjectLikes> projectLikes = List.of(
        ProjectLikes.builder().userId(1L).projectId(1L).build(),
        ProjectLikes.builder().userId(1L).projectId(2L).build(),
        ProjectLikes.builder().userId(1L).projectId(3L).build()
    );

    ListShortProjectDetail listShortProjectDetail = new ListShortProjectDetail(userProjectList);
    List<Long> projectIds = projectLikes.stream().map(ProjectLikes::getProjectId).toList();

    Profile profile = Profile.builder()
        .email(user.getEmail())
        .nickname((user.getNickname()))
        .commentCounts(5L)
        .projectCounts((long)userProjectList.size())
        .projects(listShortProjectDetail)
        .likedProjects(listShortProjectDetail)
        .badge(user.getBadge() == null ? "없음" : user.getBadge().getName())
        .build();

    // when
    when(tokenService.extractEmail(request)).thenReturn(Optional.ofNullable(user.getEmail()));
    when(validator.validateAndGetUser(userId)).thenReturn(user);
    when(validator.validateAndGetUserCommentsCount(userId)).thenReturn(5L);
    when(validator.validateAndGetProjectLike(userId)).thenReturn(projectLikes);
    long idx = 0L;
    for (Long projectId : projectIds) {
      idx++;
      when(validator.validateAndGetProject(projectId)).thenReturn(Projects.builder().id(idx).title("project"+idx).subject("subject"+idx).build());
    }

    // then

    assertEquals(
        profile.getNickname(),
        userService.myProfile(request, null, userId).getNickname()
    );

    assertEquals(
        profile.getLikedProjects().getProjectDetails().get(1).getTitle(),
        userService.myProfile(request, null, userId).getLikedProjects().getProjectDetails().get(1).getTitle()
    );


  }

  @Test
  @DisplayName("닉네임 변경하기")
  void changeNickname() {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();

    User user = User.builder()
        .email("login@gmail.com")
        .nickname("login")
        .password("login")
        .projects(
            List.of(
                Projects.builder().id(1L).title("project1").subject("subject1").build(),
                Projects.builder().id(2L).title("project2").subject("subject2").build(),
                Projects.builder().id(3L).title("project3").subject("subject3").build()
            )
        )
        .role(USER)
        .build();

    UpdateUserProfileRequest userRequest = new UpdateUserProfileRequest(
        1L,
        "newNickname"
    );
    // when
    when(tokenService.extractEmail(request)).thenReturn(Optional.ofNullable(user.getEmail()));
    when(validator.validateAndGetUser(anyLong())).thenReturn(user);

    // then
    assertEquals(
        userRequest.getNickname(),
        userService.changeNickname(request, userRequest)
    );
  }

  @Test
  @DisplayName("내 프로젝트 비공개/공개 전환하기")
  void changeVisible() {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();

    User user = User.builder()
        .email("login@gmail.com")
        .nickname("login")
        .password("login")
        .projects(
            List.of(
                Projects.builder().id(1L).title("project1").subject("subject1").visible(true).build(),
                Projects.builder().id(2L).title("project2").subject("subject2").visible(false).build(),
                Projects.builder().id(3L).title("project3").subject("subject3").visible(false).build()
            )
        )
        .role(USER)
        .build();

    UpdateUserProjectVisibleRequest userRequest = new UpdateUserProjectVisibleRequest(
        1L,
        1L
    );
    // when
    when(tokenService.extractEmail(request)).thenReturn(Optional.ofNullable(user.getEmail()));
    when(validator.validateAndGetUser(anyLong())).thenReturn(user);
    when(validator.validateAndGetProject(userRequest.getProjectId())).thenReturn(user.getProjects().get(0));
    // then
    assertEquals(
        user.getNickname() + "님의 프로젝트가 비공개" + "로 변경되었습니다.",
        userService.changeVisible(request, userRequest)
    );
  }
}