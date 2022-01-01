package by.ak.chat.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "security.authentication")
public class AuthenticationProperties {
  private Integer maxLoginAttempts;
  private Integer failedAttemptsExpiration;
}
