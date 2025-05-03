package hello.oauth2jwt.util;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 생성과 검증을 전담하는 유틸리티.
 * ① 애플리케이션 기동 시 비밀키를 읽어 HS256 SecretKey를 준비
 * ② createJwt()  : 클레임 + 만료시간을 담아 토큰 발급
 * ③ getUsername(): 토큰에서 username 추출
 * ④ getRole()    : 토큰에서 role   추출
 * ⑤ isExpired()  : 토큰 만료 여부 확인
 */
@Component
public class JwtUtil {

  // HS256 서명에 사용될 비밀 키 객체
  private SecretKey secretKey;

  public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
    // 문자열을 UTF-8 byte 배열로 변환한 뒤 SecretKeySpec으로 래핑
    this.secretKey = new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8),    // 키 바이트
            Jwts.SIG.HS256.key().build().getAlgorithm() // "HmacSHA256"
    );
  }

  // ──────────────────────────────────────────────────────────────
  //  토큰 검증용 파서(parser) 빌더는 JJWT 0.12+에서 검증/파싱을 동시에 수행
  // ──────────────────────────────────────────────────────────────
  // 토큰에서 username 클레임 추출 - verifyWith(secretKey) 단계에서 서명 검증 실패 시 예외 발생
  public String getUsername(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
  }

  public String getRole(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
  }

  public Boolean isExpired(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
  }

  /**
   * 액세스 토큰 생성
   *
   * @param username  사용자 ID(혹은 username)
   * @param role      권한(Role) 문자열
   * @param expiredMs 만료 시간(ms) – 예) 86_400_000L == 24시간
   * @return          HS256 서명 완료된 JWT 문자열
   */
  public String createJwt(String username, String role, Long expiredMs) {

    return Jwts.builder()
            .claim("username", username)   // 커스텀 클레임
            .claim("role", role)
            .issuedAt(new Date(System.currentTimeMillis())) // 발급 시각
            .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료 시각
            .signWith(secretKey) // HS256 서명
            .compact(); // 직렬화
  }
}

