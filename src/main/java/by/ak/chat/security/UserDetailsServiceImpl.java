package by.ak.chat.security;

import by.ak.chat.model.User;
import by.ak.chat.service.UserService;
import by.ak.chat.util.IPUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserService service;
  private final IPUtil ipUtil;
  private final LoginAttemptsService loginAttemptsService;

  @Override
  public UserDetails loadUserByUsername(String username) throws AuthenticationException {
    User user = service.find(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    log.info("User {} was found", user);

    if (!user.getEnabled()) {
      log.info("Attempt to login as user {} unsuccessful. Banned", username);
      throw new DisabledException("User is banned");
    }

    if (loginAttemptsService.isBlocked(ipUtil.getClientIP())) {
      log.info("Attempt to login as user {} unsuccessful. Blocked", username);
      throw new DisabledException("You are temporary blocked due to too many unsuccessful login attempts");
    }
    return UserDetailsImpl.build(user);
  }
}
