package by.ak.chat.security;

//import by.ak.chat.model.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static by.ak.chat.view.ChatSelectView.PATH;

@Component
@RequiredArgsConstructor
public class ChatRedirectingAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//  private static final String USER_JOINED_MSG = "**%s** joined";
  private final SecurityService securityService;
//  private final Storage storage;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
    AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
    String user = securityService.getLoggedInUserName();
    //storage.addMessageUserPresence(String.format(USER_JOINED_MSG, user)); // moved to chat selector view
    httpServletResponse.sendRedirect(PATH); // todo migrate to v2
//    httpServletResponse.sendRedirect(ChatViewV2.PATH);
  }
}
