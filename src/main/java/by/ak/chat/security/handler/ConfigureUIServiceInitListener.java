package by.ak.chat.security.handler;

import by.ak.chat.security.config.SecurityUtils;
import by.ak.chat.view.user.LoginView;
import by.ak.chat.view.user.RegistrationView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

  @Override
  public void serviceInit(ServiceInitEvent event) {
    event.getSource().addUIInitListener(uiEvent -> {
      final UI ui = uiEvent.getUI();
      ui.addBeforeEnterListener(this::authenticateNavigation);
    });
  }

  /*
  Adds redirect to login in case if user is not authenticated
  * */
  // todo looks like a candidate to be refactored as switch java17
  private void authenticateNavigation(BeforeEnterEvent event) {
    // Enable login view for anonymous users
    if (!(LoginView.class.equals(event.getNavigationTarget())
      // Enable registration view for anonymous users
      || RegistrationView.class.equals(event.getNavigationTarget()))
      && !SecurityUtils.isUserLoggedIn()) {
      event.rerouteTo(LoginView.class);
    }
  }
}
