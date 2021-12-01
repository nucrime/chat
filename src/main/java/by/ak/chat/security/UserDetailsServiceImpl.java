package by.ak.chat.security;

import by.ak.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserService service;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return UserDetailsImpl.build(service.find(email));
  }
}
