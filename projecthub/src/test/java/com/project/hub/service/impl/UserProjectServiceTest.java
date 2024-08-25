package com.project.hub.service.impl;

import static com.project.hub.model.type.UserRole.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.project.hub.entity.Projects;
import com.project.hub.entity.User;
import com.project.hub.exception.exception.UnmatchedUserException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class UserProjectServiceTest {

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PictureManager pictureManager;

  @InjectMocks
  private UserProjectService userProjectService;

  private List<Skills> skills1;

  private List<Skills> skills2;

  private List<Tools> tools1;

  private List<Tools> tools2;

  private User successUser;

  private User failUser;

  private ProjectCreateRequest projectCreateRequest1;

  private ProjectCreateRequest projectCreateRequest2;

  private ProjectUpdateRequest projectUpdateRequest1;

  private ProjectUpdateRequest projectUpdateRequest2;

  private ProjectUpdateRequest projectUpdateRequest3;

  private ProjectDeleteRequest projectDeleteRequest1;

  private ProjectDeleteRequest projectDeleteRequest2;

  private MyProjectListRequest myProjectListRequest1;

  private MyProjectListRequest myProjectListRequest2;

  private ProjectListRequest projectListRequest1;

  private ProjectListRequest projectListRequest2;

  private ProjectRequest projectRequest1;

  private ProjectRequest projectRequest2;

  private Projects project1;

  private Projects project2;

  private Projects project3;

  private MockMultipartFile systemArchitecturePicture1;

  private MockMultipartFile systemArchitecturePicture2;

  private MockMultipartFile erdPicture1;

  private MockMultipartFile erdPicture2;

  @BeforeEach
  void setUp(){

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

    projectCreateRequest1 = new ProjectCreateRequest(
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

    projectCreateRequest2 = new ProjectCreateRequest(
        1L,
        "project2",
        "project2",
        "project2",
        "project2",
        skills2,
        tools2,
        systemArchitecturePicture2,
        erdPicture2,
        "https://github.com/heesane",
        true);

    projectUpdateRequest1 = new ProjectUpdateRequest(
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

    projectUpdateRequest2 = new ProjectUpdateRequest(
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

    projectUpdateRequest3 = new ProjectUpdateRequest(
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

    projectDeleteRequest1 = new ProjectDeleteRequest(1L, 1L);

    projectDeleteRequest2 = new ProjectDeleteRequest(1L, 2L);

    myProjectListRequest1 = new MyProjectListRequest(0,5, Sorts.LATEST, 1L);

    myProjectListRequest2 = new MyProjectListRequest(0,5, Sorts.LATEST, 2L);

    projectListRequest1 = new ProjectListRequest(0,5, Sorts.LATEST);

    projectListRequest2 = new ProjectListRequest(0,5, Sorts.LATEST);

    projectRequest1 = new ProjectRequest(1L);

    projectRequest2 = new ProjectRequest(2L);

    project1 = Projects.builder()
        .id(1L)
        .user(successUser)
        .title("project1")
        .subject("project1")
        .contents("project1")
        .skills(skills1)
        .tools(tools1)
        .systemArchitectureUrl("project1")
        .hashSystemArchitecture("projectSystemArchitecture1Hash")
        .erdUrl("project1")
        .hashErd("projectERD1Hash")
        .githubUrl("https://github.com/heesane")
        .visible(true)
        .build();

    project2 = Projects.builder()
        .id(2L)
        .user(failUser)
        .title("project2")
        .subject("project2")
        .contents("project2")
        .skills(skills2)
        .tools(tools2)
        .systemArchitectureUrl("project2")
        .hashSystemArchitecture("projectSystemArchitecture2Hash")
        .erdUrl("project2")
        .hashErd("projectERD2Hash")
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
    when(projectRepository.findAllByDeletedAtIsNullOrderByRegisteredAtDesc(any()))
        .thenReturn(projectPage);
    // then
    assertEquals(userProjectService.listProjects(projectListRequest1).getProjectDetails().size(), 2);
    assertEquals("project1",
        userProjectService.listProjects(projectListRequest1).getProjectDetails().get(0).getTitle());
  }

  @Test
  @DisplayName("특정 프로젝트 조회 성공")
  void getProjectDetail() {
    // given
    // when
    when(projectRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(project1));
    // then
    assertEquals(userProjectService.getProjectDetail(projectRequest1).getProjectDetail().getTitle(), "project1");
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
    when(projectRepository.findAllByUserIdAndDeletedAtIsNullOrderByRegisteredAtDesc(any(), any()))
        .thenReturn(projectPage);
    // then
    assertEquals(userProjectService.getMyProjectDetail(myProjectListRequest1).getProjectDetails().size(), 2);
    assertEquals("project1",
        userProjectService.getMyProjectDetail(myProjectListRequest1).getProjectDetails().get(0).getTitle());

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

    ResultResponse resultResponse = userProjectService.createProject(projectCreateRequest1);
    // then
    assertEquals(resultResponse.getMessage(), ResultCode.PROJECT_CREATE_SUCCESS.getMessage());
  }

  @Test
  @DisplayName("프로젝트 업데이트 성공")
  void updateProject() throws IOException, NoSuchAlgorithmException {

    // given

    // when
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(successUser));
    when(projectRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(project1));
    when(pictureManager.upload(anyLong(), any(), any(), any())).thenReturn("systemArchitectureUrl");
    when(pictureManager.diff(any(),any())).thenReturn(true);

    String message = userProjectService.updateProject(projectUpdateRequest2).getMessage();

    // then
    assertEquals(message, ResultCode.PROJECT_UPDATE_SUCCESS.getMessage());
  }

  @Test
  @DisplayName("프로젝트 업데이트 실패 - 사용자 불일치")
  void updateProjectFail() throws IOException, NoSuchAlgorithmException {

    // given

    // when
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(successUser));
    when(projectRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(project1));

    // then
    assertThrows(UnmatchedUserException.class,
        ()->userProjectService.updateProject(projectUpdateRequest3));
  }

  @Test
  @DisplayName("프로젝트 삭제 성공")
  void deleteProject() {
    //given
    //when
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(successUser));
    when(projectRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(project1));

    ResultResponse resultResponse = userProjectService.deleteProject(projectDeleteRequest1);

    //then
    assertEquals(resultResponse.getMessage(), ResultCode.PROJECT_DELETE_SUCCESS.getMessage());
  }
}