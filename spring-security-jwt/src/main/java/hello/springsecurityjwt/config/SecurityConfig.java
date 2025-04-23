package hello.springsecurityjwt.config;

import hello.springsecurityjwt.jwt.JwtFilter;
import hello.springsecurityjwt.jwt.JwtUtil;
import hello.springsecurityjwt.jwt.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

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

    // CORS 설정
    http
            .cors((cors) -> cors
                    .configurationSource(new CorsConfigurationSource() {

                      @Override
                      public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); // 허용할 Origin(출처) 지정, 프론트 앱이 실행되는 주소.
                        configuration.setAllowedMethods(Collections.singletonList("*")); // 어떤 HTTP 메서드를 허용할지 지정
                        configuration.setAllowCredentials(true); // 쿠키, 인증 헤더, TLS 인증서 등을 포함한 요청 허용 여부
                        configuration.setAllowedHeaders(Collections.singletonList("*")); // 요청에 포함할 수 있는 HTTP Header를 지정
                        configuration.setMaxAge(3600L); // preflight 요청(OPTIONS)이 브라우저 캐시에 저장되는 시간(초 단위): 3600L = 1시간 동안은 같은 요청이면 다시 preflight 요청을 보내지 않음
                        configuration.setExposedHeaders(Collections.singletonList("Authorization")); // 브라우저에서 클라이언트가 응답 헤더 중 어떤 것을 JS로 접근 가능하게 할지 설정: JWT를 담아주는 Authorization 헤더를 클라이언트에서 읽을 수 있게 함

                        return configuration;
                      }
                    }));

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
