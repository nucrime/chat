package by.ak.chat.view;

import by.ak.chat.util.DateTimeProvider;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;

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
  private final DateTimeProvider dateTimeProvider;
  protected LoginForm login = new LoginForm();

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
    var error = beforeEnterEvent.getLocation()
      .getQueryParameters()
      .getParameters()
      .containsKey(ERROR);
    if (error) {
      var vaadinServletRequest = VaadinServletRequest.getCurrent();
      var httpServletRequest = vaadinServletRequest.getHttpServletRequest();
      var session = httpServletRequest.getSession();
      var ex = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
      if (ex == null) {
        login.setError(false);
      } else {
        setError(ex.getMessage());
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
      }
    }
  }

  private void setError(String msg) {
    var defaultLoginForm = LoginI18n.createDefault();
    var errorMessage = new LoginI18n.ErrorMessage();
    errorMessage.setMessage(msg);
    defaultLoginForm.setErrorMessage(errorMessage);
    login.setI18n(defaultLoginForm);
    login.setError(true);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    initializeTimeOffset();
  }

  private void initializeTimeOffset() {
    getUI().ifPresent(uiElement -> {
      var page = uiElement.getPage();
      page.retrieveExtendedClientDetails(extendedClientDetails -> {
        dateTimeProvider.timeOffset = extendedClientDetails.getTimezoneOffset();
      });
      page.executeJs(RETRIEVE_USER_TIMEZONE)
        .then(String.class, result -> dateTimeProvider.zoneId = ZoneId.of(result));
    });
  }
}
