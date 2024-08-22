package com.project.hub.auth.oauth.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuth2Attributes {

  private final Map<String, Object> attributes;
  private final String nameAttributeKey;
  private final String name;
  private final String email;
//    private String pictureURL; // 프로필 사진도 가져올 수 있음

  @Builder
  public OAuth2Attributes(Map<String, Object> attributes,
      String nameAttributeKey, String name,
      String email) {
    this.attributes = attributes;
    this.nameAttributeKey = nameAttributeKey;
    this.name = name;
    this.email = email;
  }

  // OAuth2User에서 반환하는 사용자 정보는 Map. 따라서 값 하나하나를 변환해야함.
  public static OAuth2Attributes of(String registrationId,
      String userNameAttributeName,
      Map<String, Object> attributes) {

    if ("naver".equals(registrationId)) {
      return ofNaver("id", attributes);
    }

    return ofGoogle(userNameAttributeName, attributes);
  }

  // 구글 생성자
  private static OAuth2Attributes ofGoogle(String usernameAttributeName,
      Map<String, Object> attributes) {
    return OAuth2Attributes.builder()
        .name((String) attributes.get("name"))
        .email((String) attributes.get("email"))
        .attributes(attributes)
        .nameAttributeKey(usernameAttributeName)
        .build();
  }

  // naver 생성자 (추후 사용예정)
  private static OAuth2Attributes ofNaver(String userNameAttributeName,
      Map<String, Object> attributes) {
    Map<String, Object> response = (Map<String, Object>) attributes.get("response");

    return OAuth2Attributes.builder()
        .name((String) response.get("name"))
        .email((String) response.get("email"))
        .attributes(response)
        .nameAttributeKey(userNameAttributeName)
        .build();
  }
}
