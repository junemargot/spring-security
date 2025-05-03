package hello.oauth2jwt.filter;

import hello.oauth2jwt.dto.CustomOAuth2User;
import hello.oauth2jwt.dto.UserDTO;
import hello.oauth2jwt.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 요청마다 한 번 실행되는 JWT 인증 필터.
 * 1) Authorization 쿠키에서 토큰을 추출
 * 2) 만료·서명 검증 후 username, role 클레임을 꺼냄
 * 3) SecurityContext 에 UsernamePasswordAuthenticationToken 저장
 *    └ 이후 컨트롤러에서 @AuthenticationPrincipal 등으로 사용자 정보 활용 가능
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    /* 1) 쿠키에서 토큰 추출 ---------------------------- */
    // cookie들을 불러온 뒤 Authorization Key에 담긴 쿠키를 찾음
    String authorization = null;
    Cookie[] cookies = request.getCookies();
    for(Cookie cookie : cookies) {
      log.info(cookie.getName()+":"+cookie.getValue());

      if(cookie.getName().equals("Authorization")) {
        authorization = cookie.getValue();
      }
    }

    // 쿠키가 없거나, Authorization 키가 비어있으면 -> 다음 필터 진행
    if(authorization == null) {
      System.out.println("token null");
      filterChain.doFilter(request, response);
      return;
    }

    /* 2) 토큰 만료 여부 검사 ---------------------------- */
    String token = authorization;
    if(jwtUtil.isExpired(token)) {
      System.out.println("token expired");
      filterChain.doFilter(request, response);
      return;
    }

    /* 3) 클레임 추출 ---------------------------- */
    // 토큰에서 username과 role 획득
    String username = jwtUtil.getUsername(token);
    String role = jwtUtil.getRole(token);

    /*  4) UserDTO → CustomOAuth2User 생성 -------- */
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername(username);
    userDTO.setRole(role);

    // UserDetails에 회원 정보 객체 담기
    CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

    /* 5) SecurityContext 에 인증 정보 저장 ------------- */
    // 스프링 시큐리티 인증 토큰 생성
    Authentication authToken =
            new UsernamePasswordAuthenticationToken(userDTO, null, customOAuth2User.getAuthorities());

    // 세션에 사용자 등록
    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);
  }
}
