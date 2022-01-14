package by.ak.chat.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class EncoderConfig {

  public static final int STRENGTH = 8;

  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder(STRENGTH);
  }
}
