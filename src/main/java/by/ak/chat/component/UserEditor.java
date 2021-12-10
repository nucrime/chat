package by.ak.chat.component;

import by.ak.chat.service.UserService;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;

import java.awt.*;

@SpringComponent
@UIScope
@RequiredArgsConstructor
public class UserEditor extends VerticalLayout implements KeyNotifier {
  private final UserService userService;

  TextField username = new TextField("Username");
  TextField password = new TextField("Email");
  TextField firstName = new TextField("First name");
  TextField lastName = new TextField("Last name");
  TextField role = new TextField("Role");

  private Button save = new Button("Save");

}
