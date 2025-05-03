package hello.oauth2jwt.oauth2;

import hello.oauth2jwt.dto.CustomOAuth2User;
import hello.oauth2jwt.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * OAuth2 로그인 성공 시 호출되는 SuccessHandler.
 * 1) 인증 객체에서 사용자·권한 추출
 * 2) JWT 생성 후 HttpOnly 쿠키에 저장
 * 3) 프론트엔드 URL로 리다이렉트
 */
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtUtil jwtUtil;

  public CustomSuccessHandler(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  // 인증 성공 직후 실행되는 콜백 메서드
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

    // 1) Principal -> CustomOAuth2User 다운캐스팅
    CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
    String username = customUserDetails.getUsername(); // DB에 저장된 username

    // 2) 권한(Role) 하나만 추출
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();
    String role = auth.getAuthority();

    // 3) JWT 발급 (만료 60*60*60초 = 1시간 설정)
    String token = jwtUtil.createJwt(username, role, 60*60*60L);

    // 4) JWT를 HttpOnly 쿠키로 내려보냄
    response.addCookie(createCookie("Authorization", token));

    // 5) 인증 후 리다이렉트
    response.sendRedirect("http://localhost:3000/");
  }

  /**
   * 쿠키 생성 헬퍼
   * - HttpOnly: true  → JS 접근 차단
   * - Secure  : HTTPS 환경에서만 전송하도록 주석 처리(운영 시 활성화)
   */
  private Cookie createCookie(String key, String value) {

    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(60*60*60);  // 만료 시간을 토큰과 동일하게 설정
    // cookie.setSecure(true);   // HTTP 배포 시 활성화 필수
    cookie.setPath("/");         // 모든 경로에서 쿠키 전송
    cookie.setHttpOnly(true);    // XSS 방지

    return cookie;
  }
}
