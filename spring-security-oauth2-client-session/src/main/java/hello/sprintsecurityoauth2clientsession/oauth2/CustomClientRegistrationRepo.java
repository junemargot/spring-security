package hello.sprintsecurityoauth2clientsession.oauth2;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@Configuration
public class CustomClientRegistrationRepo {

  private final SocialClientRegistration socialClientRegistration;

  public CustomClientRegistrationRepo(SocialClientRegistration socialClientRegistration) {
    this.socialClientRegistration = socialClientRegistration;
  }

  public ClientRegistrationRepository getClientRegistration() {

    return new InMemoryClientRegistrationRepository(
            socialClientRegistration.naverClientRegistration(),
            socialClientRegistration.googleClientRegistration(),
            socialClientRegistration.kakaoClientRegistration());
  }
}
