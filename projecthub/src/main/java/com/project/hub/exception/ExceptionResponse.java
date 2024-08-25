package com.project.hub.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionResponse {

  private String businessCode;
  private String errorMessage;
  private List<FieldError> errors;

  private ExceptionResponse(ExceptionCode code, List<FieldError> fieldErrors) {
    this.businessCode = code.getCode();
    this.errorMessage = code.getMessage();
    this.errors = fieldErrors;
  }

  private ExceptionResponse(ExceptionCode code) {
    this.businessCode = code.getCode();
    this.errorMessage = code.getMessage();
    this.errors = new ArrayList<>();
  }

  public static ExceptionResponse of(final ExceptionCode code, final BindingResult bindingResult) {
    return new ExceptionResponse(code, FieldError.of(bindingResult));
  }

  public static ExceptionResponse of(ExceptionCode code) {
    return new ExceptionResponse(code);
  }


  @Getter
  @AllArgsConstructor
  public static class FieldError {

    private String field;
    private String value;
    private String reason;

    private static List<FieldError> of(final BindingResult bindingResult) {
      final List<org.springframework.validation.FieldError> fieldErrors =
          bindingResult.getFieldErrors();
      return fieldErrors.stream()
          .map(
              error ->
                  new FieldError(
                      error.getField(),
                      error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                      error.getDefaultMessage()))
          .collect(Collectors.toList());
    }
  }
}
