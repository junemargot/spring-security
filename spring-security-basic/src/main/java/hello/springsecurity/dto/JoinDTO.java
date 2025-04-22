package hello.springsecurity.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinDTO {

  // 회원가입 폼에서 전달되는 데이터를 담는 dto
  String username;
  String password;

}
