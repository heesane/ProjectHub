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
import com.project.hub.service.impl.UserProjectService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
public class UserProjectController {

  private final UserProjectService userProjectService;

  @GetMapping("/list")
  public ResponseEntity<ResultResponse> getProjects(@RequestParam ProjectListRequest request) {
    return ResponseEntity.ok(
        ResultResponse.of(PROJECT_LIST_SUCCESS, userProjectService.listProjects(request)));
  }

  @GetMapping("/detail")
  public ResponseEntity<ResultResponse> getProjectDetail(
      @RequestParam @Valid ProjectRequest request) {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_DETAIL_SUCCESS,
        userProjectService.getProjectDetail(request)));
  }

  @GetMapping("/myproject/list")
  public ResponseEntity<ResultResponse> getMyProjectDetail(
      @RequestParam @Valid MyProjectListRequest request) {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_LIST_SUCCESS,
        userProjectService.getMyProjectDetail(request)));
  }

  @PostMapping("/create")
  public ResponseEntity<ResultResponse> createProject(
      @RequestBody @Valid ProjectCreateRequest request)
      throws IOException, NoSuchAlgorithmException {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_CREATE_SUCCESS,
        userProjectService.createProject(request)));
  }

  @PatchMapping("/update")
  public ResponseEntity<ResultResponse> updateProject(
      @RequestBody @Valid ProjectUpdateRequest request)
      throws IOException, NoSuchAlgorithmException {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_UPDATE_SUCCESS,
        userProjectService.updateProject(request)));
  }

  @DeleteMapping("/delete")
  public ResponseEntity<ResultResponse> deleteProject(
      @RequestBody @Valid ProjectDeleteRequest request) {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_DELETE_SUCCESS,
        userProjectService.deleteProject(request)));
  }
}
