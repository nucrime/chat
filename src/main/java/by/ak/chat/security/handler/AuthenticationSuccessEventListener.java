package by.ak.chat.security.handler;

import by.ak.chat.security.service.LoginAttemptsService;
import by.ak.chat.util.IPUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessEventListener
  implements ApplicationListener<AuthenticationSuccessEvent> {

  private final LoginAttemptsService loginAttemptService;
  private final IPUtil ipUtil;

  @Override
  public void onApplicationEvent(final AuthenticationSuccessEvent e) {
    loginAttemptService.loginSucceeded(ipUtil.getClientIP());
  }
}
