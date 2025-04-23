package hello.springsecurityjwt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {

    // 모든 경로에대해 CORS 설정 적용, 허용할 Origin을 설정
    registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000");
  }
}
