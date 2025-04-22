package hello.springsecurity.service;

import hello.springsecurity.dto.JoinDTO;
import hello.springsecurity.entity.User;
import hello.springsecurity.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

  private final UserRepository userRepository; // 사용자 정보 저장을 위한 repository 주입
  private final BCryptPasswordEncoder bCryptPasswordEncoder; // 비밀번호 암호화를 위한 encoder 주입

  public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.userRepository = userRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  // 회원가입 처리
  public void joinProcess(JoinDTO joinDTO) {

    // DB에 동일한 username을 가진 회원이 존재하는지 확인
    boolean isUser = userRepository.existsByUsername(joinDTO.getUsername());

    if(isUser) {
      return;
    }

    // 새 사용자 엔티티 생성
    User user = new User();
    user.setUsername(joinDTO.getUsername());
    user.setPassword(bCryptPasswordEncoder.encode(joinDTO.getPassword())); // 비밀번호는 반드시 암호화하여 저장
    user.setRole("ROLE_USER"); // 기본 역할 설정 - 모든 신규 사용자에게 USER 역할 부여

    userRepository.save(user);
  }
}
