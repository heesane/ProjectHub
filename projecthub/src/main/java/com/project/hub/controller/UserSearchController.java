package com.project.hub.controller;

import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.type.ResultCode;
import com.project.hub.model.type.SearchType;
import com.project.hub.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 검색 컨트롤러 Elasticsearch와 RDB 비교 32.1% 감소
 */
@Tag(name = "UserSearchController", description = "검색 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class UserSearchController {

  private final SearchService userSearchService;

//  @Operation(
//      summary = "프로젝트 제목으로 검색",
//      description = "프로젝트 제목으로 검색합니다."
//  )
//  @GetMapping("/es")
//  public ResponseEntity<ResultResponse> searchProjectByTitle(String keyword) {
//    ProjectDocuments projectDocuments = userSearchService.searchProjectByTitle(keyword);
//    return ResponseEntity.ok(ResultResponse.of(ResultCode.SEARCH_SUCCESS,projectDocuments));
//  }

  @Operation(
      summary = "프로젝트 전문 검색 (created, like, commentCount)",
      description = "프로젝트의 내용으로 검색합니다."
  )
  @GetMapping("")
  public ResponseEntity<ResultResponse> searchProjectByTitleLike(
      @RequestParam(value = "keyword") String keyword,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam(value = "sort", defaultValue = "id") SearchType sort
  ) {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.SEARCH_SUCCESS,
        userSearchService.searchProjectByTitleLike(keyword, page, size, sort)));
  }

  @Operation(
      summary = "모든 데이터 조회 (created, like, commentCount)",
      description = "모든 데이터를 조회합니다."
  )
  @GetMapping("/all")
  public ResponseEntity<ResultResponse> findAllInElasticSearch(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam(value = "sort", defaultValue = "id") SearchType sort
  ) {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.SEARCH_SUCCESS,
        userSearchService.findAllInElasticSearch(page, size, sort)));
  }

//  @Operation(
//      summary = "프로젝트 제목으로 검색 (RDB)",
//      description = "프로젝트 제목으로 검색합니다."
//  )
//  @GetMapping("/rdb")
//  public ResponseEntity<ResultResponse> searchProjectByTitleInRDB(String keyword) {
//    ProjectDetail projectDetail = userSearchService.searchProjectByTitleInRDB(keyword);
//    return ResponseEntity.ok(ResultResponse.of(ResultCode.SEARCH_SUCCESS,projectDetail));
//  }
//
//  @Operation(
//      summary = "프로젝트 제목으로 검색 (RDB Like)",
//      description = "프로젝트 제목으로 검색합니다."
//  )
//  @GetMapping("/rdb/like")
//  public ResponseEntity<ResultResponse> searchProjectByTitleInRDBLike(String keyword) {
//    List<ProjectDetail> projectDetail = userSearchService.searchProjectByTitleInRDBLike(keyword);
//    return ResponseEntity.ok(ResultResponse.of(ResultCode.SEARCH_SUCCESS,projectDetail));
//  }


}
