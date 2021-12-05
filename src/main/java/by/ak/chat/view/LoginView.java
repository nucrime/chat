package by.ak.chat.view;

import by.ak.chat.util.DateTimeProvider;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import static by.ak.chat.view.ChatView.TITLE;

@Route(LoginView.PATH)
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

  public static final String PATH = "/login";
  protected LoginForm login = new LoginForm();
  private final DateTimeProvider dateTimeProvider;

  public LoginView(DateTimeProvider dateTimeProvider) {
    this.dateTimeProvider = dateTimeProvider;

    addClassName("login-view");
    setSizeFull();

    setJustifyContentMode(JustifyContentMode.CENTER);
    setAlignItems(Alignment.CENTER);

    login.setAction("login");
    login.addForgotPasswordListener(event -> {
      getUI().ifPresent(ui -> ui.navigate(ForgotPasswordView.PATH));
      });
    add(new H1(TITLE), login);
    add(new Button("Register", e -> getUI().ifPresent(ui -> ui.navigate(RegistrationView.PATH))));
  }

  @Override
  public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    if(beforeEnterEvent.getLocation()
      .getQueryParameters()
      .getParameters()
      .containsKey("error")) {
      login.setError(true);
    }
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    initializeTimeOffset();
  }

  private void initializeTimeOffset() {
    getUI().ifPresent(uiElement -> {
      uiElement.getPage().retrieveExtendedClientDetails(extendedClientDetails -> {
        dateTimeProvider.timeOffset = extendedClientDetails.getTimezoneOffset();
      });
    });
  }
}
