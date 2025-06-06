package hello.sprintsecurityoauth2clientsession.config;

import hello.sprintsecurityoauth2clientsession.oauth2.CustomClientRegistrationRepo;
import hello.sprintsecurityoauth2clientsession.oauth2.CustomOAuth2AuthorizedClientService;
import hello.sprintsecurityoauth2clientsession.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final CustomClientRegistrationRepo customClientRegistrationRepo;
  private final CustomOAuth2AuthorizedClientService customOAuth2AuthorizedClientService;
  private final JdbcTemplate jdbcTemplate;

  public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomClientRegistrationRepo customClientRegistrationRepo, CustomOAuth2AuthorizedClientService customOAuth2AuthorizedClientService, JdbcTemplate jdbcTemplate) {
    this.customOAuth2UserService = customOAuth2UserService;
    this.customClientRegistrationRepo = customClientRegistrationRepo;
    this.customOAuth2AuthorizedClientService = customOAuth2AuthorizedClientService;
    this.jdbcTemplate = jdbcTemplate;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    // CSRF 설정
    http
            .csrf((csrf) -> csrf.disable());

    // form login
    http
            .formLogin((login) -> login.disable());

    // HTTP Basic 인증방식
    http
            .httpBasic((httpBasic) -> httpBasic.disable());

    // OAuth2
    http
            .oauth2Login((oauth2) -> oauth2
                    .loginPage("/login")
                    .clientRegistrationRepository(customClientRegistrationRepo.getClientRegistration())
                    .authorizedClientService(customOAuth2AuthorizedClientService.oAuth2AuthorizedClientService(jdbcTemplate, customClientRegistrationRepo.getClientRegistration()))
                    .userInfoEndpoint((userInfoEndpointConfig) ->
                            userInfoEndpointConfig.userService(customOAuth2UserService))
                    );

    // 경로 인가 설정
    http
            .authorizeHttpRequests((auth) -> auth
                    .requestMatchers("/", "/oauth2/**", "/login/**", "/images/**").permitAll()
                    .anyRequest().authenticated());

    return http.build();
  }
}
