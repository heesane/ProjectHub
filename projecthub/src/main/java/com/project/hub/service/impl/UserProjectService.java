package com.project.hub.service.impl;

import com.project.hub.aop.badge.BadgeCheck;
import com.project.hub.auth.service.TokenService;
import com.project.hub.entity.Projects;
import com.project.hub.entity.User;
import com.project.hub.exceptions.ExceptionCode;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.exceptions.exception.TokenNotExistsException;
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
import com.project.hub.model.dto.response.projects.ListShortProjectDetail;
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
import com.project.hub.validator.Validator;
import jakarta.servlet.http.HttpServletRequest;
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
  private final Validator validator;
  private final TokenService tokenService;

  @Override
  public ListShortProjectDetail listProjects(ProjectListRequest request) {

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

    Sorts sort = request.getSort();

    if (sort == Sorts.LATEST) {
      pageable.getSort().and(Sort.by(Sort.Direction.DESC, "registeredAt"));
      Page<Projects> sortedProjects = projectRepository.findAll(pageable);

      List<ShortProjectDetail> collect = sortedProjects.stream().map(ShortProjectDetail::new)
          .collect(Collectors.toList());

      return new ListShortProjectDetail(collect);

    }
    else if (sort == Sorts.LIKE) {
      pageable.getSort().and(Sort.by(Sort.Direction.ASC, "like_counts"));
      Page<Projects> sortedProjects = projectRepository.findAll(pageable);

      List<ShortProjectDetail> collect = sortedProjects.stream().map(ShortProjectDetail::new)
          .collect(Collectors.toList());

      return new ListShortProjectDetail(collect);
    }
    else if (sort == Sorts.COMMENTS) {
      pageable.getSort().and(Sort.by(Sort.Direction.DESC, "comment_counts"));
      Page<Projects> sortedProjects = projectRepository.findAllOrderByCommentsCountDesc(pageable);

      List<ShortProjectDetail> collect = sortedProjects.stream().map(ShortProjectDetail::new)
          .collect(Collectors.toList());

      return new ListShortProjectDetail(collect);
    }
    return null;
  }

  @Override
  public ProjectDetailResponse getProjectDetail(ProjectRequest request) {

    Projects project = validator.validateAndGetProject(request.getId());

    return new ProjectDetailResponse(new ProjectDetail(project));
  }

  @Override
  public ListShortProjectDetail getMyProjectDetail(HttpServletRequest request,
      MyProjectListRequest myProjectListRequest) {

    String userEmail = tokenService.extractEmail(request).orElseThrow(TokenNotExistsException::new);

    User user = validator.validateAndGetUser(myProjectListRequest.getUserId());

    if (!userEmail.equals(user.getEmail())) {
      throw new UnmatchedUserException();
    }

    Sorts sort = myProjectListRequest.getSort();

    if (sort == Sorts.LATEST) {
      Pageable pageable = PageRequest.of(myProjectListRequest.getPage(), myProjectListRequest.getSize(),
          Sort.by(Sort.Direction.DESC, "registeredAt"));
      Page<Projects> sortedProjects = projectRepository.findAllByUserId(
          myProjectListRequest.getUserId(),
          pageable);

      List<ShortProjectDetail> collect = sortedProjects.stream().map(ShortProjectDetail::new)
          .collect(Collectors.toList());

      return new ListShortProjectDetail(collect);

    } else if (sort == Sorts.LIKE) {
      Pageable pageable = PageRequest.of(myProjectListRequest.getPage(), myProjectListRequest.getSize(),
          Sort.by(Sort.Direction.DESC, "likeCounts"));
      Page<Projects> sortedProjects = projectRepository.findAllByUserId(myProjectListRequest.getUserId(), pageable);

      List<ShortProjectDetail> collect = sortedProjects.stream().map(ShortProjectDetail::new)
          .collect(Collectors.toList());

      return new ListShortProjectDetail(collect);
    } else if (sort == Sorts.COMMENTS) {
      Pageable pageable = PageRequest.of(myProjectListRequest.getPage(), myProjectListRequest.getSize(),
          Sort.by(Sort.Direction.DESC, "comments"));
      Page<Projects> sortedProjects = projectRepository.findAllByUserId(
          myProjectListRequest.getUserId(),
          pageable);

      List<ShortProjectDetail> collect = sortedProjects.stream().map(ShortProjectDetail::new)
          .collect(Collectors.toList());

      return new ListShortProjectDetail(collect);
    }
    return null;
  }

  @BadgeCheck
  @Override
  @Transactional
  public ResultResponse createProject(ProjectCreateRequest request)
      throws IOException {

    Long userId = request.getUserId();

    User user = validator.validateAndGetUser(userId);

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
        .visible(request.isVisible())
        .build();

    projectRepository.save(project);

    // Elasticsearch에 저장
    createProjectElasticsearch(project);

    return ResultResponse.of(ResultCode.PROJECT_CREATE_SUCCESS);
  }

  @Override
  @Transactional
  public ResultResponse updateProject(HttpServletRequest request,
      ProjectUpdateRequest projectUpdateRequest)
      throws IOException {

    String userEmail = tokenService.extractEmail(request).orElseThrow(TokenNotExistsException::new);

    Projects projects = projectRepository.findById(projectUpdateRequest.getProjectId()).orElseThrow(
        () -> new NotFoundException(ExceptionCode.PROJECT_NOT_FOUND)
    );

    User user = validator.validateAndGetUser(projectUpdateRequest.getUserId());

    if (!userEmail.equals(user.getEmail())) {
      throw new UnmatchedUserException();
    }

    Long userId = user.getId();

    validator.isUserExist(userId);

    Long projectId = projectUpdateRequest.getProjectId();

    log.info("projectId : {}", projectId);

    // 사용자가 등록한 프로젝트가 아닌 경우
    if (!projects.getUser().getId().equals(userId)) {
      throw new UnmatchedUserException();
    }

    // 업데이트 요청 시 이미지가 있는 경우만 변경
    if (projectUpdateRequest.getSystemArchitecturePicture() != null
        && !projectUpdateRequest.getSystemArchitecturePicture()
        .isEmpty()) {
      String uploadedSystemArchitectureUrl = pictureManager.upload(
          userId,
          projectUpdateRequest.getTitle(),
          projectUpdateRequest.getSystemArchitecturePicture(),
          PictureType.SYSTEM_ARCHITECTURE
      );
      projects.updateSystemArchitecture(uploadedSystemArchitectureUrl);
    }

    if (projectUpdateRequest.getErdPicture() != null) {
      String uploadedErdUrl = pictureManager.upload(
          userId,
          projectUpdateRequest.getTitle(),
          projectUpdateRequest.getErdPicture(),
          PictureType.ERD
      );
      projects.updateErd(uploadedErdUrl);
    }

    projects.update(projectUpdateRequest);

    updateProjectsElasticsearch(projects);

    return ResultResponse.of(ResultCode.PROJECT_UPDATE_SUCCESS);
  }

  @Override
  @Transactional
  public ResultResponse deleteProject(
      HttpServletRequest request,
      ProjectDeleteRequest projectDeleteRequest) {

    String userEmail = tokenService.extractEmail(request).orElseThrow(TokenNotExistsException::new);

    User user = validator.validateAndGetUser(projectDeleteRequest.getUserId());

    if (!userEmail.equals(user.getEmail())) {
      throw new UnmatchedUserException();
    }

    Long userId = user.getId();

    Long projectId = projectDeleteRequest.getProjectId();

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

    projectRepository.delete(project);

    projectDocumentsRepository.deleteById(projectId);

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
        commentsCount(project.getComments() != null ? (long) project.getComments().size() : 0L).
        likeCount(project.getLikeCounts()).
        build();
    projectDocumentsRepository.save(newProjectDocuments);
  }

  @Transactional
  public void updateProjectsElasticsearch(Projects projects) {
    ProjectDocuments projectDocuments = projectDocumentsRepository.findById(projects.getId())
        .orElseThrow(
            () -> new NotFoundException(ExceptionCode.PROJECT_NOT_FOUND)
        );
    projectDocuments.update(projects);
    projectDocumentsRepository.save(projectDocuments);
  }
}

