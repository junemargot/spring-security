package hello.sprintsecurityoauth2clientsession.dto;

import java.util.Map;

public class NaverResponse implements OAuth2Response {

  // 사용자의 데이터를 받을 변수
  private final Map<String, Object> attribute;

  public NaverResponse(Map<String, Object> attribute) {
    this.attribute = attribute;
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
