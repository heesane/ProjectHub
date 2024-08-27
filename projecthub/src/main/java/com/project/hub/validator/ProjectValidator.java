package com.project.hub.validator;

import com.project.hub.entity.Projects;
import com.project.hub.exceptions.ExceptionCode;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {

  private final ProjectRepository projectRepository;

  public Projects validateAndGetProject(Long projectId) {
    // 프로젝트 정보 검증
    return projectRepository.findById(projectId).orElseThrow(
        () -> new NotFoundException(ExceptionCode.PROJECT_NOT_FOUND));
  }

  public void isProjectExist(Long projectId) {
    if (!projectRepository.existsById(projectId)) {
      throw new NotFoundException(ExceptionCode.PROJECT_NOT_FOUND);
    }
  }
}
