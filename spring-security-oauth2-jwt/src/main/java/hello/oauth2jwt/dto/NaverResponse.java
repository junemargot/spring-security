package hello.oauth2jwt.dto;

import java.util.Map;

public class NaverResponse implements OAuth2Response {

  private final Map<String, Object> attribute;

  public NaverResponse(Map<String, Object> attribute) {
    this.attribute =(Map<String, Object>) attribute.get("response"); // 네이버 응답 구조에 맞게 설정
  }

  @Override
  public String getProvider() {
    return "naver";
  }

  @Override
  public String getProviderId() {
    return attribute.get("id").toString();
  }

  @Override
  public String getEmail() {
    return attribute.get("email").toString();
  }

  @Override
  public String getName() {
    return attribute.get("name").toString();
  }
}
