package com.project.hub.service;

import com.project.hub.model.documents.ProjectDocuments;
import com.project.hub.model.mapper.ProjectDetail;
import java.util.List;

public interface SearchService {

  List<ProjectDocuments> searchProjectByTitleLike(String keyword);

  ProjectDocuments searchProjectByTitle(String keyword);

  List<ProjectDetail> searchProjectByTitleInRDBLike(String keyword);

  ProjectDetail searchProjectByTitleInRDB(String keyword);

  Object findAllInElasticSearch();
}
