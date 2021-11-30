package by.ak.chat;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("register")
public class RegistrationView extends VerticalLayout {

  public RegistrationView() {
    RegistrationForm registrationForm = new RegistrationForm();
    // Center the RegistrationForm
    setHorizontalComponentAlignment(Alignment.CENTER, registrationForm);

    add(registrationForm);

    RegistrationFormBinder registrationFormBinder = new RegistrationFormBinder(registrationForm);
    registrationFormBinder.addBindingAndValidation();
  }
}
