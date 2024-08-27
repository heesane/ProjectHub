package com.project.hub.exception;

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

  // Project
  PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "P001", "프로젝트를 찾을 수 없음"),
  UNMATCHED_PROJECT_OWNER(HttpStatus.FORBIDDEN.value(), "P002", "프로젝트 소유자가 아님"),
  ;
  private final int status;
  private final String code;
  private final String message;
}