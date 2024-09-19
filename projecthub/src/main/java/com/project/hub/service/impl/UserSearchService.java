package com.project.hub.service.impl;

import com.project.hub.entity.Projects;
import com.project.hub.exceptions.ExceptionCode;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.model.documents.ProjectDocuments;
import com.project.hub.model.mapper.ProjectDetail;
import com.project.hub.model.type.SearchType;
import com.project.hub.repository.document.ProjectDocumentsRepository;
import com.project.hub.repository.jpa.ProjectRepository;
import com.project.hub.service.SearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSearchService implements SearchService {

  private final ProjectDocumentsRepository projectDocumentsRepository;
  private final ProjectRepository projectRepository;

  @Cacheable(value = "project", key = "#keyword + '_' + #page + '_' + #size + '_' + #searchType")
  @Override
  public List<ProjectDocuments> searchProjectByTitleLike(
      String keyword,
      int page,
      int size,
      SearchType searchType) {
    Sort sortOption = Sort.by(Direction.DESC, searchType.getSort());
    Pageable pageable = PageRequest.of(page, size, sortOption);
    return projectDocumentsRepository.findByTitleLike(keyword,pageable);
  }

  @Cacheable(value = "allProject", key = "#page + '_' + #size + '_' + #searchType")
  @Override
  public Iterable<ProjectDocuments> findAllInElasticSearch(int page, int size, SearchType searchType) {
    Sort sortOption = Sort.by(Direction.DESC, searchType.getSort());
    Pageable Page = PageRequest.of(page, size, sortOption);

    return projectDocumentsRepository.findAll(Page);
  }

  @Override
  public ProjectDocuments searchProjectByTitle(String keyword) {
    return projectDocumentsRepository.findByTitle(keyword).orElseThrow(
        () -> new NotFoundException(ExceptionCode.PROJECT_NOT_FOUND)
    );
  }

  @Override
  public ProjectDetail searchProjectByTitleInRDB(String keyword) {
    return projectRepository.findByTitle(keyword).map(ProjectDetail::new).orElseThrow(
        () -> new NotFoundException(ExceptionCode.PROJECT_NOT_FOUND)
    );
  }

  @Override
  public List<ProjectDetail> searchProjectByTitleInRDBLike(String keyword) {
    List<Projects> projects = projectRepository.findAllByTitleLike("%" + keyword + "%");
    return projects.stream().map(ProjectDetail::new).toList();
  }


}
