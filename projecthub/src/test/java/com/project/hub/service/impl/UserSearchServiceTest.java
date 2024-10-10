package com.project.hub.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.hub.entity.Comments;
import com.project.hub.entity.Projects;
import com.project.hub.entity.User;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.model.documents.ProjectDocuments;
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
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

@ExtendWith(MockitoExtension.class)
class UserSearchServiceTest {

  @Mock
  private ProjectDocumentsRepository projectDocumentsRepository;

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private ElasticsearchOperations elasticsearchOperations;

  @InjectMocks
  private UserSearchService userSearchService;

  @Test
  @DisplayName("ES 프로젝트 내용으로 검색")
  void searchProjectByTitleLike() {

    // given
    String keyword = "keyword";
    int page = 0;
    int size = 10;
    SearchType sort = SearchType.LATEST;
    Sort sortOption = Sort.by(Direction.DESC, sort.getSort());
    Pageable pageable = PageRequest.of(page, size, sortOption);

    ProjectDocuments doc1 = ProjectDocuments.builder()
        .id(1L)
        .title("2 keyword 1")
        .subject("subject 1")
        .feature("feature 1")
        .contents("이 내용은 2012년부터 시작해서... springboot를 통해서...~~~~")
        .skills(List.of("Java", "Spring"))
        .tools(List.of("Maven", "Git"))
        .systemArchitecture("Microservice")
        .erd("ERD Link 1")
        .githubLink("github.com/project1")
        .authorName("Author 1")
        .comments(List.of()) // 빈 댓글 리스트
        .commentsCount(0L)
        .likeCount(10L)
        .deletedAt(null) // 필수 필드 설정
        .build();

    ProjectDocuments doc2 = ProjectDocuments.builder()
        .id(1L)
        .title("4 keyword 2")
        .subject("subject 1")
        .feature("feature 1")
        .contents("이 내용은 2012년부터 시작해서... DJango를 통해서...~~~~")
        .skills(List.of("Python", "Django"))
        .tools(List.of("PiPy", "Git"))
        .systemArchitecture("Microservice")
        .erd("ERD Link 1")
        .githubLink("github.com/project1")
        .authorName("Author 1")
        .comments(List.of()) // 빈 댓글 리스트
        .commentsCount(0L)
        .likeCount(10L)
        .deletedAt(null) // 필수 필드 설정
        .build();

    SearchHit<ProjectDocuments> searchHit1 = mock(SearchHit.class);
    SearchHit<ProjectDocuments> searchHit2 = mock(SearchHit.class);
    when(searchHit1.getContent()).thenReturn(doc1);
    when(searchHit2.getContent()).thenReturn(doc2);

    List<SearchHit<ProjectDocuments>> searchHitsList = List.of(searchHit1, searchHit2);

    Criteria criteria = new Criteria("contents").matches(keyword);
    CriteriaQuery query = new CriteriaQuery(criteria).setPageable(pageable);

    SearchHits<ProjectDocuments> mockSearchHits = mock(SearchHits.class);
    when(mockSearchHits.getSearchHits()).thenReturn(searchHitsList);

    // ElasticsearchOperations의 search() 메서드가 mockSearchHits를 반환하도록 설정
    when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(ProjectDocuments.class)))
        .thenReturn(mockSearchHits);

    // when
    List<ProjectDocuments> result = userSearchService.searchProjectByTitleLike(keyword, page, size,
        sort);

    // then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("2 keyword 1", result.get(0).getTitle());
    assertEquals("4 keyword 2", result.get(1).getTitle());

    // verify: ElasticsearchOperations의 search() 메서드가 호출되었는지 확인
    verify(elasticsearchOperations, times(1)).search(any(CriteriaQuery.class),
        eq(ProjectDocuments.class));
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