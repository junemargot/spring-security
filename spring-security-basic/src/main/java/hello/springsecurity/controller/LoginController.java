package hello.springsecurity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

  @GetMapping("/login")
  public String loginPage() {

    // 실제 로그인 처리는 Spring Security가 담당
    // SecurityConfig에서 .loginProcessingUrl("/loginProc")으로 지정된 URL 폼이 제출되면
    // Spring Security가 자동으로 인증 처리 진행
    return "login";
  }
}
