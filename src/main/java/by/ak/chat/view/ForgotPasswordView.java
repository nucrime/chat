package by.ak.chat.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import static by.ak.chat.view.ChatView.TITLE;

// todo similar to login view, but of course without login and forgot password buttons
//  instead, send password reset email ->
//        call notification service which sends a generated link
//        to reset password to user if email was found in a database
@Route(ForgotPasswordView.PATH)
@PageTitle("Forgot Password")
public class ForgotPasswordView extends LoginView { // do we need it? extend from Login view
  public static final String PATH = "/forgotPassword";
    public ForgotPasswordView() {
      addClassName("forgot-password-view");
      setSizeFull();

      setJustifyContentMode(JustifyContentMode.CENTER);
      setAlignItems(Alignment.CENTER);
      login.setForgotPasswordButtonVisible(false);

      login.setAction("forgotPassword");
      login.addForgotPasswordListener(event -> {
        getUI().ifPresent(ui -> ui.navigate(ForgotPasswordView.PATH));
      });
      add(new H1("труляля"), login);
    }
}
