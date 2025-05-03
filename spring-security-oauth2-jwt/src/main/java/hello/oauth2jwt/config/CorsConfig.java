package hello.oauth2jwt.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class CorsConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry corsRegistry) {

    corsRegistry.addMapping("/**")
            .exposedHeaders("Set-Cookies")
            .allowedOrigins("http://localhost:3000");
  }
}
