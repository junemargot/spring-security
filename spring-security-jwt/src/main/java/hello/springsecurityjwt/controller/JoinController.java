package hello.springsecurityjwt.controller;

import hello.springsecurityjwt.dto.JoinDto;
import hello.springsecurityjwt.service.JoinService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class JoinController {

  private final JoinService joinService;

  public JoinController(JoinService joinService) {
    this.joinService = joinService;
  }

  @PostMapping("/join")
  public String join(JoinDto joinDto) {

    joinService.join(joinDto);

    return "success";
  }
}
