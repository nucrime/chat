package by.ak.chat.security;

import by.ak.chat.view.LoginView;
import by.ak.chat.view.RegistrationView;
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
