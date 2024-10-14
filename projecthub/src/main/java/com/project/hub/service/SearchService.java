package com.project.hub.service;

import com.project.hub.model.documents.ProjectDocuments;
import com.project.hub.model.mapper.ProjectDetail;
import com.project.hub.model.type.SearchType;
import java.util.List;

public interface SearchService {

  List<ProjectDocuments> searchProjectByTitleLike(String keyword, int page, int size,
      SearchType sort);

  ProjectDocuments searchProjectByTitle(String keyword);

  List<ProjectDetail> searchProjectByTitleInRDBLike(String keyword);

  ProjectDetail searchProjectByTitleInRDB(String keyword);

  Iterable<ProjectDocuments> findAllInElasticSearch(int page, int size, SearchType searchType);
}
