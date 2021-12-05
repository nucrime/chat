package by.ak.chat.security;

import com.vaadin.flow.component.UI;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PreDestroy;

@Component
@SessionScope
@RequiredArgsConstructor
public class SecurityService {

  private static final String LOGOUT_SUCCESS_URL = "/";
  private SecurityContext context = SecurityContextHolder.getContext();
  private final ChatLogoutHandler logoutHandler;
  private String username;

  public boolean isUserLoggedIn() {
    return context.getAuthentication() != null;
  }

  public String getLoggedInUserName() {
    if (username == null) {
      Authentication authentication = context.getAuthentication();
      if (authentication == null) {
        return null;
      }
      Object principal = authentication.getPrincipal();
      if (principal instanceof AuthenticatedPrincipal) {
        username = ((AuthenticatedPrincipal) principal).getName();
        return username;
      } else if (principal instanceof UserDetails) {
        username = ((UserDetails) principal).getUsername();
        return username;
      } else {
        username = principal.toString();
        return principal.toString();
      }
    }
    return username;
  }

  public void logout() {
    UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
    logoutAndInvalidate();
  }

  public Authentication getAuthentication() {
    return context.getAuthentication();
  }

  public UserDetails getAuthenticatedUser() {
    Authentication authentication = getAuthentication();
    if (authentication == null) {
      return null;
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof UserDetails) {
      return (UserDetails) principal;
    }
    // Anonymous or no authentication.
    return null;
  }

  @PreDestroy
  public void logoutOnTimeout() {
    logoutAndInvalidate();
  }

  private void logoutAndInvalidate() {
    logoutHandler.logout(invalidateUser());
  }

  private String invalidateUser() {
    String user = username;
    username = null;
    return user;
  }
}
