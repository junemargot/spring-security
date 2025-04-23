package hello.springsecurityjwt.config;

import hello.springsecurityjwt.jwt.JwtFilter;
import hello.springsecurityjwt.jwt.JwtUtil;
import hello.springsecurityjwt.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;
  private final JwtUtil jwtUtil;

  public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtUtil jwtUtil) {
    this.authenticationConfiguration = authenticationConfiguration;
    this.jwtUtil = jwtUtil;
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {

    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    // form 로그인 방식
    http
            .formLogin((auth) -> auth.disable());

    // http basic 인증 방식
    http
            .httpBasic((auth) -> auth.disable());

    // CSRF 설정
    http
            .csrf((auth) -> auth.disable());

    // 경로별 인가 설정
    http
            .authorizeHttpRequests((auth) -> auth
                    .requestMatchers("/", "/login", "/join").permitAll()
                    .requestMatchers("/admin").hasRole("ADMIN")
                    .anyRequest().authenticated());
    // JWT 필터 등록
    http
            .addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

    http
            .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

    // 세션 설정 : JWT를 통한 인증/인가를 위해서는 session을 STATELESS 상태로 설정하는 것이 중요
    http
            .sessionManagement((session) -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS));



    return http.build();

  }
}
