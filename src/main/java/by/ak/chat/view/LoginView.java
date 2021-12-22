package by.ak.chat.view;

import by.ak.chat.util.DateTimeProvider;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.ZoneId;

@Route(LoginView.PATH)
@PageTitle(LoginView.TITLE)
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

  public static final String PATH = "/login";
  public static final String LOGIN = "login";
  public static final String REGISTER = "Register";
  public static final String ERROR = "error";
  public static final String RETRIEVE_USER_TIMEZONE = "return Intl.DateTimeFormat().resolvedOptions().timeZone";
  public static final String LOGIN_VIEW = "login-view";
  public static final String TITLE = "Login";

  protected LoginForm login = new LoginForm();
  private final DateTimeProvider dateTimeProvider;

  public LoginView(DateTimeProvider dateTimeProvider) {
    this.dateTimeProvider = dateTimeProvider;

    addClassName(LOGIN_VIEW);
    setSizeFull();

    setJustifyContentMode(JustifyContentMode.CENTER);
    setAlignItems(Alignment.CENTER);

    login.setAction(LOGIN);
    login.addForgotPasswordListener(event -> {
      getUI().ifPresent(ui -> ui.navigate(ForgotPasswordView.PATH));
    });
    login.setForgotPasswordButtonVisible(false); //todo create forgot password view
    add(new H1(ChatView.TITLE), login);
    add(new Button(REGISTER, e -> getUI().ifPresent(ui -> ui.navigate(RegistrationView.PATH))));
  }

  @Override
  public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    if (beforeEnterEvent.getLocation()
      .getQueryParameters()
      .getParameters()
      .containsKey(ERROR)) {
      login.setError(true);
    }
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    initializeTimeOffset();
  }

  private void initializeTimeOffset() {
    getUI().ifPresent(uiElement -> {
      Page page = uiElement.getPage();
      page.retrieveExtendedClientDetails(extendedClientDetails -> {
        dateTimeProvider.timeOffset = extendedClientDetails.getTimezoneOffset();
      });
      page.executeJs(RETRIEVE_USER_TIMEZONE)
        .then(String.class, result -> dateTimeProvider.zoneId = ZoneId.of(result));
    });
  }
}
