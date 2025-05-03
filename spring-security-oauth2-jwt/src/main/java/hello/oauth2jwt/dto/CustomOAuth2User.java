package hello.oauth2jwt.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

  private final UserDTO userDTO;

  public CustomOAuth2User(UserDTO userDTO) {
    this.userDTO = userDTO;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return Map.of();
  }

  // ROLE 값 반환
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    Collection<GrantedAuthority> collection = new ArrayList<>();
    collection.add(new GrantedAuthority() {

      @Override
      public String getAuthority() {
        return userDTO.getRole();
      }
    });

    return collection;
  }

  @Override
  public String getName() {
    return userDTO.getName();
  }

  // 우리 서버에서 만들어주는 값
  public String getUsername() {
    return userDTO.getUsername();
  }
}
