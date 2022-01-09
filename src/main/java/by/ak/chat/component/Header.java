package by.ak.chat.component;

import by.ak.chat.model.User;
import by.ak.chat.security.SecurityService;
import by.ak.chat.service.UserService;
import by.ak.chat.view.ChatSelectView;
import by.ak.chat.view.ChatView;
import by.ak.chat.view.UserView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@SessionScope
@PageTitle("FUAGRA")
public class Header extends AppLayout implements RouterLayout {
  public static final String CHAT = "Chat";
  public static final String CHATS = "Chat list";
  public static final String USER_MANAGEMENT = "User Management";
  public static final String DARK_MODE = "Dark mode";
  private final SecurityService securityService;
  private final UserService userService;
  private H2 viewTitle;

  public Header(SecurityService securityService, UserService userService) {
    this.securityService = securityService;
    this.userService = userService;

    setPrimarySection(Section.DRAWER);
    addToDrawer(createDrawerContent());
    addToNavbar(true, createHeaderContent());
    this.setDrawerOpened(false);
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

  private Component createHeaderContent() {
    DrawerToggle toggle = new DrawerToggle();
    toggle.addClassName("text-secondary");
    toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
    toggle.getElement().setAttribute("aria-label", "Menu toggle");

    viewTitle = new H2();
    // position the title in the middle of the header
    viewTitle.getStyle()
      .set("font-size", "var(--lumo-font-size-l)")
      .set("margin", "0");

    var header = new HorizontalLayout();
    header.setId("header");
    header.setWidthFull();
    header.setSpacing(false);
    header.setAlignItems(FlexComponent.Alignment.CENTER);
    header.add(toggle);
    header.add(viewTitle);
    return header;
  }

  private Component createDrawerContent() {
    H2 appName = new H2("FUARGA");
    appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");

    com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
      createNavigation(), createThemeButton(), createFooter());
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

  private VerticalLayout createThemeButton() {
    var layout = new VerticalLayout();
    var darkTheme = new Button(DARK_MODE, VaadinIcon.MOON_O.create(), click -> {
      var themeList = UI.getCurrent().getElement().getThemeList();

      if (themeList.contains(Lumo.DARK)) {
        themeList.remove(Lumo.DARK);
        click.getSource().setIcon(VaadinIcon.MOON_O.create());
      } else {
        themeList.add(Lumo.DARK);
        click.getSource().setIcon(VaadinIcon.MOON.create());
      }
    });
    layout.add(darkTheme);
    return layout;
  }

  private List<RouterLink> createLinks() {
    var menuItems = List.of(
      new MenuItemInfo(USER_MANAGEMENT, "la la-file", UserView.class),
      new MenuItemInfo(CHAT, "la la-columns", ChatView.class),
      new MenuItemInfo(CHATS, "la la-columns", ChatSelectView.class));
    List<RouterLink> links = new ArrayList<>();
    menuItems.forEach(menuItem -> {
      links.add(createLink(menuItem));
    });
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

  @Override
  protected void afterNavigation() {
    super.afterNavigation();
    viewTitle.setText(getCurrentPageTitle());
  }

  private String getCurrentPageTitle() {
    String pageTitle = "";

    // Get list of current views, the first view is the top view.
    List<HasElement> views = UI.getCurrent().getInternals().getActiveRouterTargetsChain();
    if (views.size() > 0) {
      HasElement view = views.get(0);

      // If the view has a dynamic title we'll use that
      if (view instanceof HasDynamicTitle) {
        pageTitle = ((HasDynamicTitle) view).getPageTitle().toString();
      } else {
        // It does not have a dynamic title. Try to read title from
        // annotations
        PageTitle pt = getContent().getClass().getAnnotation(PageTitle.class);
        pageTitle = pt == null ? "" : pt.value();
      }
    }
    return pageTitle;
  }

  private record MenuItemInfo(
    String text,
    String iconClass,
    Class<? extends Component> view) {
  }
}
