package com.project.hub.service.impl;

import static com.project.hub.model.type.UserRole.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.project.hub.auth.service.TokenService;
import com.project.hub.entity.Comments;
import com.project.hub.entity.ProjectLikes;
import com.project.hub.entity.Projects;
import com.project.hub.entity.User;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.exceptions.exception.UnmatchedUserException;
import com.project.hub.model.documents.ProjectDocuments;
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
import com.project.hub.repository.document.ProjectDocumentsRepository;
import com.project.hub.repository.jpa.ProjectRepository;
import com.project.hub.util.PictureManager;
import com.project.hub.validator.Validator;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class UserProjectServiceTest {

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private ProjectDocumentsRepository projectDocumentsRepository;

  @Mock
  private PictureManager pictureManager;

  @Mock
  private Validator validator;

  @Mock
  private TokenService tokenService;


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

  private MyProjectListRequest successMyProjectListRequestLATEST;
  private MyProjectListRequest successMyProjectListRequestLIKE;
  private MyProjectListRequest successMyProjectListRequestCOMMENT;

  private MyProjectListRequest failMyProjectListRequest;

  private ProjectListRequest successProjectListRequestLATEST;
  private ProjectListRequest successProjectListRequestLIKE;
  private ProjectListRequest successProjectListRequestCOMMENT;

  private ProjectListRequest failProjectListRequest;

  private ProjectRequest successProjectRequest;

  private ProjectRequest failProjectRequest;

  private Projects project1;

  private Projects project2;

  private Projects project3;

  private Comments comment1;

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

    successMyProjectListRequestLATEST = new MyProjectListRequest(0, 5, Sorts.LATEST, 1L);
    successMyProjectListRequestLIKE = new MyProjectListRequest(0, 5, Sorts.LIKE, 1L);
    successMyProjectListRequestCOMMENT = new MyProjectListRequest(0, 5, Sorts.COMMENTS, 1L);

    failMyProjectListRequest = new MyProjectListRequest(0, 5, Sorts.LATEST, 2L);

    successProjectListRequestLATEST = new ProjectListRequest(0, 5, Sorts.LATEST);
    successProjectListRequestLIKE = new ProjectListRequest(0, 5, Sorts.LIKE);
    successProjectListRequestCOMMENT = new ProjectListRequest(0, 5, Sorts.COMMENTS);

    failProjectListRequest = new ProjectListRequest(0, 5, null);

    successProjectRequest = new ProjectRequest(1L);

    failProjectRequest = new ProjectRequest(2L);

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
        .likeCounts(20L)
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
        .likeCounts(30L)
        .build();

    comment1 = Comments.builder()
        .id(1L)
        .user(successUser)
        .contents("comment1")
        .build();
  }

  @Test
  @DisplayName("전체 프로젝트 리스트 조회 성공(최신순)")
  void listProjectsLATEST() {
    // given
    project1 = Projects.builder()
        .id(1L)
        .user(successUser)
        .title("project1")
        .subject("project1")
        .feature("project1")
        .contents("project1")
        .skills(skills1)
        .tools(tools1)
        .systemArchitectureUrl("project1")
        .erdUrl("project1")
        .githubUrl("https://github.com/heesane")
        .likeCounts(0L)
        .visible(true)
        .build();

    project1.setRegisteredAt(LocalDateTime.of(2021, 1, 1, 0, 0));
    project2.setRegisteredAt(LocalDateTime.of(2021, 1, 2, 0, 0));
    List<Projects> projects = List.of(project1, project2);
    Page<Projects> projectPage = new PageImpl<>(projects); // PageImpl을 사용하여 Page 객체 생성
    // when
    when(projectRepository.findAll((Pageable) any())).thenReturn(projectPage);
    // then
    assertEquals(
        userProjectService.listProjects(successProjectListRequestLATEST).getProjectDetails().size(), 2);
    assertEquals("project1",
        userProjectService.listProjects(successProjectListRequestLATEST).getProjectDetails().get(0)
            .getTitle());
  }

  @Test
  @DisplayName("전체 프로젝트 리스트 조회 성공(좋아요순)")
  void listProjectsLIKE() {
    // given
    project1 = Projects.builder()
        .id(1L)
        .user(successUser)
        .title("project1")
        .subject("project1")
        .feature("project1")
        .contents("project1")
        .skills(skills1)
        .tools(tools1)
        .systemArchitectureUrl("project1")
        .erdUrl("project1")
        .githubUrl("https://github.com/heesane")
        .likeCounts(10L)
        .visible(true)
        .build();

    List<Projects> projects = List.of(project3, project2, project1);
    Page<Projects> projectPage = new PageImpl<>(projects); // PageImpl을 사용하여 Page 객체 생성
    // when
    when(projectRepository.findAll((Pageable) any())).thenReturn(projectPage);
    // then
    assertEquals(
        3,
        userProjectService.listProjects(successProjectListRequestLIKE).getProjectDetails().size());

    assertEquals(
        "project1",
        userProjectService.listProjects(successProjectListRequestLIKE).getProjectDetails().get(2)
            .getTitle());

  }

  @Test
  @DisplayName("전체 프로젝트 리스트 조회 성공(댓글순)")
  void listProjects() {
    // given
    project1 = Projects.builder()
        .id(1L)
        .user(successUser)
        .title("project1")
        .subject("project1")
        .feature("project1")
        .contents("project1")
        .skills(skills1)
        .tools(tools1)
        .systemArchitectureUrl("project1")
        .erdUrl("project1")
        .githubUrl("https://github.com/heesane")
        .likeCounts(0L)
        .visible(true)
        .build();

    List<Projects> projects = List.of(project2, project1, project3);
    Page<Projects> projectPage = new PageImpl<>(projects); // PageImpl을 사용하여 Page 객체 생성
    // when
    when(projectRepository.findAllOrderByCommentsCountDesc(any())).thenReturn(projectPage);
    // then
    assertEquals(
        3,
        userProjectService.listProjects(successProjectListRequestCOMMENT).getProjectDetails().size());
    assertEquals("project2",
        userProjectService.listProjects(successProjectListRequestCOMMENT).getProjectDetails().get(0)
            .getTitle());
  }

  @Test
  @DisplayName("특정 프로젝트 조회 성공")
  void getProjectDetail() {
    // given
    ProjectRequest projectRequest = new ProjectRequest(1L);
    project1 = Projects.builder()
        .id(1L)
        .user(successUser)
        .title("project1")
        .subject("project1")
        .feature("project1")
        .contents("project1")
        .skills(skills1)
        .tools(tools1)
        .systemArchitectureUrl("project1")
        .erdUrl("project1")
        .githubUrl("https://github.com/heesane")
        .likeCounts(0L)
        .visible(true)
        .comments(List.of(comment1))
        .build();

    // when
    when(validator.validateAndGetProject(projectRequest.getId())).thenReturn(project1);

    // then
    assertEquals(
        userProjectService.getProjectDetail(projectRequest).getProjectDetail().getTitle(),
        project1.getTitle());
  }

  @Test
  @DisplayName("특정 프로젝트 조회 실패 - 프로젝트 없음")
  void getProjectDetailFail() {
    // given
    // when
    when(validator.validateAndGetProject(failProjectRequest.getId())).thenThrow(
        NotFoundException.class);
    // then
    assertThrows(NotFoundException.class,
        () -> userProjectService.getProjectDetail(failProjectRequest));
  }

  @DisplayName("마이프로필 조회 실패 - 사용자 불일치")
  @Test
  void failMyProfileBecauseUnmatchedUser(){
      //given
      //when
    when(tokenService.extractEmail((HttpServletRequest) any())).thenReturn(
        Optional.of("test@gmail.com"));
    when(validator.validateAndGetUser(anyLong())).thenReturn(successUser);
      //then
    assertThrows(UnmatchedUserException.class,
        () -> userProjectService.getMyProjectDetail(any(), successMyProjectListRequestLATEST));
  }

  @Test
  @DisplayName("자신의 프로젝트 리스트 조회 성공(최신순)")
  void getMyProjectDetail() {
    // given
    project1 = Projects.builder()
        .id(1L)
        .user(successUser)
        .title("project1")
        .subject("project1")
        .feature("project1")
        .contents("project1")
        .skills(skills1)
        .tools(tools1)
        .systemArchitectureUrl("project1")
        .erdUrl("project1")
        .githubUrl("https://github.com/heesane")
        .likeCounts(0L)
        .visible(true)
        .comments(List.of(comment1))
        .build();
    project1.setRegisteredAt(LocalDateTime.of(2021, 1, 1, 0, 0));
    project2.setRegisteredAt(LocalDateTime.of(2021, 1, 2, 0, 0));
    List<Projects> projects = new ArrayList<>(List.of(project1, project2));
    projects.sort((o1, o2) -> o2.getRegisteredAt().compareTo(o1.getRegisteredAt()));
    Page<Projects> projectPage = new PageImpl<>(projects); // PageImpl을 사용하여 Page 객체 생성

    MockHttpServletRequest request = new MockHttpServletRequest();

    Pageable pageable = PageRequest.of(successMyProjectListRequestLIKE.getPage(),
        successMyProjectListRequestLIKE.getSize(),
        Sort.by(Sort.Direction.ASC, "registeredAt"));

    // when
    when(tokenService.extractEmail(request))
        .thenReturn(Optional.ofNullable(successUser.getEmail()));
    when(validator.validateAndGetUser(successMyProjectListRequestLATEST.getUserId()))
        .thenReturn(successUser);
    when(projectRepository.findAllByUserId(successMyProjectListRequestLATEST.getUserId(), pageable))
        .thenReturn(projectPage);
    // then
    assertEquals(project2.getTitle(),
        userProjectService.getMyProjectDetail(request, successMyProjectListRequestLATEST)
            .getProjectDetails()
            .get(0).getTitle());

  }

  @Test
  @DisplayName("자신의 프로젝트 리스트 조회 성공(좋아요순)")
  void getMyProjectDetailLIKE() {
    // given
    project1 = Projects.builder()
        .id(1L)
        .user(successUser)
        .title("project1")
        .subject("project1")
        .feature("project1")
        .contents("project1")
        .skills(skills1)
        .tools(tools1)
        .systemArchitectureUrl("project1")
        .erdUrl("project1")
        .githubUrl("https://github.com/heesane")
        .likeCounts(0L)
        .visible(true)
        .comments(List.of(comment1))
        .build();

    List<Projects> projects = new ArrayList<>(List.of(project1, project2));
    Page<Projects> projectPage = new PageImpl<>(projects); // PageImpl을 사용하여 Page 객체 생성

    MockHttpServletRequest request = new MockHttpServletRequest();

    // when
    when(tokenService.extractEmail(request))
        .thenReturn(Optional.ofNullable(successUser.getEmail()));
    when(validator.validateAndGetUser(successMyProjectListRequestLIKE.getUserId()))
        .thenReturn(successUser);
    when(projectRepository.findAllByUserId(successMyProjectListRequestLIKE.getUserId(), PageRequest.of(
        successMyProjectListRequestLIKE.getPage(),
        successMyProjectListRequestLIKE.getSize(),
        Sort.by(Direction.DESC, "likeCounts")))
    ).thenReturn(projectPage);

    // then
    assertEquals(project1.getTitle(),
        userProjectService.getMyProjectDetail(request, successMyProjectListRequestLIKE)
            .getProjectDetails()
            .get(0).getTitle());

  }

  @Test
  @DisplayName("자신의 프로젝트 리스트 조회 성공(댓글순)")
  void getMyProjectDetailCOMMENT() {
    // given
    project1 = Projects.builder()
        .id(1L)
        .user(successUser)
        .title("project1")
        .subject("project1")
        .feature("project1")
        .contents("project1")
        .skills(skills1)
        .tools(tools1)
        .systemArchitectureUrl("project1")
        .erdUrl("project1")
        .githubUrl("https://github.com/heesane")
        .likeCounts(0L)
        .visible(true)
        .comments(List.of(comment1))
        .build();
    project1.setRegisteredAt(LocalDateTime.of(2021, 1, 1, 0, 0));
    project2.setRegisteredAt(LocalDateTime.of(2021, 1, 2, 0, 0));
    List<Projects> projects = new ArrayList<>(List.of(project1, project2));
    projects.sort((o1, o2) -> o2.getRegisteredAt().compareTo(o1.getRegisteredAt()));
    Page<Projects> projectPage = new PageImpl<>(projects); // PageImpl을 사용하여 Page 객체 생성

    MockHttpServletRequest request = new MockHttpServletRequest();

    Pageable pageable = PageRequest.of(successMyProjectListRequestLIKE.getPage(),
        successMyProjectListRequestLIKE.getSize(),
        Sort.by(Sort.Direction.ASC, "registeredAt"));

    // when
    when(tokenService.extractEmail(request))
        .thenReturn(Optional.ofNullable(successUser.getEmail()));
    when(validator.validateAndGetUser(successMyProjectListRequestLATEST.getUserId()))
        .thenReturn(successUser);
    when(projectRepository.findAllByUserId(successMyProjectListRequestLATEST.getUserId(), pageable))
        .thenReturn(projectPage);
    // then
    assertEquals(project2.getTitle(),
        userProjectService.getMyProjectDetail(request, successMyProjectListRequestLATEST)
            .getProjectDetails()
            .get(0).getTitle());

  }

  @Test
  @DisplayName("프로젝트 생성 성공")
  void createProject() throws IOException, NoSuchAlgorithmException {
    // given
    Long userId = successProjectCreateRequest.getUserId();

    // when
    when(validator.validateAndGetUser(userId)).thenReturn(successUser);
    when(pictureManager.upload(anyLong(), any(), any(), any())).thenReturn("systemArchitectureUrl");
    when(pictureManager.upload(anyLong(), any(), any(), any())).thenReturn("uploadedErdUrl");

    ResultResponse resultResponse = userProjectService.createProject(successProjectCreateRequest);
    // then
    assertEquals(resultResponse.getMessage(), ResultCode.PROJECT_CREATE_SUCCESS.getMessage());
  }

  @Test
  @DisplayName("프로젝트 업데이트 성공")
  void updateProject() throws IOException {

    // given
    project1 = Projects.builder()
        .id(1L)
        .user(successUser)
        .title("project1")
        .subject("project1")
        .feature("project1")
        .contents("project1")
        .skills(skills1)
        .tools(tools1)
        .systemArchitectureUrl("project1")
        .erdUrl("project1")
        .githubUrl("https://github.com/heesane")
        .likeCounts(0L)
        .visible(true)
        .comments(List.of(comment1))
        .build();
    MockHttpServletRequest request = new MockHttpServletRequest();
    // when
    when(tokenService.extractEmail((HttpServletRequest) any())).thenReturn(
        Optional.ofNullable(successUser.getEmail()));
    when(projectRepository.findById(anyLong())).thenReturn(Optional.ofNullable(project1));
    when(validator.validateAndGetUser(anyLong())).thenReturn(successUser);
    when(pictureManager.upload(anyLong(), any(), any(), any())).thenReturn("systemArchitectureUrl");
    when(pictureManager.upload(anyLong(), any(), any(), any())).thenReturn("uploadedErdUrl");
    when(projectDocumentsRepository.findById(project1.getId())).thenReturn(
        Optional.ofNullable(
            ProjectDocuments.builder()
                .id(project1.getId())
                .build()
        )
    );

    ResultResponse resultResponse = userProjectService.updateProject(request,
        successProjectUpdateRequest);

    // then
    assertEquals(resultResponse.getCode(), ResultCode.PROJECT_UPDATE_SUCCESS.getCode());
  }

  @Test
  @DisplayName("프로젝트 업데이트 실패 - 사용자 불일치")
  void updateProjectFail() {

    // given
    project1 = Projects.builder()
        .id(1L)
        .user(successUser)
        .title("project1")
        .subject("project1")
        .feature("project1")
        .contents("project1")
        .skills(skills1)
        .tools(tools1)
        .systemArchitectureUrl("project1")
        .erdUrl("project1")
        .githubUrl("https://github.com/heesane")
        .likeCounts(0L)
        .visible(true)
        .comments(List.of(comment1))
        .build();
    // when
    when(tokenService.extractEmail((HttpServletRequest) any())).thenReturn(
        Optional.ofNullable(successUser.getEmail()));
    when(projectRepository.findById(failProjectUpdateRequest.getProjectId())).thenReturn(
        Optional.of(project1));
    when(validator.validateAndGetUser(failProjectUpdateRequest.getUserId())).thenReturn(failUser);

    // then
    assertThrows(UnmatchedUserException.class,
        () -> userProjectService.updateProject((HttpServletRequest) any(),
            failProjectUpdateRequest));
  }

  @Test
  @DisplayName("프로젝트 삭제 성공")
  void deleteProject() {
    //given
    project1 = Projects.builder()
        .id(1L)
        .user(successUser)
        .title("project1")
        .subject("project1")
        .feature("project1")
        .contents("project1")
        .skills(skills1)
        .tools(tools1)
        .systemArchitectureUrl("project1")
        .erdUrl("project1")
        .githubUrl("https://github.com/heesane")
        .likeCounts(0L)
        .visible(true)
        .comments(List.of(comment1))
        .build();
    //when
    when(tokenService.extractEmail((HttpServletRequest) any())).thenReturn(
        Optional.ofNullable(successUser.getEmail()));
    when(validator.validateAndGetUser(anyLong())).thenReturn(successUser);
    when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project1));

    ResultResponse resultResponse = userProjectService.deleteProject(any(),
        successProjectDeleteRequest);

    //then
    assertEquals(resultResponse.getMessage(), ResultCode.PROJECT_DELETE_SUCCESS.getMessage());
  }
}