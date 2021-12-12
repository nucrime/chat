package by.ak.chat.component;

import by.ak.chat.model.User;
import by.ak.chat.service.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;


@Component
@UIScope
public class UserEditor extends VerticalLayout implements KeyNotifier {
  private final UserService userService;
  private User user;

  TextField username = new TextField("Username");
  TextField email = new TextField("Email");
  TextField firstName = new TextField("First name");
  TextField lastName = new TextField("Last name");
  TextField role = new TextField("Role");
  TextField password = new TextField("Password");

  private Button save = new Button("Save", VaadinIcon.CHECK.create());
  private Button cancel = new Button("Cancel", VaadinIcon.CLOSE_SMALL.create());
  private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
  private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

  private Binder<User> binder = new Binder<>(User.class);
  private ChangeHandler changeHandler;

  public UserEditor(UserService userService) {
    this.userService = userService;
    HorizontalLayout firstRow = new HorizontalLayout();
    firstRow.add(firstName, lastName, username);
    HorizontalLayout secondRow = new HorizontalLayout();
    secondRow.add(email, role, password);
    HorizontalLayout thirdRow = new HorizontalLayout();
    thirdRow.add(actions);
    add(firstRow, secondRow, thirdRow);

    // bind using naming convention
    binder.bindInstanceFields(this);

    // Configure and style components
    setSpacing(true);

    save.getElement().getThemeList().add("primary");
    delete.getElement().getThemeList().add("error");

    addKeyPressListener(Key.ENTER, e -> save());

    // wire action buttons to save, delete and reset
    save.addClickListener(e -> save());
    delete.addClickListener(e -> delete());
    cancel.addClickListener(e -> editCustomer(user));
    setVisible(false);
  }

  void delete() {
    userService.delete(user);
    changeHandler.onChange();
  }

  void save() {
    userService.save(user);
    changeHandler.onChange();
  }

  public interface ChangeHandler {
    void onChange();
  }

  public final void editCustomer(User u) {
    // if user is null, don't show form and actions
    if (u == null) {
      setVisible(false);
      return;
    }
    final boolean persisted = u.getId() != null;
    if (persisted) {
      // Find fresh entity for editing
      user = userService.findById(u.getId()).get();
    } else {
      user = u;
    }
    cancel.setVisible(persisted);

    // Bind customer properties to similarly named fields
    // Could also use annotation or "manual binding" or programmatically
    // moving values from fields to entities before saving
    user.setPassword(null); // it's bcrypt, so we don't need to show it

    binder.setBean(user);

    setVisible(true);

    // Focus first name initially
    firstName.focus();
  }

  public void setChangeHandler(ChangeHandler h) {
    // ChangeHandler is notified when either save or delete
    // is clicked
    changeHandler = h;
  }

}
