package by.ak.chat.view;

import by.ak.chat.model.User;
import by.ak.chat.service.UserService;
import by.ak.chat.component.Header;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle(UserView.TITLE)
@Route(UserView.PATH)
public class UserView extends VerticalLayout {

  public static final String PATH = "/users";
  public static final String TITLE = "Users Management";
  private final UserService userService;
  private final Header header;
  final Grid<User> grid;

  public UserView(UserService userService, Header header) {
    this.userService = userService;
    this.header = header;
    add(header.init());
    grid = new Grid<>(User.class);
    add(grid);
    grid.setColumns("id", "username", "email", "firstName", "lastName", "role", "created");
    grid.setItems(userService.findAll());
  }
}

