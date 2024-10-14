package com.project.hub.service;

import com.project.hub.model.dto.request.projects.MyProjectListRequest;
import com.project.hub.model.dto.request.projects.ProjectCreateRequest;
import com.project.hub.model.dto.request.projects.ProjectDeleteRequest;
import com.project.hub.model.dto.request.projects.ProjectListRequest;
import com.project.hub.model.dto.request.projects.ProjectRequest;
import com.project.hub.model.dto.request.projects.ProjectUpdateRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.dto.response.projects.ListShortProjectDetail;
import com.project.hub.model.dto.response.projects.ProjectDetailResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface ProjectService {

  // 전체 조회(각 프로젝트별 title, subject)
  ListShortProjectDetail listProjects(ProjectListRequest request);

  // 단건 조회 (프로젝트별 상세정보)
  ProjectDetailResponse getProjectDetail(ProjectRequest request);

  // 내 프로젝트 전체 조회
  ListShortProjectDetail getMyProjectDetail(HttpServletRequest request,
      MyProjectListRequest myProjectListRequest);

  // 등록
  ResultResponse createProject(ProjectCreateRequest request)
      throws IOException, NoSuchAlgorithmException;

  // 수정
  ResultResponse updateProject(HttpServletRequest request,
      ProjectUpdateRequest projectUpdateRequest)
      throws IOException, NoSuchAlgorithmException;

  // 삭제
  ResultResponse deleteProject(HttpServletRequest request,
      ProjectDeleteRequest projectDeleteRequest);
}
