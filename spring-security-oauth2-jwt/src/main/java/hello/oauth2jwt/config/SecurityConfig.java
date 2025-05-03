package hello.oauth2jwt.config;

import hello.oauth2jwt.filter.JwtAuthenticationFilter;
import hello.oauth2jwt.oauth2.CustomSuccessHandler;
import hello.oauth2jwt.service.CustomOAuth2UserService;
import hello.oauth2jwt.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final CustomSuccessHandler customSuccessHandler;
  private final JwtUtil jwtUtil;

  public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler, JwtUtil jwtUtil) {
    this.customOAuth2UserService = customOAuth2UserService;
    this.customSuccessHandler = customSuccessHandler;
    this.jwtUtil = jwtUtil;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    // CORS 설정
    http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

      @Override
      public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setMaxAge(3600L);

        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

        return configuration;
      }
    }));

    // CSRF disable 설정
    http.csrf((auth) -> auth.disable());

    // Form Login 방식 disable 설정
    http.formLogin((auth) -> auth.disable());

    // HTTP Basic 인증 방식 disable 설정
    http.httpBasic((auth) -> auth.disable());

    // JWTAuthenticationFilter 추가
    http.addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

    // OAuth2 설정
    http.oauth2Login((oauth2) -> oauth2
            .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                    .userService(customOAuth2UserService))
            .successHandler(customSuccessHandler)
    );

    // 경로별 인가 작업
    http.authorizeHttpRequests((auth) -> auth
            .requestMatchers("/").permitAll()
            .anyRequest().authenticated());

    // 세션 STATELESS 설정
    http.sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}
