package hello.springsecurity.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.Iterator;

@Controller
public class MainController {

  @GetMapping("/")
  public String mainPage(Model model) {

    // SecurityContextHolder를 통해 현재 인증된 사용자 정보 접근
    // SecurityContext는 현재 사용자의 인증 및 권한 정보를 담고 있음
    String id = SecurityContextHolder.getContext().getAuthentication().getName(); // 현재 인증된 사용자의 이름(username) 가져오기

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // 인증 객체 가져오기

    // 사용자의 권한 정보 가져오기
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();
    String role = auth.getAuthority();

    model.addAttribute("id", id);
    model.addAttribute("role", role);

    return "main";
  }
}
