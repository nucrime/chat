package by.ak.chat.security.handler;

import by.ak.chat.service.StorageService;
import com.vaadin.flow.server.VaadinServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class ChatLogoutHandler extends SecurityContextLogoutHandler {
  private static final String USER_LEFT_MSG = "**%s** left";
  private final StorageService storage;

  /**
   * If @param user String is empty then it indicates that user parameter has been set to null during logout,
   * and we don't need to proceed with logout.
   * <p>
   * Then are adding message that user left the chat
   * and checking if VaadinServletRequest is not null, because it can be null when user is
   * logging out from the chat due to session timeout. (Session scoped)
   * Explicit logout is not needed when session closed
   * Spring Security will do it for us.
   * <p>
   * If VaadinServletRequest is not null, we are proceeding with logout.
   * <p>
   * Then we are adding a message to the chat, that user left.
   *
   * @param user is the user who left the chat
   */
  public void logout(String user) {
    if (StringUtils.hasText(user)) {
      storage.addMessageUserPresence(String.format(USER_LEFT_MSG, user));
      HttpServletRequest request = VaadinServletRequest.getCurrent();
      if (request != null) {
        request = VaadinServletRequest.getCurrent().getHttpServletRequest();
        logout(request, null, null);
      }
    }
  }
}
