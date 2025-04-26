package hello.sprintsecurityoauth2clientsession.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.stereotype.Component;

@Component
public class SocialClientRegistration {

  @Value("${oauth2.google.client-id}")
  private String googleClientId;

  @Value("${oauth2.google.client-secret}")
  private String googleClientSecret;

  @Value("${oauth2.naver.client-id}")
  private String naverClientId;

  @Value("${oauth2.naver.client-secret}")
  private String naverClientSecret;

  @Value("${oauth2.kakao.client-id}")
  private String kakaoClientId;

  @Value("${oauth2.kakao.client-secret}")
  private String kakaoClientSecret;

  public ClientRegistration naverClientRegistration() {

    return ClientRegistration.withRegistrationId("naver")
            .clientId(naverClientId)
            .clientSecret(naverClientSecret)
            .redirectUri("http://localhost:8080/login/oauth2/code/naver")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .scope("name", "email")
            .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
            .tokenUri("https://nid.naver.com/oauth2.0/token")
            .userInfoUri("https://openapi.naver.com/v1/nid/me")
            .userNameAttributeName("response")
            .build();
  }

  public ClientRegistration googleClientRegistration() {

    return ClientRegistration.withRegistrationId("google")
            .clientId(googleClientId)
            .clientSecret(googleClientSecret)
            .redirectUri("http://localhost:8080/login/oauth2/code/google")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .scope("profile", "email")
            .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
            .tokenUri("https://www.googleapis.com/oauth2/v4/token")
            .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
            .issuerUri("https://accounts.google.com")
            .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
            .userNameAttributeName(IdTokenClaimNames.SUB)
            .build();
  }

  public ClientRegistration kakaoClientRegistration() {

    return ClientRegistration.withRegistrationId("kakao")
            .clientId(kakaoClientId)
            .clientSecret(kakaoClientSecret)
            .redirectUri("http://localhost:8080/login/oauth2/code/kakao")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
            .scope("profile_nickname", "account_email")
            .authorizationUri("https://kauth.kakao.com/oauth/authorize")
            .tokenUri("https://kauth.kakao.com/oauth/token")
            .userInfoUri("https://kapi.kakao.com/v2/user/me")
            .userNameAttributeName("id")
            .build();
  }
}
