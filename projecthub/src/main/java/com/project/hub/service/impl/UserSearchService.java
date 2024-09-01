package com.project.hub.service.impl;

import com.project.hub.entity.Projects;
import com.project.hub.exceptions.ExceptionCode;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.model.documents.ProjectDocuments;
import com.project.hub.model.mapper.ProjectDetail;
import com.project.hub.repository.document.ProjectDocumentsRepository;
import com.project.hub.repository.jpa.ProjectRepository;
import com.project.hub.service.SearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSearchService implements SearchService {

  private final ProjectDocumentsRepository projectDocumentsRepository;
  private final ProjectRepository projectRepository;

  @Override
  public ProjectDocuments searchProjectByTitle(String keyword) {
    return projectDocumentsRepository.findByTitle(keyword).orElseThrow(
        () -> new NotFoundException(ExceptionCode.PROJECT_NOT_FOUND)
    );
  }

  @Override
  public List<ProjectDocuments> searchProjectByTitleLike(String keyword) {
    return projectDocumentsRepository.findByTitleLike(keyword);
  }

  @Override
  public ProjectDetail searchProjectByTitleInRDB(String keyword) {
    return projectRepository.findByTitle(keyword).map(ProjectDetail::new).orElseThrow(
        () -> new NotFoundException(ExceptionCode.PROJECT_NOT_FOUND)
    );
  }

  @Override
  public List<ProjectDetail> searchProjectByTitleInRDBLike(String keyword) {
    List<Projects> projects = projectRepository.findAllByTitleLike("%"+keyword+"%");
    return projects.stream().map(ProjectDetail::new).toList();
  }

  @Override
  public Iterable<ProjectDocuments> findAllInElasticSearch() {
    return projectDocumentsRepository.findAll();
  }
}
