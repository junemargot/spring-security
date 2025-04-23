package hello.springsecurityjwt.jwt;

import hello.springsecurityjwt.dto.CustomUserDetails;
import hello.springsecurityjwt.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  public JwtFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    // HTTP 요청 헤더에서 "Authorization" 헤더 값을 가져온다.
    String authorization = request.getHeader("Authorization");

    // 토큰 형식 검증 - Authorization 헤더 검증
    if(authorization == null || !authorization.startsWith("Bearer ")) {
      System.out.println("유효한 토큰을 찾을 수 없습니다.");
      filterChain.doFilter(request, response);
      return;
    }

    System.out.println("유효한 토큰을 찾았습니다.");

    // Bearer 접두사 제거 후 순수 토큰만 추출
    String token = authorization.split(" ")[1];

    // 토큰이 존재하지만, 소멸시간이 지났는지에 대한 검증
    if(jwtUtil.isExpired(token)) {
      System.out.println("만료된 토큰입니다.");
      filterChain.doFilter(request, response); // 토큰이 만료되었다면 다음 필터로 요청을 전달하고 현재 필터 처리를 종료
      return;
    }

    // 토큰에서 일시적인 session을 생성해 securityContextHolder에 전달
    // 토큰에서 사용자 정보 추출 - 유효한 토큰에서 사용자 이름과 역할 정보를 추출
    // 이 정보는 JWT 토큰의 payload에 포함된 클레임에서 가져온다.
    String username = jwtUtil.getUsernameFromToken(token);
    String role = jwtUtil.getRoleFromToken(token);

    // 사용자 객체 생성 - 토큰에서 추출한 정보로 User 객체를 생성
    User user = new User();
    user.setUsername(username);
    user.setPassword("temppassword"); // 비밀번호는 토큰에 담겨있지 않기 때문에 임시 값을 설정
    user.setRole(role);

    // 생성한 User 객체로 SpringSecurity의 UserDetails 구현체인 CustomUserDetails 객체를 생성
    CustomUserDetails customUserDetails = new CustomUserDetails(user);

    // Spring Security의 인증 객체(Authentication) 생성
    Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

    // 인증 객체를 SecurityContextHolder에 설정하여 현재 요청 스레드에서 인증된 사용자로 처리되도록 함
    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);
  }
}
