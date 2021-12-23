package by.ak.chat.view;

import by.ak.chat.component.Header;
import by.ak.chat.component.UserEditor;
import by.ak.chat.model.User;
import by.ak.chat.security.SecurityService;
import by.ak.chat.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collections;
import java.util.Objects;

import static org.springframework.util.StringUtils.hasText;

@PageTitle(UserView.TITLE)
@Route(UserView.PATH)
public class UserView extends VerticalLayout {

  public static final String PATH = "/users";
  public static final String TITLE = "Users Management";
  public static final String ERROR_MSG = "Error occurred. Please contact administrator";
  private final UserService userService;
  private final SecurityService securityService;
  private final UserEditor editor;
  private final Header header;
  final Grid<User> grid;
  private User currentUser;

  public UserView(UserService userService, SecurityService securityService, UserEditor editor, Header header) {
    this.userService = userService;
    this.securityService = securityService;
    this.editor = editor;
    this.header = header;

    add(header.init());
    grid = new Grid<>(User.class);
    grid.setColumns("id", "username", "email", "firstName", "lastName", "role", "created");
    // todo fix rendering of created date. time always changes unexpectedly, render as in chatview

    Button addNewBtn = new Button("Add new user", VaadinIcon.PLUS.create(), event -> editor.editCustomer(new User()));

    TextField filter = new TextField();
    filter.setPlaceholder("Filter by last name");

    // Hook logic to components
    // Replace listing with filtered content when user changes filter
    filter.setValueChangeMode(ValueChangeMode.EAGER);
    filter.addValueChangeListener(e -> listCustomers(e.getValue()));

    HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);

    add(actions, grid, editor);

    // Connect selected Customer to editor or hide if none is selected
    grid.asSingleSelect().addValueChangeListener(e -> {
      editor.editCustomer(e.getValue());
    });

    // Listen changes made by the editor, refresh data from backend
    editor.setChangeHandler(() -> {
      editor.setVisible(false);
      listCustomers(filter.getValue());
      if (!securityService.isAdmin()) {
        getUI().ifPresent(ui -> ui.navigate(ChatView.PATH)); // navigate to chat view if user is not admin
      }
    });

    // Initialize listing
    listCustomers(null);

    if (!securityService.isAdmin()) {
      grid.setVisible(false);
      actions.setVisible(false);
      grid.select(getCurrentUser());
    }
  }

  // Filter based on a full text search
  private void listCustomers(String filterText) {
    if (!hasText(filterText)) {
      if (securityService.isAdmin()) {
        grid.setItems(userService.findAll());
      } else if (!securityService.isAdmin()) {
        grid.setItems(Collections.singletonList(getCurrentUser()));
      }
    } else {
      grid.setItems(userService.findByLastName(filterText));
    }
  }

  private User getCurrentUser() {
    if (Objects.isNull(currentUser)) {
      currentUser = userService.find(securityService.getLoggedInUserName()).get();
    }
    return currentUser;
  }
}

