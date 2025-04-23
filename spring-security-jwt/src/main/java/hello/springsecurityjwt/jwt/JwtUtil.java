package hello.springsecurityjwt.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

  private SecretKey secretKey;

  public JwtUtil(@Value("${spring.jwt.secret}") String secret) {

    secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());

  }

  // 토큰 검증
  public String getUsernameFromToken(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
  }

  public String getRoleFromToken(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
  }

  public Boolean isExpired(String token) {
    try {
      Date currentDate = new Date();
      System.out.println("현재 시간: " + currentDate);

      // 토큰 파싱 및 만료 시간 추출
      Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
      Date expirationDate = claims.getExpiration();

      // 토큰 정보 출력
      System.out.println("토큰 발급 시간: " + claims.getIssuedAt());
      System.out.println("토큰 만료 시간: " + expirationDate);
      System.out.println("토큰 만료까지 남은 시간: " + (expirationDate.getTime() - currentDate.getTime()));

      // 토큰 주요 클레임 정보 정보 출력
      System.out.println("토큰 주체(subject): " + claims.getSubject());
      System.out.println("토큰에 포함된 권한: " + claims.get("role", String.class));

      // 만료 여부 확인 및 결과 출력
      boolean isExpired = expirationDate.before(currentDate);
      System.out.println("토큰 만료 여부: " + isExpired);

      return isExpired;

    } catch(ExpiredJwtException e) {
      System.out.println("토큰 검증 중 만료 예외 발생: " + e.getMessage());
      System.out.println("예외 발생 토큰의 만료 시간: " + e.getClaims().getExpiration());
      System.out.println("현재 시간과의 차이(ms): " + (new Date().getTime() - e.getClaims().getExpiration().getTime()));

      return true;

    } catch(Exception e) {
      System.out.println("토큰 검증 중 예외 발생: " + e.getClass().getName());
      System.out.println("예외 메시지: " + e.getMessage());
      e.printStackTrace();
      return true;
    }
//    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
  }

  // 토큰 생성
  public String createJwtToken(String username, String role, Long expiredMs) {

    return Jwts.builder()
            .claim("username", username)
            .claim("role", role)
            .issuedAt(new Date(System.currentTimeMillis()))               // 토큰 발행시간
            .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 토큰 소멸시간
            .signWith(secretKey)                                          // 암호화 진행
            .compact();
  }
}
