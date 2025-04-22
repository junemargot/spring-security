package hello.springsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration     // 스프링 설정 클래스임을 나타냄
@EnableWebSecurity // Spring Security 활성화를 위한 애노테이션
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
            .authorizeHttpRequests((auth) -> auth
                    .requestMatchers("/", "/login", "/join", "/joinProc").permitAll() // 모든 사용자 접근 가능
                    .requestMatchers("/admin/**").hasRole("ADMIN") // /admin으로 시작하는 경로에 대해서 ADMIN 역할만 접근 가능
                    .requestMatchers("/my/**").hasAnyRole("ADMIN", "USER") // /my로 시작하는 경로에 대해서 ADMIN, USER 접근 가능
                    .anyRequest().authenticated() // 그 외 모든 요청은 인증된 사용자만 접근 가능
            );

    http
            .formLogin((auth) -> auth.loginPage("/login") // 커스텀 로그인 페이지 경로 지정
                    .loginProcessingUrl("/loginProc") // 로그인 폼 제출 시 요청을 처리할 URL 지정
                    .permitAll() // 로그인 페이지는 모든 사용자 접근 가능
            );

//    http
//            .csrf((auth) -> auth.disable()); // CSRF 보호 기능 비활성화

    // 세션 관리 설정
    http
            .sessionManagement((session) -> session
                    .maximumSessions(1) // 한 사용자 계정당 허용되는 최대 새션 수 설정
                    .maxSessionsPreventsLogin(true) // 최대 세션 수에 도달했을 때 새로운 로그인 시도를 어떻게 처리할지 설정. (true는 새로운 로그인 시도 차단)
            );

    http
            .sessionManagement((session) -> session
                    .sessionFixation().changeSessionId()
            );

    return http.build(); // 구성된 SecurityFilterChain 객체 반환
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {

    // 비밀번호 암호화를 위한 BCrypt 인코더 빈 등록
    // Bcrypt는 단방향 해시 함수로, 같은 비밀번호로 매번 다른 해시값을 생성
    return new BCryptPasswordEncoder();
  }
}
