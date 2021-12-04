package by.ak.chat.security;

import by.ak.chat.model.Storage;
import com.vaadin.flow.server.VaadinServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

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
