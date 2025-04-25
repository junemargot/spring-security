package hello.sprintsecurityoauth2clientsession.service;

import hello.sprintsecurityoauth2clientsession.dto.CustomOAuth2User;
import hello.sprintsecurityoauth2clientsession.dto.GoogleResponse;
import hello.sprintsecurityoauth2clientsession.dto.NaverResponse;
import hello.sprintsecurityoauth2clientsession.dto.OAuth2Response;
import hello.sprintsecurityoauth2clientsession.entity.User;
import hello.sprintsecurityoauth2clientsession.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  // DefaultOAuth2UserService OAuth2UserService의 구현체이다. OAuth2UserService를 상속받아도 되고, 상관없다.

  private final UserRepository userRepository;

  public CustomOAuth2UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    OAuth2User oAuth2User = super.loadUser(userRequest);
    log.info("OAuth2User: {}", oAuth2User.getAttributes());

    // userRequest를 통해 외부 지원 서비스사가 들어오게 된다. 어떤 서비스인지 구분해야 한다.
    // 어떤 인증 provider인지 확인
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    OAuth2Response oAuth2Response = null;

    // naver, google를 나누는 이유는 네이버에서 보내주는 인증데이터 규격과, 구글에서 보내주는 인증데이터 규격이 다르다.
    // => 다른 바구니에 담아야 한다. (dto) 그게 OAuth2Response 클래스
    if(registrationId.equals("naver")) {
      oAuth2Response = new NaverResponse(oAuth2User.getAttributes());

    } else if(registrationId.equals("google")) {
      oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

    } else {
      return null;
    }

    String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
    User existUser = userRepository.findByUsername(username);
    String role = "ROLE_USER";

    if(existUser == null) {
      User user = new User();
      user.setUsername(username);
      user.setEmail(oAuth2Response.getEmail());
      user.setRole(role);

      userRepository.save(user);

    } else {
      existUser.setUsername(username);
      existUser.setEmail(oAuth2Response.getEmail());
      role = existUser.getRole();
      userRepository.save(existUser);
    }

//    String role = "ROLE_USER";

    return new CustomOAuth2User(oAuth2Response, role);
  }
}
