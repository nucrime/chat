package by.ak.chat;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("/")
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

  private LoginForm login = new LoginForm();

  public LoginView() {
    addClassName("login-view");
    setSizeFull();

    setJustifyContentMode(JustifyContentMode.CENTER);
    setAlignItems(Alignment.CENTER);

    login.setAction("chat");
    add(new H1("Chat"), login);
    add(new Button("Register", e -> getUI().ifPresent(ui -> ui.navigate("register"))));
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
}
