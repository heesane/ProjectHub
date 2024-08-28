package com.project.hub.service.impl;

import static com.project.hub.model.type.UserRole.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.project.hub.entity.Projects;
import com.project.hub.entity.User;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.exceptions.exception.UnmatchedUserException;
import com.project.hub.model.dto.request.projects.MyProjectListRequest;
import com.project.hub.model.dto.request.projects.ProjectCreateRequest;
import com.project.hub.model.dto.request.projects.ProjectDeleteRequest;
import com.project.hub.model.dto.request.projects.ProjectListRequest;
import com.project.hub.model.dto.request.projects.ProjectRequest;
import com.project.hub.model.dto.request.projects.ProjectUpdateRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.type.ResultCode;
import com.project.hub.model.type.Skills;
import com.project.hub.model.type.Sorts;
import com.project.hub.model.type.Tools;
import com.project.hub.repository.ProjectRepository;
import com.project.hub.repository.UserRepository;
import com.project.hub.util.PictureManager;
import com.project.hub.validator.ProjectValidator;
import com.project.hub.validator.UserValidator;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class UserProjectServiceTest {

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PictureManager pictureManager;

  @Mock
  private ProjectValidator projectValidator;

  @Mock
  private UserValidator userValidator;

  @InjectMocks
  private UserProjectService userProjectService;

  private List<Skills> skills1;

  private List<Skills> skills2;

  private List<Tools> tools1;

  private List<Tools> tools2;

  private User successUser;

  private User failUser;

  private ProjectCreateRequest successProjectCreateRequest;

  private ProjectCreateRequest failProjectCreateRequest;

  private ProjectUpdateRequest successProjectUpdateRequest;

  private ProjectUpdateRequest failProjectUpdateRequest;

  private ProjectDeleteRequest successProjectDeleteRequest;

  private ProjectDeleteRequest failProjectDeleteRequest;

  private MyProjectListRequest successMyProjectListRequest;

  private MyProjectListRequest failMyProjectListRequest;

  private ProjectListRequest successProjectListRequest;

  private ProjectListRequest failProjectListRequest;

  private ProjectRequest successProjectRequest;

  private ProjectRequest failProjectRequest;

  private Projects project1;

  private Projects project2;

  private Projects project3;

  private MockMultipartFile systemArchitecturePicture1;

  private MockMultipartFile systemArchitecturePicture2;

  private MockMultipartFile erdPicture1;

  private MockMultipartFile erdPicture2;

  @BeforeEach
  void setUp() {

    // 성공 유저
    successUser = User.builder()
        .id(1L)
        .email("registerTest@gmail.com")
        .nickname("registerTest")
        .password("register")
        .role(USER)
        .build();

    // 실패 유저
    failUser = User.builder()
        .id(2L)
        .email("loginTest@gmail.com")
        .nickname("loginTest")
        .password("login")
        .role(USER)
        .build();

    skills1 = List.of(Skills.AWS, Skills.JAVA, Skills.JAVASCRIPT, Skills.JPA, Skills.KAFKA);

    skills2 = List.of(Skills.AWS, Skills.JAVA, Skills.JAVASCRIPT);

    tools1 = List.of(Tools.JIRA, Tools.SLACK);

    tools2 = List.of(Tools.DISCORD, Tools.GOOGLE_MEET);

    systemArchitecturePicture1 = new MockMultipartFile(
        "file", "architecture1.png", "image/png", "1 System Architecture".getBytes()
    );

    systemArchitecturePicture2 = new MockMultipartFile(
        "file", "architecture2.png", "image/png", "System Architecture 2".getBytes()
    );

    erdPicture1 = new MockMultipartFile(
        "file", "erd1.png", "image/png", "Some ERD 1".getBytes()
    );

    erdPicture2 = new MockMultipartFile(
        "file", "erd2.png", "image/png", "1 ERD SOME".getBytes()
    );

    successProjectCreateRequest = new ProjectCreateRequest(
        1L,
        "project1",
        "project1",
        "project1",
        "project1",
        skills1,
        tools1,
        systemArchitecturePicture1,
        erdPicture1,
        "https://github.com/heesane",
        true);

    failProjectCreateRequest = new ProjectCreateRequest(
        1L,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        true);

    successProjectUpdateRequest = new ProjectUpdateRequest(
        1L,
        1L,
        "project1",
        "project1",
        "project1",
        "project1",
        skills1,
        tools1,
        systemArchitecturePicture1,
        erdPicture1,
        "https://github.com/heesane",
        true
    );

    failProjectUpdateRequest = new ProjectUpdateRequest(
        2L,
        2L,
        "project2",
        "project2",
        "project2",
        "project2",
        skills2,
        tools2,
        systemArchitecturePicture2,
        erdPicture2,
        "https://github.com/heesane",
        true
    );

    successProjectDeleteRequest = new ProjectDeleteRequest(1L, 1L);

    failProjectDeleteRequest = new ProjectDeleteRequest(1L, 2L);

    successMyProjectListRequest = new MyProjectListRequest(0, 5, Sorts.LATEST, 1L);

    failMyProjectListRequest = new MyProjectListRequest(0, 5, Sorts.LATEST, 2L);

    successProjectListRequest = new ProjectListRequest(0, 5, Sorts.LATEST);

    failProjectListRequest = new ProjectListRequest(0, 5, Sorts.LATEST);

    successProjectRequest = new ProjectRequest(1L);

    failProjectRequest = new ProjectRequest(2L);

    project1 = Projects.builder()
        .id(1L)
        .user(successUser)
        .title("project1")
        .subject("project1")
        .contents("project1")
        .skills(skills1)
        .tools(tools1)
        .systemArchitectureUrl("project1")
        .erdUrl("project1")
        .githubUrl("https://github.com/heesane")
        .visible(true)
        .build();

    project2 = Projects.builder()
        .id(2L)
        .user(successUser)
        .title("project2")
        .subject("project2")
        .contents("project2")
        .skills(skills2)
        .tools(tools2)
        .systemArchitectureUrl("project2")
        .erdUrl("project2")
        .githubUrl("https://github.com/heesane2")
        .visible(false)
        .build();

    project3 = Projects.builder()
        .id(3L)
        .user(failUser)
        .title(null)
        .subject(null)
        .contents(null)
        .skills(skills2)
        .tools(tools2)
        .systemArchitectureUrl("project2")
        .erdUrl("project2")
        .githubUrl("https://github.com/heesane2")
        .visible(false)
        .build();
  }

  @Test
  @DisplayName("전체 프로젝트 리스트 조회 성공(최신순)")
  void listProjects() {
    // given
    project1.setRegisteredAt(LocalDateTime.of(2021, 1, 1, 0, 0));
    project2.setRegisteredAt(LocalDateTime.of(2021, 1, 2, 0, 0));
    List<Projects> projects = List.of(project1, project2);
    Page<Projects> projectPage = new PageImpl<>(projects); // PageImpl을 사용하여 Page 객체 생성
    // when
    when(projectRepository.findAll((Pageable) any())).thenReturn(projectPage);
    // then
    assertEquals(
        userProjectService.listProjects(successProjectListRequest).getProjectDetails().size(), 2);
    assertEquals("project1",
        userProjectService.listProjects(successProjectListRequest).getProjectDetails().get(0)
            .getTitle());
  }

  @Test
  @DisplayName("특정 프로젝트 조회 성공")
  void getProjectDetail() {
    // given
    // when
    when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project1));
    // then
    assertEquals(
        userProjectService.getProjectDetail(successProjectRequest).getProjectDetail().getTitle(),
        "project1");
  }

  @Test
  @DisplayName("특정 프로젝트 조회 실패 - 프로젝트 없음")
  void getProjectDetailFail() {
    // given
    // when
    // then
    assertThrows(NotFoundException.class,
        () -> userProjectService.getProjectDetail(failProjectRequest));
  }

  @Test
  @DisplayName("자신의 프로젝트 리스트 조회 성공(최신순)")
  void getMyProjectDetail() {
    // given
    project1.setRegisteredAt(LocalDateTime.of(2021, 1, 1, 0, 0));
    project2.setRegisteredAt(LocalDateTime.of(2021, 1, 2, 0, 0));
    List<Projects> projects = List.of(project1, project2);
    Page<Projects> projectPage = new PageImpl<>(projects); // PageImpl을 사용하여 Page 객체 생성
    // when
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(successUser));
    when(projectRepository.findAllByUserId(any(), any()))
        .thenReturn(projectPage);
    // then
    assertEquals(
        userProjectService.getMyProjectDetail(successMyProjectListRequest).getProjectDetails()
            .size(), 2);
    assertEquals("project1",
        userProjectService.getMyProjectDetail(successMyProjectListRequest).getProjectDetails()
            .get(0).getTitle());

  }

  @Test
  @DisplayName("프로젝트 생성 성공")
  void createProject() throws IOException, NoSuchAlgorithmException {
    // given

    // when
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(successUser));
    when(pictureManager.upload(anyLong(), any(), any(), any())).thenReturn("systemArchitectureUrl");
    when(pictureManager.calculateSHA256Base64(any())).thenReturn("sysahash");
    when(pictureManager.upload(anyLong(), any(), any(), any())).thenReturn("ERDurl");
    when(pictureManager.calculateSHA256Base64(any())).thenReturn("erdhash");

    ResultResponse resultResponse = userProjectService.createProject(successProjectCreateRequest);
    // then
    assertEquals(resultResponse.getMessage(), ResultCode.PROJECT_CREATE_SUCCESS.getMessage());
  }

  @Test
  @DisplayName("프로젝트 업데이트 성공")
  void updateProject() throws IOException, NoSuchAlgorithmException {

    // given

    // when
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(successUser));
    when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project1));
    when(pictureManager.upload(anyLong(), any(), any(), any())).thenReturn("systemArchitectureUrl");

    String message = userProjectService.updateProject(successProjectUpdateRequest).getMessage();

    // then
    assertEquals(message, ResultCode.PROJECT_UPDATE_SUCCESS.getMessage());
  }

  @Test
  @DisplayName("프로젝트 업데이트 실패 - 사용자 불일치")
  void updateProjectFail() {

    // given

    // when
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(successUser));
    when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project1));

    // then
    assertThrows(UnmatchedUserException.class,
        () -> userProjectService.updateProject(failProjectUpdateRequest));
  }

  @Test
  @DisplayName("프로젝트 삭제 성공")
  void deleteProject() {
    //given
    //when
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(successUser));
    when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project1));

    ResultResponse resultResponse = userProjectService.deleteProject(successProjectDeleteRequest);

    //then
    assertEquals(resultResponse.getMessage(), ResultCode.PROJECT_DELETE_SUCCESS.getMessage());
  }
}