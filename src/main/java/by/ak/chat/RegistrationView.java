package by.ak.chat;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(RegistrationView.PATH)
public class RegistrationView extends VerticalLayout {

  public static final String PATH = "/register";

  public RegistrationView() {
    RegistrationForm registrationForm = new RegistrationForm();
    // Center the RegistrationForm
    setHorizontalComponentAlignment(Alignment.CENTER, registrationForm);

    add(registrationForm);

    RegistrationFormBinder registrationFormBinder = new RegistrationFormBinder(registrationForm);
    registrationFormBinder.addBindingAndValidation();
  }
}
