package com.project.hub.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.project.hub.model.documents.ProjectDocuments;
import com.project.hub.model.type.SearchType;
import com.project.hub.repository.document.ProjectDocumentsRepository;
import com.project.hub.repository.jpa.ProjectRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class UserSearchServiceTest {

  @Mock
  private ProjectDocumentsRepository projectDocumentsRepository;

  @Mock
  private ProjectRepository projectRepository;

  @InjectMocks
  private UserSearchService userSearchService;

  @Test
  @DisplayName("ES 프로젝트 제목으로 검색")
  void searchProjectByTitleLike() {

    // given
    String keyword = "keyword";
    int page = 0;
    int size = 10;
    SearchType sort = SearchType.LATEST;
    Sort sortOption = Sort.by(Direction.DESC, sort.getSort());
    Pageable pageable = PageRequest.of(page, size, sortOption);

    List<ProjectDocuments> projectDocumentsList =
        List.of(
            ProjectDocuments.builder().id(1L).title("2 keyword 1").build(),
            ProjectDocuments.builder().id(2L).title("4 keyword 3").build()
        );

    // when
    when(projectDocumentsRepository.findByTitleLike(keyword, pageable)).thenReturn(
        projectDocumentsList);

    // then
    assertEquals(
        projectDocumentsList,
        userSearchService.searchProjectByTitleLike(keyword, page, size, sort)
    );
  }

  @Test
  @DisplayName("ES 모든 데이터 조회")
  void findAllInElasticSearch() {

    // given
    int page = 0;
    int size = 10;
    SearchType sort = SearchType.LATEST;
    Sort sortOption = Sort.by(Direction.DESC, sort.getSort());
    Pageable pageable = PageRequest.of(page, size, sortOption);

    List<ProjectDocuments> projects = List.of(
        ProjectDocuments.builder().id(1L).title("2 keyword 1").build(),
        ProjectDocuments.builder().id(2L).title("4 keyword 3").build()
    );
    Page<ProjectDocuments> projectDocumentsList = new PageImpl<>(projects, pageable, projects.size());

    // when
    when(projectDocumentsRepository.findAll(pageable)).thenReturn(projectDocumentsList);
    // then
    assertEquals(
        projectDocumentsList,
        userSearchService.findAllInElasticSearch(page, size, sort)
    );
  }
}