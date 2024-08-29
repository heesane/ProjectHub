package com.project.hub.service;

import com.project.hub.model.documents.ProjectDocuments;

public interface SearchService {
  ProjectDocuments searchProjectByTitle(String keyword);

  Object findAllInElasticSearch();
}
