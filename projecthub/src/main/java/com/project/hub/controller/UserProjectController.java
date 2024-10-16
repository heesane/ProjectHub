package com.project.hub.controller;

import static com.project.hub.model.type.ResultCode.PROJECT_LIST_SUCCESS;

import com.project.hub.model.dto.request.projects.MyProjectListRequest;
import com.project.hub.model.dto.request.projects.ProjectCreateRequest;
import com.project.hub.model.dto.request.projects.ProjectDeleteRequest;
import com.project.hub.model.dto.request.projects.ProjectListRequest;
import com.project.hub.model.dto.request.projects.ProjectRequest;
import com.project.hub.model.dto.request.projects.ProjectUpdateRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.type.ResultCode;
import com.project.hub.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "UserProjectController", description = "프로젝트 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
@RestController
public class UserProjectController {

  private final ProjectService projectService;

  @GetMapping(value = "/list")
  @Operation(
      summary = "Get Projects",
      description = "프로젝트 리스트 조회"
  )
  public ResponseEntity<ResultResponse> getProjects(
      @ModelAttribute ProjectListRequest projectListRequest) {
    return ResponseEntity.ok(
        ResultResponse.of(PROJECT_LIST_SUCCESS,
            projectService.listProjects(projectListRequest)));
  }

  @GetMapping(value = "/{projectId}")
  @Operation(
      summary = "Get Project Detail",
      description = "프로젝트 상세 조회"
  )
  public ResponseEntity<ResultResponse> getProjectDetail(
      @PathVariable("projectId") Long projectId) {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_DETAIL_SUCCESS,
        projectService.getProjectDetail(ProjectRequest.of(projectId))));
  }

  @GetMapping(value = "/myproject/list")
  @Operation(
      summary = "Get My Projects",
      description = "내 프로젝트 리스트 조회"
  )
  public ResponseEntity<ResultResponse> getMyProjects(HttpServletRequest request,
      @ModelAttribute @Valid MyProjectListRequest myProjectListRequest) {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_LIST_SUCCESS,
        projectService.getMyProjectDetail(request, myProjectListRequest)));
  }

  @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      summary = "Create Project",
      description = "프로젝트 생성"
  )
  public ResponseEntity<ResultResponse> createProject(
      @ModelAttribute @Valid ProjectCreateRequest request)
      throws IOException, NoSuchAlgorithmException {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_CREATE_SUCCESS,
        projectService.createProject(request)));
  }

  @PatchMapping(value = "/update", consumes = {"multipart/form-data"})
  @Operation(
      summary = "Update Project",
      description = "프로젝트 수정"
  )
  public ResponseEntity<ResultResponse> updateProject(
      HttpServletRequest request,
      @ModelAttribute @Valid ProjectUpdateRequest projectUpdateRequest)
      throws IOException, NoSuchAlgorithmException {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_UPDATE_SUCCESS,
        projectService.updateProject(request, projectUpdateRequest)));
  }

  @DeleteMapping(value = "/delete", consumes = {"application/json"})
  @Operation(
      summary = "Delete Project",
      description = "프로젝트 삭제"
  )
  public ResponseEntity<ResultResponse> deleteProject(
      HttpServletRequest request,
      @RequestBody @Valid ProjectDeleteRequest projectDeleteRequest) {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_DELETE_SUCCESS,
        projectService.deleteProject(request, projectDeleteRequest)));
  }
}


