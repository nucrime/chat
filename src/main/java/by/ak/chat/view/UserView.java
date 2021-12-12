package by.ak.chat.view;

import by.ak.chat.component.Header;
import by.ak.chat.component.UserEditor;
import by.ak.chat.model.User;
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

import static org.springframework.util.StringUtils.hasText;

@PageTitle(UserView.TITLE)
@Route(UserView.PATH)
public class UserView extends VerticalLayout {

  public static final String PATH = "/users";
  public static final String TITLE = "Users Management";
  private final UserService userService;
  private final UserEditor editor;
  private final Header header;
  final Grid<User> grid;

  public UserView(UserService userService, UserEditor editor, Header header) {
    this.userService = userService;
    this.editor = editor;
    this.header = header;

    add(header.init());
    grid = new Grid<>(User.class);
    grid.setColumns("id", "username", "email", "firstName", "lastName", "role", "created");

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
    });

    // Initialize listing
    listCustomers(null);
  }

  // Filter based on a full text search
  private void listCustomers(String filterText) {
    if (!hasText(filterText)) {
      grid.setItems(userService.findAll());
    } else {
      grid.setItems(userService.findByLastName(filterText));
    }
  }
}

