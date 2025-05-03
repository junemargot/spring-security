package hello.oauth2jwt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

  private String name;
  private String username; // 우리 서버에서 만들 유저네임
  private String role;
}
