package com.project.hub.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.project.hub.entity.Comments;
import com.project.hub.entity.Projects;
import com.project.hub.entity.User;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.model.documents.ProjectDocuments;
import com.project.hub.model.mapper.ProjectDetail;
import com.project.hub.model.type.SearchType;
import com.project.hub.repository.document.ProjectDocumentsRepository;
import com.project.hub.repository.jpa.ProjectRepository;
import java.util.List;
import java.util.Optional;
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
    Page<ProjectDocuments> projectDocumentsList = new PageImpl<>(projects, pageable,
        projects.size());

    // when
    when(projectDocumentsRepository.findAll(pageable)).thenReturn(projectDocumentsList);
    // then
    assertEquals(
        projectDocumentsList,
        userSearchService.findAllInElasticSearch(page, size, sort)
    );
  }

  @DisplayName("title로 검색하기 실패 - 존재하지 않는 프로젝트")
  @Test
  void failSearchProjectByTitle() {
    // given
    String keyword = "keyword";

    // when
    when(projectDocumentsRepository.findByTitle(keyword)).thenReturn(Optional.empty());
    // then
    assertThrows(
        NotFoundException.class,
        () -> userSearchService.searchProjectByTitle(keyword)
    );
  }

  @DisplayName("title로 검색하기")
  @Test
  void searchProjectByTitle() {
    // given
    String keyword = "keyword";

    ProjectDocuments projectDocuments = ProjectDocuments.builder().id(1L).title("keyword").build();

    // when
    when(projectDocumentsRepository.findByTitle(keyword)).thenReturn(
        Optional.ofNullable(projectDocuments));
    // then
    assertEquals(
        projectDocuments,
        userSearchService.searchProjectByTitle(keyword)
    );
  }

  @DisplayName("title로 검색하기 - RDB")
  @Test
  void searchProjectByTitleInRDB() {
    // given
    String keyword = "keyword";

    User test = User.builder()
        .id(1L)
        .nickname("testNickname")
        .build();

    List<Comments> comments = List.of(
        Comments.builder()
            .id(1L)
            .user(test)
            .contents("testContents")
            .likes(1L)
            .build()
    );
    Projects projects = Projects.builder()
        .id(1L)
        .title("keyword")
        .subject("testSubject")
        .user(test)
        .comments(comments)
        .build();

    // when
    when(projectRepository.findByTitle(keyword)).thenReturn(
        Optional.ofNullable(projects));
    // then
    assertEquals(
        "testSubject",
        userSearchService.searchProjectByTitleInRDB(keyword).getSubject()
    );
  }

  @DisplayName("title로 검색하기 실패  - RDB")
  @Test
  void failSearchProjectByTitleInRDB() {
    String keyword = "keyword";

    // when
    when(projectRepository.findByTitle(keyword)).thenReturn(
        Optional.empty());
    // then
    assertThrows(
        NotFoundException.class,
        () -> userSearchService.searchProjectByTitleInRDB(keyword)
    );
  }

  @DisplayName("title로 검색하기 - RDB like")
  @Test
  void searchProjectByTitleInRDBLike() {
    //given
    String keyword = "keyword";

    User test = User.builder()
        .id(1L)
        .nickname("testNickname")
        .build();

    List<Comments> comments = List.of(
        Comments.builder()
            .id(1L)
            .user(test)
            .contents("testContents")
            .likes(1L)
            .build()
    );
    Projects projects = Projects.builder()
        .id(1L)
        .title("keyword")
        .subject("testSubject")
        .user(test)
        .comments(comments)
        .build();
    //when
    when(projectRepository.findAllByTitleLike("%" + keyword + "%")).thenReturn(
        List.of(
            projects
        )
    );

    //then
    assertEquals(
        "testSubject",
        userSearchService.searchProjectByTitleInRDBLike(keyword).get(0).getSubject()
    );
  }
}