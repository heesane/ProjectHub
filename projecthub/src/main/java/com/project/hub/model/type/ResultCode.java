package com.project.hub.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
  // user
  USER_CREATED("U001", "회원가입 성공"),
  USER_LOGIN_SUCCESS("U002", "로그인 성공"),
  USER_DETAIL_INFO_SUCCESS("U003", "유저 정보 조회 성공"),
  USER_UPDATE_SUCCESS("U004", "유저 정보 수정 성공"),
  ;

  private final String code;
  private final String message;
}
