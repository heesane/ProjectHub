package com.project.hub.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
  // Global
  INTERNAL_SERVER_ERROR(500, "G001", "서버 오류"),
  INPUT_INVALID_VALUE(409, "G002", "잘못된 입력"),

  // Auth
  TOKEN_ACCESS_NOT_EXISTS(HttpStatus.UNAUTHORIZED.value(), "A001", "토큰을 찾을 수 없음"),
  TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "A002", "액세스 토큰 만료"),
  DUPLICATE_EMAIL(HttpStatus.CONFLICT.value(), "A003", "이미 존재하는 이메일"),
  DUPLICATE_NICKNAME(HttpStatus.CONFLICT.value(), "A004", "이미 존재하는 닉네임"),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "A005", "사용자를 찾을 수 없음"),
  UNMATCHED_PASSWORD(HttpStatus.BAD_REQUEST.value(), "A006", "비밀번호가 일치하지 않음"),
  ERROR_OAUTH(HttpStatus.BAD_REQUEST.value(), "A007", "OAuth 인증 오류"),
  INVALID_OAUTH_CODE(HttpStatus.BAD_REQUEST.value(), "A008", "잘못된 OAuth 코드"),

  // Project
  PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "P001", "프로젝트를 찾을 수 없음"),
  UNMATCHED_PROJECT_OWNER(HttpStatus.FORBIDDEN.value(), "P002", "프로젝트 소유자가 아님"),

  // Comments
  COMMENTS_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "C001", "댓글을 찾을 수 없음"),

  // Project Documents
  PROJECT_DOCUMENTS_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "PD001", "프로젝트 문서를 찾을 수 없음"),

  // Badge
  BADGE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "B001", "뱃지를 찾을 수 없음"),
  BADGE_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "B002", "이미 존재하는 뱃지"),
  ;
  private final int status;
  private final String code;
  private final String message;
}