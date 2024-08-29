package com.project.hub.service.impl;

import com.project.hub.entity.Projects;
import com.project.hub.entity.User;
import com.project.hub.exceptions.ExceptionCode;
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
import com.project.hub.model.dto.response.comments.Comment;
import com.project.hub.model.dto.response.projects.ListProjectResponse;
import com.project.hub.model.dto.response.projects.ProjectDetailResponse;
import com.project.hub.model.mapper.ProjectDetail;
import com.project.hub.model.mapper.ShortProjectDetail;
import com.project.hub.model.type.PictureType;
import com.project.hub.model.type.ResultCode;
import com.project.hub.model.type.Sorts;
import com.project.hub.repository.document.ProjectDocumentsRepository;
import com.project.hub.repository.jpa.ProjectRepository;
import com.project.hub.service.ProjectService;
import com.project.hub.util.PictureManager;
import com.project.hub.validator.ProjectValidator;
import com.project.hub.validator.UserValidator;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class UserProjectService implements ProjectService {

  private final ProjectRepository projectRepository;
  private final ProjectDocumentsRepository projectDocumentsRepository;
  private final PictureManager pictureManager;
  private final UserValidator userValidator;
  private final ProjectValidator projectValidator;

  @Override
  public ListProjectResponse listProjects(ProjectListRequest request) {

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

    Sorts sort = request.getSort();

    // 추후 개발을 위해 우선 선언
    Page<Projects> sortedProjects;

    if (sort == Sorts.LATEST) {
      pageable.getSort().and(Sort.by(Sort.Direction.DESC, "registeredAt"));
      sortedProjects = projectRepository.findAll(pageable);

      List<ShortProjectDetail> collect = sortedProjects.stream().map(ShortProjectDetail::new)
          .collect(Collectors.toList());

      return new ListProjectResponse(collect);

    } else if (sort == Sorts.LIKE) {
//      return projectRepository.findAllByLike(request); (추후 좋아요 기능 구현시)
      return null;
    } else if (sort == Sorts.COMMENTS) {
//      return projectRepository.findAllByComments(request); (추후 댓글 기능 구현시)
      return null;
    }
    return null;
  }

  @Override
  public ProjectDetailResponse getProjectDetail(ProjectRequest request) {

    Projects project = projectValidator.validateAndGetProject(request.getId());

    return new ProjectDetailResponse(new ProjectDetail(project));
  }

  @Override
  public ListProjectResponse getMyProjectDetail(MyProjectListRequest request) {

    userValidator.isUserExist(request.getUserId());

    Pageable pageable;

    Sorts sort = request.getSort();

    // 추후 개발을 위해 우선 선언
    Page<Projects> sortedProjects;

    if (sort == Sorts.LATEST) {
      pageable = PageRequest.of(request.getPage(), request.getSize(),
          Sort.by(Sort.Direction.DESC, "registeredAt"));
      sortedProjects = projectRepository.findAllByUserId(
          request.getUserId(),
          pageable);

      List<ShortProjectDetail> collect = sortedProjects.stream().map(ShortProjectDetail::new)
          .collect(Collectors.toList());

      return new ListProjectResponse(collect);

    } else if (sort == Sorts.LIKE) {
//      return projectRepository.findAllByLike(request); (추후 좋아요 기능 구현시)
      return null;
    } else if (sort == Sorts.COMMENTS) {
//      return projectRepository.findAllByComments(request); (추후 댓글 기능 구현시)
      return null;
    }
    return null;
  }

  @Override
  @Transactional
  public ResultResponse createProject(ProjectCreateRequest request)
      throws IOException {

    Long userId = request.getUserId();

    User user = userValidator.validateAndGetUser(userId);

    String uploadedSystemArchitectureUrl = pictureManager.upload(
        userId,
        request.getTitle(),
        request.getSystemArchitecturePicture(),
        PictureType.SYSTEM_ARCHITECTURE
    );

    String uploadedErdUrl = pictureManager.upload(
        userId,
        request.getTitle(),
        request.getErdPicture(),
        PictureType.ERD
    );

    Projects project = Projects.builder()
        .title(request.getTitle())
        .subject(request.getSubject())
        .feature(request.getFeature())
        .contents(request.getContents())
        .skills(request.getSkills())
        .tools(request.getTools())
        .systemArchitectureUrl(uploadedSystemArchitectureUrl)
        .erdUrl(uploadedErdUrl)
        .githubUrl(request.getGithubUrl())
        .user(user)
        .build();

    projectRepository.save(project);

    // Elasticsearch에 저장
    createProjectElasticsearch(project);

    return ResultResponse.of(ResultCode.PROJECT_CREATE_SUCCESS);
  }

  @Override
  @Transactional
  public ResultResponse updateProject(ProjectUpdateRequest request)
      throws IOException {

    Long userId = request.getUserId();

    userValidator.isUserExist(userId);

    Long projectId = request.getProjectId();

    Projects project = projectValidator.validateAndGetProject(projectId);

    // 사용자가 등록한 프로젝트가 아닌 경우
    if (!project.getUser().getId().equals(userId)) {
      throw new UnmatchedUserException();
    }

    // 업데이트 요청 시 이미지가 있는 경우만 변경

    if (request.getSystemArchitecturePicture() != null && !request.getSystemArchitecturePicture()
        .isEmpty()) {
      String uploadedSystemArchitectureUrl = pictureManager.upload(
          userId,
          request.getTitle(),
          request.getSystemArchitecturePicture(),
          PictureType.SYSTEM_ARCHITECTURE
      );
      project.updateSystemArchitecture(uploadedSystemArchitectureUrl);
    } else {
      log.info("System Architecture Picture is null");
    }

    if (request.getErdPicture() != null) {
      String uploadedErdUrl = pictureManager.upload(
          userId,
          request.getTitle(),
          request.getErdPicture(),
          PictureType.ERD
      );
      project.updateErd(uploadedErdUrl);
    } else {
      log.info("ERD Picture is null");
    }

    project.update(
        request.getTitle(),
        request.getSubject(),
        request.getFeature(),
        request.getContents(),
        request.getSkills(),
        request.getTools(),
        request.getGithubUrl(),
        request.isVisible()
    );

    return ResultResponse.of(ResultCode.PROJECT_UPDATE_SUCCESS);
  }

  @Override
  @Transactional
  public ResultResponse deleteProject(ProjectDeleteRequest request) {

    Long userId = request.getUserId();

    userValidator.isUserExist(userId);

    Long projectId = request.getProjectId();

    Projects project = projectRepository.findById(projectId).orElseThrow(
        () -> new NotFoundException(ExceptionCode.PROJECT_NOT_FOUND)
    );

    // 사용자가 등록한 프로젝트가 아닌 경우
    if (!project.getUser().getId().equals(userId)) {
      throw new UnmatchedUserException();
    }

    // 삭제된 프로젝트인 경우 처리 고려 중
    // 1. 댓글 및 좋아요 기능 CascadeType.ALL로 처리하여 자동 삭제
    // 2. 삭제된 프로젝트 하위 댓글들 모두 soft delete

    // soft delete
    projectRepository.delete(project);

    return ResultResponse.of(ResultCode.PROJECT_DELETE_SUCCESS);
  }

  @Transactional
  public void createProjectElasticsearch(Projects project) {
    ProjectDocuments newProjectDocuments = ProjectDocuments.builder().
        id(project.getId()).
        title(project.getTitle()).
        subject(project.getSubject()).
        feature(project.getFeature()).
        contents(project.getContents()).
        skills(project.getSkills().stream().map(Enum::name).collect(Collectors.toList())).
        tools(project.getTools().stream().map(Enum::name).collect(Collectors.toList())).
        systemArchitecture(project.getSystemArchitectureUrl()).
        erd(project.getErdUrl()).
        githubLink(project.getGithubUrl()).
        authorName(project.getUser().getNickname()).
        comments(
            project.getComments() != null ? project.getComments().stream().map(Comment::of).toList()
                : null).
        commentsCount(project.getCommentCounts()).
        likeCount(project.getLikeCounts()).
        build();
    projectDocumentsRepository.save(newProjectDocuments);
  }
}

