package com.project.hub.service.impl;

import com.project.hub.entity.Projects;
import com.project.hub.entity.User;
import com.project.hub.exception.exception.ProjectNotFoundException;
import com.project.hub.exception.exception.UnmatchedUserException;
import com.project.hub.exception.exception.UserNotFoundException;
import com.project.hub.model.dto.request.projects.MyProjectListRequest;
import com.project.hub.model.dto.request.projects.ProjectCreateRequest;
import com.project.hub.model.dto.request.projects.ProjectDeleteRequest;
import com.project.hub.model.dto.request.projects.ProjectListRequest;
import com.project.hub.model.dto.request.projects.ProjectRequest;
import com.project.hub.model.dto.request.projects.ProjectUpdateRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.dto.response.projects.ListProjectResponse;
import com.project.hub.model.dto.response.projects.ProjectDetailResponse;
import com.project.hub.model.mapper.ProjectDetail;
import com.project.hub.model.mapper.ShortProjectDetail;
import com.project.hub.model.type.PictureType;
import com.project.hub.model.type.ResultCode;
import com.project.hub.model.type.Sorts;
import com.project.hub.repository.ProjectRepository;
import com.project.hub.repository.UserRepository;
import com.project.hub.service.ProjectService;
import com.project.hub.util.PictureManager;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProjectService implements ProjectService {

  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final PictureManager pictureManager;

  @Override
  public ListProjectResponse listProjects(ProjectListRequest request) {

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

    Sorts sort = request.getSort();

    // 추후 개발을 위해 우선 선언
    Page<Projects> sortedProjects;

    if (sort == Sorts.LATEST) {
      sortedProjects = projectRepository.findAllByDeletedAtIsNullOrderByRegisteredAtDesc(
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
  public ProjectDetailResponse getProjectDetail(ProjectRequest request) {

    Projects project = projectRepository.findByIdAndDeletedAtIsNull(request.getId()).orElseThrow(
        ProjectNotFoundException::new
    );

    return new ProjectDetailResponse(new ProjectDetail(project));
  }

  @Override
  public ListProjectResponse getMyProjectDetail(MyProjectListRequest request) {
    Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

    Sorts sort = request.getSort();

    // 추후 개발을 위해 우선 선언
    Page<Projects> sortedProjects;

    if (sort == Sorts.LATEST) {
      sortedProjects = projectRepository.findAllByUserIdAndDeletedAtIsNullOrderByRegisteredAtDesc(
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
      throws IOException, NoSuchAlgorithmException {

    Long userId = request.getUserId();

    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    String uploadedSystemArchitectureUrl = pictureManager.upload(
        userId,
        request.getTitle(),
        request.getSystemArchitecturePicture(),
        PictureType.SYSTEM_ARCHITECTURE
    );

    String hashSystemArchitecture = pictureManager.calculateSHA256Base64(
        request.getSystemArchitecturePicture());

    String uploadedErdUrl = pictureManager.upload(
        userId,
        request.getTitle(),
        request.getErdPicture(),
        PictureType.ERD
    );

    String hashErd = pictureManager.calculateSHA256Base64(request.getErdPicture());

    Projects project = Projects.builder()
        .title(request.getTitle())
        .subject(request.getSubject())
        .feature(request.getFeature())
        .contents(request.getContents())
        .skills(request.getSkills())
        .tools(request.getTools())
        .systemArchitectureUrl(uploadedSystemArchitectureUrl)
        .hashSystemArchitecture(hashSystemArchitecture)
        .erdUrl(uploadedErdUrl)
        .hashErd(hashErd)
        .githubUrl(request.getGithubUrl())
        .user(user)
        .build();

    projectRepository.save(project);

    return ResultResponse.of(ResultCode.PROJECT_CREATE_SUCCESS);
  }

  @Override
  @Transactional
  public ResultResponse updateProject(ProjectUpdateRequest request)
      throws IOException, NoSuchAlgorithmException {

    Long userId = request.getUserId();

    // 존재 여부만 파악
    User user = userRepository.findById(userId).orElseThrow(
        UserNotFoundException::new
    );

    Long projectId = request.getProjectId();

    Projects project = projectRepository.findByIdAndDeletedAtIsNull(projectId).orElseThrow(
        ProjectNotFoundException::new
    );

    // 사용자가 등록한 프로젝트가 아닌 경우
    if (!project.getUser().getId().equals(userId)) {
      throw new UnmatchedUserException();
    }

    // 사진의 hash 값 비교로 변경 여부 확인
    if (pictureManager.diff(project.getHashSystemArchitecture(),
        request.getSystemArchitecturePicture())) {
      String uploadedSystemArchitectureUrl = pictureManager.upload(
          userId,
          request.getTitle(),
          request.getSystemArchitecturePicture(),
          PictureType.SYSTEM_ARCHITECTURE
      );

      project.updateSystemArchitecture(
          uploadedSystemArchitectureUrl,
          pictureManager.calculateSHA256Base64(request.getSystemArchitecturePicture())
      );
    }

    if (pictureManager.diff(project.getHashErd(), request.getErdPicture())) {
      String uploadedErdUrl = pictureManager.upload(
          userId,
          request.getTitle(),
          request.getErdPicture(),
          PictureType.ERD
      );

      project.updateErd(
          uploadedErdUrl,
          pictureManager.calculateSHA256Base64(request.getErdPicture())
      );
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

    // 존재 여부만 파악
    User user = userRepository.findById(userId).orElseThrow(
        UserNotFoundException::new
    );

    Long projectId = request.getProjectId();

    Projects project = projectRepository.findByIdAndDeletedAtIsNull(projectId).orElseThrow(
        ProjectNotFoundException::new
    );

    // 사용자가 등록한 프로젝트가 아닌 경우
    if (!project.getUser().getId().equals(userId)) {
      throw new UnmatchedUserException();
    }

    // 삭제된 프로젝트인 경우 처리 고려 중
    // 1. 댓글 및 좋아요 기능 CascadeType.ALL로 처리하여 자동 삭제
    // 2. 삭제된 프로젝트 하위 댓글들 모두 soft delete

    // soft delete
    project.delete();

    return ResultResponse.of(ResultCode.PROJECT_DELETE_SUCCESS);
  }
}
