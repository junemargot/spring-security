package hello.springsecurityjwt.service;

import hello.springsecurityjwt.dto.JoinDto;
import hello.springsecurityjwt.repository.UserRepository;
import hello.springsecurityjwt.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  public JoinService(UserRepository userRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  public void join(JoinDto joinDto) {

    String username = joinDto.getUsername();
    String password = joinDto.getPassword();

    Boolean isUser = userRepository.existsByUsername(username);
    if(isUser) {
      return;
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    user.setRole("ROLE_USER");

    userRepository.save(user);
  }
}
