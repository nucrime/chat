package by.ak.chat.component;

import by.ak.chat.model.User;
import by.ak.chat.security.SecurityService;
import by.ak.chat.service.UserService;
import by.ak.chat.view.ChatSelectView;
import by.ak.chat.view.ChatView;
import by.ak.chat.view.UserView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.ak.chat.view.ChatView.LOG_OUT;

@Service
@SessionScope
@PageTitle("FUAGRA")
public class Header extends AppLayout implements RouterLayout {
  public static final String CHAT = "Chat";
  public static final String CHATS = "Chat list";
  public static final String USER_MANAGEMENT = "User Management";
  private H1 viewTitle;
  private final SecurityService securityService;
  private final UserService userService;

  private record MenuItemInfo (
    String text,
    String iconClass,
    Class<? extends Component> view){}

  public Header(SecurityService securityService, UserService userService) {
    this.securityService = securityService;
    this.userService = userService;

    var container = new VerticalLayout();
    var header = new HorizontalLayout();

    setPrimarySection(Section.DRAWER);

    addToDrawer(createDrawerContent());

    var darkTheme = new Button(VaadinIcon.MOON_O.create(), click -> {
      var themeList = UI.getCurrent().getElement().getThemeList();

      if (themeList.contains(Lumo.DARK)) {
        themeList.remove(Lumo.DARK);
        click.getSource().setIcon(VaadinIcon.MOON_O.create());
      } else {
        themeList.add(Lumo.DARK);
        click.getSource().setIcon(VaadinIcon.MOON.create());
      }
    });

    header.add(darkTheme);

    var logoutButton = new Button(LOG_OUT, e -> securityService.logout());
    header.add(logoutButton);

    container.setHeight(10, Unit.PERCENTAGE);
    container.setAlignItems(FlexComponent.Alignment.END);
    container.add(header);

    addToNavbar(true, createHeaderContent());
    addToNavbar(container);

    this.setDrawerOpened(false);
  }
/* Todo revise and remove. Old UI
  public VerticalLayout init() {
    var container = new VerticalLayout();
    var header = new HorizontalLayout();
    var current = UI.getCurrent();

    var management = new Button(USER_MANAGEMENT, e -> current.navigate(UserView.PATH));
    header.add(management);

    var chatButton = new Button(CHAT, e -> current.navigate(ChatView.PATH));
    var chatList = new Button(CHATS, e -> {
      current.navigate(ChatSelectView.PATH);
    });
    header.add(chatButton);
    header.add(chatList);

    var darkTheme = new Button(VaadinIcon.MOON_O.create(), click -> {
      var themeList = UI.getCurrent().getElement().getThemeList();

      if (themeList.contains(Lumo.DARK)) {
        themeList.remove(Lumo.DARK);
        click.getSource().setIcon(VaadinIcon.MOON_O.create());
      } else {
        themeList.add(Lumo.DARK);
        click.getSource().setIcon(VaadinIcon.MOON.create());
      }
    });

    header.add(darkTheme);

    var logoutButton = new Button(LOG_OUT, e -> securityService.logout());
    header.add(logoutButton);

    container.setHeight(10, Unit.PERCENTAGE);
    container.setAlignItems(FlexComponent.Alignment.END);
    container.add(header);

    return container;
  }*/

  private Component createHeaderContent() {
    DrawerToggle toggle = new DrawerToggle();
    toggle.addClassName("text-secondary");
    toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
    toggle.getElement().setAttribute("aria-label", "Menu toggle");

    // todo add dynamic title
    viewTitle = new H1();
    viewTitle.addClassNames("m-0", "text-l");

    com.vaadin.flow.component.html.Header header = new com.vaadin.flow.component.html.Header(toggle
//      , viewTitle
    );
    header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", "h-xl", "items-center",
      "w-full");
    return header;
  }

  private Component createDrawerContent() {
    H2 appName = new H2("FUARGA");
    appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");

    com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
      createNavigation(), createFooter());
    section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
    return section;
  }

  private Nav createNavigation() {
    Nav nav = new Nav();
    nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
    nav.getElement().setAttribute("aria-labelledby", "views");

    // Wrap the links in a list; improves accessibility
    UnorderedList list = new UnorderedList();
    list.addClassNames("list-none", "m-0", "p-0");
    nav.add(list);

    for (RouterLink link : createLinks()) {
      ListItem item = new ListItem(link);
      list.add(item);
    }
    return nav;
  }

  private List<RouterLink> createLinks() {
    /*
    *     var management = new Button(USER_MANAGEMENT, e -> current.navigate(UserView.PATH));
    header.add(management);

    var chatButton = new Button(CHAT, e -> current.navigate(ChatView.PATH));
    var chatList = new Button(CHATS, e -> {
      current.navigate(ChatSelectView.PATH);
    });
    * */
    var menuItems = List.of(
      new MenuItemInfo(USER_MANAGEMENT, "la la-columns", UserView.class),
      new MenuItemInfo(CHAT, "la la-columns", ChatView.class),
      new MenuItemInfo(CHATS, "la la-columns", ChatSelectView.class));
    List<RouterLink> links = new ArrayList<>();
    menuItems.forEach(menuItem -> {
      links.add(createLink(menuItem));
    });
//    for (MenuItemInfo menuItemInfo : menuItems) {
//      if (accessChecker.hasAccess(menuItemInfo.getView())) {
//        links.add(createLink(menuItemInfo));
//      }
//    }
    return links;
  }

  private Footer createFooter() {
    Footer layout = new Footer();
    layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");

    Optional<User> maybeUser = userService.find(securityService.getLoggedInUserName());
    if (maybeUser.isPresent()) {
      User user = maybeUser.get();

      Avatar avatar = new Avatar(user.getUsername(), null); // avatar is currently not used
      avatar.addClassNames("me-xs");

      ContextMenu userMenu = new ContextMenu(avatar);
      userMenu.setOpenOnClick(true);
      userMenu.addItem("Logout", e -> {
        securityService.logout();
      });

      Span name = new Span(user.getUsername());
      name.addClassNames("font-medium", "text-s", "text-secondary");

      layout.add(avatar, name);
    } else {
      Anchor loginLink = new Anchor("login", "Sign in");
      layout.add(loginLink);
    }

    return layout;
  }

  private static RouterLink createLink(MenuItemInfo menuItemInfo) {
    RouterLink link = new RouterLink();
    link.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
    link.setRoute(menuItemInfo.view());

    Span icon = new Span();
    icon.addClassNames("me-s", "text-l");
    if (!menuItemInfo.iconClass().isEmpty()) {
      icon.addClassNames(menuItemInfo.iconClass());
    }

    Span text = new Span(menuItemInfo.text());
    text.addClassNames("font-medium", "text-s");

    link.add(icon, text);
    return link;
  }

  @Override
  protected void afterNavigation() {
    super.afterNavigation();
    viewTitle.setText(getCurrentPageTitle());
  }

  private String getCurrentPageTitle() {
    PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
    return title == null ? "" : title.value();
  }
}
