package hello.sprintsecurityoauth2clientsession.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

  // 사용자의 데이터를 받을 변수
  private final Map<String, Object> attribute;

  public KakaoResponse(Map<String, Object> attribute) {
    this.attribute = attribute;
  }

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

    // 이메일은 kakaoAccount 내부에 있다.
    Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
    if(kakaoAccount == null || kakaoAccount.get("email") == null) {
      return null;
    }

    return kakaoAccount.get("email").toString();
  }

  @Override
  @SuppressWarnings("unchecked")
  public String getName() {

    // kakao의 사용자 정보는 중첩 구조로 되어 있으며,
    // 닉네임(nickname)은 kakao_account.profile.nickname에 위치함
    Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");

    // kakao_account가 존재하는 경우에만 내부의 profile 접근 시도
    if (kakaoAccount != null) {
      Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

      // profile이 존재하는 경우에 nickname 확인
      if(profile != null) {
        Object nickname = profile.get("nickname");

        // nickname이 null이 아니고, 빈 문자열이 아닌 경우 -> 해당 값 반환
        // 사용자가 닉네임 수집에 동의한 경우에만 해당
        if(nickname != null && !nickname.toString().isBlank()) {
          return nickname.toString();
        }
      }
    }

    // nickname이 없거나, 동의하지 않아 누락된 경우
    // fallback으로 "kakao_고유ID" 형태의 값 반환 -> pricipalName 대체용
    // 이 값을 스프링 시큐리티의 getName() -> principalName으로 활용
    return getProvider() + "_" + getProviderId();
  }
}
