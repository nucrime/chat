package by.ak.chat.component;

import by.ak.chat.security.service.SecurityService;
import by.ak.chat.service.UserService;
import by.ak.chat.view.chat.ChatSelectView;
import by.ak.chat.view.chat.ChatView;
import by.ak.chat.view.user.UserManagementView;
import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    var link = new RouterLink();
    link.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
    link.setRoute(menuItemInfo.view());

    var icon = new Span();
    icon.addClassNames("me-s", "text-l");
    if (!menuItemInfo.iconClass().isEmpty()) {
      icon.addClassNames(menuItemInfo.iconClass());
    }

    var text = new Span(menuItemInfo.text());
    text.addClassNames("font-medium", "text-s");

    link.add(icon, text);
    return link;
  }

  private Component createHeaderContent() {
    var toggle = new DrawerToggle();
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
    var appName = new H2("FUARGA");
    appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");

    com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
      createNavigation(), createThemeButton(), createFooter());
    section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
    return section;
  }

  private Nav createNavigation() {
    var nav = new Nav();
    nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
    nav.getElement().setAttribute("aria-labelledby", "views");

    // Wrap the links in a list; improves accessibility
    var list = new UnorderedList();
    list.addClassNames("list-none", "m-0", "p-0");
    nav.add(list);

    createLinks()
      .forEach(link -> list.add(new ListItem(link)));
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
      new MenuItemInfo(USER_MANAGEMENT, "la la-file", UserManagementView.class),
      new MenuItemInfo(CHAT, "la la-columns", ChatView.class),
      new MenuItemInfo(CHATS, "la la-columns", ChatSelectView.class));
    var links = new ArrayList<RouterLink>();
    menuItems.forEach(menuItem -> {
      links.add(createLink(menuItem));
    });
    return links;
  }

  private Footer createFooter() {
    var layout = new Footer();
    layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");

    var maybeUser = userService.find(securityService.getLoggedInUserName());
    if (maybeUser.isPresent()) {
      var user = maybeUser.get();

      var avatar = new Avatar(user.getUsername(), null);
      avatar.addClassNames("me-xs");
      if (Objects.nonNull(user.getAvatar())) {
        avatar.setImageResource(streamResourceForBytes(user.getAvatar()));
      }

      var userMenu = new ContextMenu(avatar);
      userMenu.setOpenOnClick(true);
      userMenu.addItem("Logout", e -> {
        securityService.logout();
      });

      var name = new Span(user.getUsername());
      name.addClassNames("font-medium", "text-s", "text-secondary");

      layout.add(avatar, name);
    } else {
      Anchor loginLink = new Anchor("login", "Sign in");
      layout.add(loginLink);
    }

    return layout;
  }

  // remove as this is a duplicate of the method in the UserEditor
  @SneakyThrows
  private AbstractStreamResource streamResourceForBytes(byte[] bytes) {
    @Cleanup var stream = new ByteArrayInputStream(bytes);
    return new StreamResource("nobodycares", () -> stream);
  }

  @Override
  protected void afterNavigation() {
    super.afterNavigation();
    viewTitle.setText(getCurrentPageTitle());
  }

  private String getCurrentPageTitle() {
    var pageTitle = "";

    // Get list of current views, the first view is the top view.
    var views = UI.getCurrent().getInternals().getActiveRouterTargetsChain();
    if (views.size() > 0) {
      var view = views.get(0);

      // If the view has a dynamic title we'll use that
      if (view instanceof HasDynamicTitle v) {
        pageTitle = v.getPageTitle();
      } else {
        // It does not have a dynamic title. Try to read title from
        // annotations
        var pt = getContent().getClass().getAnnotation(PageTitle.class);
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
