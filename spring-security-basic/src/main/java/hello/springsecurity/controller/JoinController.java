package hello.springsecurity.controller;

import hello.springsecurity.dto.JoinDTO;
import hello.springsecurity.service.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class JoinController {

  private final JoinService joinService;

  public JoinController(JoinService joinService) {
    this.joinService = joinService;
  }

  @GetMapping("/join")
  public String joinPage() {

    return "join";
  }

  @PostMapping("/joinProc")
  public String joinProcess(JoinDTO joinDTO) {

    joinService.joinProcess(joinDTO);
    System.out.println("회원가입이 완료되었습니다. Join User: " + joinDTO.getUsername());

    return "redirect:/login";
  }
}
