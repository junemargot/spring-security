package hello.oauth2jwt.service;

import hello.oauth2jwt.dto.*;
import hello.oauth2jwt.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import repository.UserRepository;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  public CustomOAuth2UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    OAuth2User oAuth2User = super.loadUser(userRequest);
    log.info("oAuth2User: {}", oAuth2User);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    OAuth2Response oAuth2Response = null;

    if (registrationId.equals("google")) {
      oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

    } else if (registrationId.equals("naver")) {
      oAuth2Response = new NaverResponse(oAuth2User.getAttributes());

    } else if (registrationId.equals("kakao")) {
      oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());

    } else {
      return null;
    }

    // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 생성
    String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

    // User 조회 : 존재하면 업데이트, 존재하지 않으면 신규 생성
    User existUser = userRepository.findByUsername(username);
    if(existUser == null) {
      User user = new User();
      user.setName(oAuth2Response.getName());
      user.setEmail(oAuth2Response.getEmail());
      user.setUsername(username);
      user.setRole("ROLE_USER");

      userRepository.save(user);

      UserDTO userDTO = new UserDTO();
      userDTO.setName(oAuth2Response.getName());
      userDTO.setUsername(username);
      userDTO.setRole("ROLE_USER");

      return new CustomOAuth2User(userDTO);

    } else {
      existUser.setName(oAuth2Response.getName());
      existUser.setEmail(oAuth2Response.getEmail());

      userRepository.save(existUser);

      UserDTO userDTO = new UserDTO();
      userDTO.setName(oAuth2Response.getName());
      userDTO.setUsername(existUser.getUsername());
      userDTO.setRole(existUser.getRole());

      return new CustomOAuth2User(userDTO);
    }
  }
}
