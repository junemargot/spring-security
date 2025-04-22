package hello.springsecurity.service;

import hello.springsecurity.dto.CustomUserDetails;
import hello.springsecurity.entity.User;
import hello.springsecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  // 사용자 정보 조회를 위한 repository 주입(생성자 주입)
  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // Spring Security 인증 과정에서 자동으로 호출됨
    // 사용자가 로그인 폼에 입력한 username으로 DB에서 사용자 정보 조회
    User userData = userRepository.findByUsername(username);

    if (userData != null) {
      // 사용자 정보가 존재하면 CustomUserDetails 객체 생성하여 반환
      // Spring Security는 이 객체를 사용하여 비밀번호 검증 및 권한을 확인
      return new CustomUserDetails(userData);
    }

    throw new UsernameNotFoundException(username);
  }
}
