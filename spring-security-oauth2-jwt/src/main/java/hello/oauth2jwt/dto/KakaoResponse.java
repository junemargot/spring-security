package hello.oauth2jwt.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

  private final Map<String, Object> attribute;
  private final Map<String, Object> kakaoAccount;
  private final Map<String, Object> profile;

  @SuppressWarnings("unchecked")
  public KakaoResponse(Map<String, Object> attribute) {
    this.attribute = attribute;

    Object accountObj = attribute.get("kakao_account");
    this.kakaoAccount = accountObj instanceof Map ?(Map<String, Object>) accountObj : Map.of();

    Object profileObj = kakaoAccount.get("profile");
    this.profile = profileObj instanceof Map ? (Map<String, Object>) profileObj : Map.of();
  }

  // Google/Naver와 형식을 통일해주는 것이 좋다. 이렇게 해야 유지보수 하기가 쉽다.
  // 생성자를 하나로 정리해서, 내부에서 파싱하게 만들자.
//  public KakaoResponse(Map<String, Object> attribute, Map<String, Object> kakaoAccount, Map<String, Object> profile) {
//    this.attribute = attribute;
//    this.kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
//    this.profile = (Map<String, Object>) kakaoAccount.get("profile");
//  }

  @Override
  public String getProvider() {
    return "kakao";
  }

  @Override
  public String getProviderId() {
    return attribute.get("id").toString();
  }

  @Override
  public String getEmail() {
    return kakaoAccount.get("email").toString();
  }

  @Override
  public String getName() {
    return profile.get("nickname").toString();
  }
}