package by.ak.chat.security;

import by.ak.chat.util.IPUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationFailureEventListener
  implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

  private final LoginAttemptsService loginAttemptService;
  private final IPUtil ipUtil;

  @Override
  public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
    loginAttemptService.loginFailed(ipUtil.getClientIP());
  }
}
