package by.ak.chat.security;

import by.ak.chat.model.Storage;
import by.ak.chat.view.ChatView;
import by.ak.chat.view.LoginView;
import com.vaadin.flow.server.VaadinServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler extends SecurityContextLogoutHandler {
  private static final String USER_LEFT_MSG = "**%s** left";
  private final Storage storage;

  public void logout(String user) {
    storage.addMessageUserLeft(String.format(USER_LEFT_MSG, user));
    logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
      null);
  }
}
