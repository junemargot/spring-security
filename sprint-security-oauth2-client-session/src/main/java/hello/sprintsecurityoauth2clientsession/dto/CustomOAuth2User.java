package hello.sprintsecurityoauth2clientsession.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

  private final OAuth2Response oAuth2Response;
  private final String role;

  public CustomOAuth2User(OAuth2Response oAuth2Response, String role) {
    this.oAuth2Response = oAuth2Response;
    this.role = role;
  }

  // 넘어 오는 모든 데이터
  @Override
  public Map<String, Object> getAttributes() {
    return Map.of();
  }

  // 롤값
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    Collection<GrantedAuthority> collection = new ArrayList<>();
    collection.add(new GrantedAuthority() {

      @Override
      public String getAuthority() {
        return role;
      }
    });

    return collection;
  }

  @Override
  public String getName() {

    return oAuth2Response.getName();
  }

  // 추가 생성 메서드
  public String getUsername() {

    return oAuth2Response.getProvider()+ " " + oAuth2Response.getProviderId();
  }
}
