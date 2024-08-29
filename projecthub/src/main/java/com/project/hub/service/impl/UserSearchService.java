package com.project.hub.service.impl;

import com.project.hub.exceptions.ExceptionCode;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.model.documents.ProjectDocuments;
import com.project.hub.repository.document.ProjectDocumentsRepository;
import com.project.hub.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSearchService implements SearchService {

  private final ProjectDocumentsRepository projectDocumentsRepository;

  @Override
  public ProjectDocuments searchProjectByTitle(String keyword) {

    return projectDocumentsRepository.findByTitle(keyword).orElseThrow(
        () -> new NotFoundException(ExceptionCode.PROJECT_NOT_FOUND)
    );
  }

  @Override
  public Iterable<ProjectDocuments> findAllInElasticSearch() {
    return projectDocumentsRepository.findAll();
  }
}
