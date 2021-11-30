package by.ak.chat;

import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.stream.Stream;

public class RegistrationForm extends FormLayout {

  private H3 title;

  private TextField firstName;
  private TextField lastName;

  private EmailField email;

  private PasswordField password;
  private PasswordField passwordConfirm;

  private Span errorMessageField;

  private Button submitButton;


  public RegistrationForm() {
    title = new H3("Sign up");
    firstName = new TextField("First name");
    lastName = new TextField("Last name");
    email = new EmailField("Email");

    password = new PasswordField("Password");
    passwordConfirm = new PasswordField("Confirm password");

    setRequiredIndicatorVisible(firstName, lastName, email, password,
      passwordConfirm);

    errorMessageField = new Span();

    submitButton = new Button("Register");
    submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

    add(title, firstName, lastName, email, password,
      passwordConfirm, errorMessageField,
      submitButton);

    // Max width of the Form
    setMaxWidth("500px");

    // Allow the form layout to be responsive.
    // On device widths 0-490px we have one column.
    // Otherwise, we have two columns.
    setResponsiveSteps(
      new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
      new ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP));

    // These components always take full width
    setColspan(title, 2);
    setColspan(email, 2);
    setColspan(errorMessageField, 2);
    setColspan(submitButton, 2);
  }

  public PasswordField getPasswordField() {
    return password;
  }

  public PasswordField getPasswordConfirmField() {
    return passwordConfirm;
  }

  public Span getErrorMessageField() {
    return errorMessageField;
  }

  public Button getSubmitButton() {
    return submitButton;
  }

  private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
    Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
  }
}
