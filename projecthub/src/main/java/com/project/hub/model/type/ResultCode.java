package com.project.hub.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

  // User
  USER_CREATED("U001", 201, "회원가입 성공"),
  USER_OAUTH_LOGIN_SUCCESS("U001", 201, "소셜 로그인 성공"),
  USER_OAUTH_REGISTER_SUCCESS("U001", 201, "소셜 회원가입 성공"),
  USER_LOGIN_SUCCESS("U002", 200, "로그인 성공"),
  USER_LOGIN_FAIL("U002", 401, "로그인 실패"),
  USER_DETAIL_INFO_SUCCESS("U003", 200, "유저 정보 조회 성공"),
  USER_UPDATE_SUCCESS("U004", 200, "유저 정보 수정 성공"),
  USER_PROFILE_SUCCESS("U005", 200, "프로필 조회 성공"),
  USER_VISIBLE_SUCCESS("U006", 200, "프로젝트 공개 여부 수정 성공"),


  // Project
  PROJECT_LIST_SUCCESS("P000", 200, "프로젝트 리스트 조회 성공"),
  PROJECT_DETAIL_SUCCESS("P000", 200, "프로젝트 상세 조회 성공"),
  PROJECT_CREATE_SUCCESS("P001", 201, "프로젝트 생성 성공"),
  PROJECT_UPDATE_SUCCESS("P002", 201, "프로젝트 수정 성공"),
  PROJECT_DELETE_SUCCESS("P003", 200, "프로젝트 삭제 성공"),

  // Comments
  COMMENT_WRITE_SUCCESS("C001", 201, "댓글 작성 성공"),
  COMMENT_UPDATE_SUCCESS("C005", 201, "댓글 수정 성공"),
  COMMENT_DELETE_SUCCESS("C002", 200, "댓글 삭제 성공"),
  COMMENT_REPLY_SUCCESS("C003", 201, "대댓글 작성 성공"),
  COMMENT_LIST_SUCCESS("C004", 200, "댓글 리스트 조회 성공"),

  // Likes
  PROJECT_DISLIKE_SUCCESS("L001", 200, "프로젝트 좋아요 취소 성공"),
  PROJECT_LIKE_SUCCESS("L002", 200, "프로젝트 좋아요 성공"),
  COMMENT_DISLIKE_SUCCESS("L003", 200, "댓글 좋아요 취소 성공"),
  COMMENT_LIKE_SUCCESS("L004", 200, "댓글 좋아요 성공"),

  // Search
  SEARCH_SUCCESS("S001", 200, "검색 성공"),

  // Badge
  BADGE_LIST_SUCCESS("B001", 200, "뱃지 리스트 조회 성공"),
  BADGE_CREATE_SUCCESS("B002", 201, "뱃지 생성 성공"),
  BADGE_UPDATE_SUCCESS("B003", 201, "뱃지 수정 성공"),
  BADGE_DELETE_SUCCESS("B004", 200, "뱃지 삭제 성공"),
  ;
  private final String code;
  private final int status;
  private final String message;
}
