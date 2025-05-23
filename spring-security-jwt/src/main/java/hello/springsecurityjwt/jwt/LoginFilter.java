package hello.springsecurityjwt.jwt;

import hello.springsecurityjwt.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

    // 클라이언트 요청에서 username, password 추출
    String username = obtainUsername(request);
    String password = obtainPassword(request);

    System.out.println("username = " + username);

    // 스프링시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 한다.
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

    // 검증을 위해 AuthenticationManager로 토큰을 전달
    return authenticationManager.authenticate(authToken);
  }

  // 로그인 성공(인증 성공)시 실행하는 메서드 (여기서 JWT 토큰을 발급)
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    // 사용자명 추출
    String username = customUserDetails.getUsername();

    // 사용자권한 추출
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();
    String role = auth.getAuthority();

    // 토큰 생성
    String token = jwtUtil.createJwtToken(username, role, 60*60*10L);

    System.out.println("successful authentication");

    response.addHeader("Authorization", "Bearer " + token);
  }

  // 로그인 실패시 실행하는 메서드
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

    System.out.println("unsuccessful authentication");

    response.setStatus(401);
  }
}
