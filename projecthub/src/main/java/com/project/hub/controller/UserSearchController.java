package com.project.hub.controller;

import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.type.ResultCode;
import com.project.hub.service.impl.UserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class UserSearchController {

  private final UserSearchService userSearchService;

  @GetMapping
  public ResponseEntity<ResultResponse> searchProjectByTitle(String keyword) {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.SEARCH_SUCCESS,userSearchService.searchProjectByTitle(keyword)));
  }
}
