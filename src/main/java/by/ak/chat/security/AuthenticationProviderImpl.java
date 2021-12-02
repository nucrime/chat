package by.ak.chat.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {

  private final UserDetailsServiceImpl service;
  private final PasswordEncoder encoder;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String name = authentication.getName();
    String password = authentication.getCredentials().toString();
    UserDetails user = service.loadUserByUsername(name);
    if (encoder.matches(password, user.getPassword())) {
      return new UsernamePasswordAuthenticationToken(name, password, user.getAuthorities());
    } else throw new BadCredentialsException("Not correct email or password");
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.isInstance(UsernamePasswordAuthenticationToken.class);
  }
}
