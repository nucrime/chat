package by.ak.chat.view;

import by.ak.chat.form.RegistrationForm;
import by.ak.chat.form.RegistrationFormBinder;
import by.ak.chat.service.UserService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.stereotype.Service;

@Route(RegistrationView.PATH)
@Service
public class RegistrationView extends VerticalLayout {

  public static final String PATH = "/register";
  private final UserService userService;

  public RegistrationView(UserService userService) {
    this.userService = userService;
    RegistrationForm registrationForm = new RegistrationForm();
    /*
    * Center the RegistrationForm
    */
    setHorizontalComponentAlignment(Alignment.CENTER, registrationForm);

    /*
    * Add to vertical layout
    */
    add(registrationForm);

    /*
    * Bind and validate registration form fields
    */
    RegistrationFormBinder registrationFormBinder = new RegistrationFormBinder(registrationForm, userService);
    registrationFormBinder.addBindingAndValidation();
  }
}
