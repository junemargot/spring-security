package hello.sprintsecurityoauth2clientsession.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

  @GetMapping("/my")
  public String myPage() {

    return "my";
  }
}
