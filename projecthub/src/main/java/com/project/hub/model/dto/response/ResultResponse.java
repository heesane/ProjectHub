package com.project.hub.model.dto.response;

import com.project.hub.model.type.ResultCode;
import lombok.Getter;

@Getter
public class ResultResponse {

  private final String code;
  private final String message;
  private final Object data;

  public static ResultResponse of(ResultCode resultCode, Object data) {
    return new ResultResponse(resultCode, data);
  }

  public static ResultResponse of(ResultCode resultCode) {
    return new ResultResponse(resultCode, null);
  }

  public ResultResponse(ResultCode resultCode, Object data) {
    this.code = resultCode.getCode();
    this.message = resultCode.getMessage();
    this.data = data;
  }
}
