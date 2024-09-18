package com.project.hub.controller;

import com.project.hub.model.documents.ProjectDocuments;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.mapper.ProjectDetail;
import com.project.hub.model.type.ResultCode;
import com.project.hub.service.impl.UserSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 검색 컨트롤러
 * Elasticsearch와 RDB 비교
 * 32.1% 감소
 */
@Tag(name = "UserSearchController", description = "검색 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class UserSearchController {

  private final UserSearchService userSearchService;

  @Operation(
      summary = "프로젝트 제목으로 검색",
      description = "프로젝트 제목으로 검색합니다."
  )
  @GetMapping("/es")
  public ResponseEntity<ResultResponse> searchProjectByTitle(String keyword) {
    long startTime = System.currentTimeMillis();
    ProjectDocuments projectDocuments = userSearchService.searchProjectByTitle(keyword);
    long endTime = System.currentTimeMillis();
    log.info("searchProjectByTitle elapsed time: {}", endTime - startTime);
    return ResponseEntity.ok(ResultResponse.of(ResultCode.SEARCH_SUCCESS,projectDocuments));
  }

  @Operation(
      summary = "프로젝트 제목으로 검색 (Like)",
      description = "프로젝트 제목으로 검색합니다."
  )
  @GetMapping("/es/like")
  public ResponseEntity<ResultResponse> searchProjectByTitleLike(String keyword) {
    long startTime = System.currentTimeMillis();
    List<ProjectDocuments> projectDocuments = userSearchService.searchProjectByTitleLike(keyword);
    long endTime = System.currentTimeMillis();
    log.info("searchProjectByTitleLike elapsed time: {}", endTime - startTime);
    return ResponseEntity.ok(ResultResponse.of(ResultCode.SEARCH_SUCCESS,projectDocuments));
  }

  @Operation(
      summary = "프로젝트 제목으로 검색 (RDB)",
      description = "프로젝트 제목으로 검색합니다."
  )
  @GetMapping("/rdb")
  public ResponseEntity<ResultResponse> searchProjectByTitleInRDB(String keyword) {
    long startTime = System.currentTimeMillis();
    ProjectDetail projectDetail = userSearchService.searchProjectByTitleInRDB(keyword);
    long endTime = System.currentTimeMillis();
    log.info("searchProjectByTitleInRDB elapsed time: {}", endTime - startTime);
    return ResponseEntity.ok(ResultResponse.of(ResultCode.SEARCH_SUCCESS,projectDetail));
  }

  @Operation(
      summary = "프로젝트 제목으로 검색 (RDB Like)",
      description = "프로젝트 제목으로 검색합니다."
  )
  @GetMapping("/rdb/like")
  public ResponseEntity<ResultResponse> searchProjectByTitleInRDBLike(String keyword) {
    long startTime = System.currentTimeMillis();
    List<ProjectDetail> projectDetail = userSearchService.searchProjectByTitleInRDBLike(keyword);
    long endTime = System.currentTimeMillis();
    log.info("searchProjectByTitleInRDBLike elapsed time: {}", endTime - startTime);
    return ResponseEntity.ok(ResultResponse.of(ResultCode.SEARCH_SUCCESS,projectDetail));
  }

  @Operation(
      summary = "모든 데이터 조회 (Elasticsearch)",
      description = "모든 데이터를 조회합니다."
  )
  @GetMapping("/es/all")
  public ResponseEntity<ResultResponse> findAllInElasticSearch() {
    long startTime = System.currentTimeMillis();
    Iterable<ProjectDocuments> projectDocuments = userSearchService.findAllInElasticSearch();
    long endTime = System.currentTimeMillis();
    log.info("findAllInElasticSearch elapsed time: {}", endTime - startTime);
    return ResponseEntity.ok(ResultResponse.of(ResultCode.SEARCH_SUCCESS,projectDocuments));
  }
}
