package by.ak.chat.security.service;

import by.ak.chat.model.Role;
import by.ak.chat.model.User;
import by.ak.chat.security.config.UserDetailsImpl;
import by.ak.chat.security.handler.ChatLogoutHandler;
import com.vaadin.flow.component.UI;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.stream.Collectors;

@Component
@SessionScope
@RequiredArgsConstructor
@AutoConfigureAfter(SessionRegistryImpl.class)
public class SecurityService {

  private static final String LOGOUT_SUCCESS_URL = "/";
  private final ChatLogoutHandler logoutHandler;
  private final SessionRegistry sessionRegistry;
  private SecurityContext context = SecurityContextHolder.getContext();
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

  public String getRole() {
    Authentication authentication = context.getAuthentication();
    if (authentication == null) {
      return null;
    }
    return authentication.getAuthorities().stream()
      .findFirst()
      .map(GrantedAuthority::getAuthority)
      .orElse(Role.USER.name());
  }

  public boolean isAdmin() {
    return getRole().equals(Role.ADMINISTRATOR.name());
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

  private List<UserDetailsImpl> getAllActiveUsers() {
    return sessionRegistry.getAllPrincipals().stream()
      .filter(principal -> principal instanceof UserDetailsImpl)
      .map(principal -> (UserDetailsImpl) principal).collect(Collectors.toList());
  }

  public void expireUserSessions(User user) {
    getAllActiveUsers().stream()
      .filter(principal -> principal.getUsername().equals(user.getUsername())).forEach(principal -> {
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
        sessions.forEach(SessionInformation::expireNow);
      });
  }

  private boolean isActive(User user) {
    return getAllActiveUsers().stream()
      .anyMatch(principal -> principal.getUsername().equals(user.getUsername()));
  }
}
