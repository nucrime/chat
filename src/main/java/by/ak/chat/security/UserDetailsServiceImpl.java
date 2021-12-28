package by.ak.chat.security;

import by.ak.chat.model.User;
import by.ak.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserService service;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = service.find(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    log.info("User {} was found", user);

    if (!user.getEnabled()) {
      log.info("Attempt to login as user {} unsuccessful. Banned", username);
      throw new DisabledException("User is banned");
    }
    return UserDetailsImpl.build(user);
  }
}
