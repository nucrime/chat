package by.ak.chat.component;

import by.ak.chat.security.SecurityService;
import by.ak.chat.view.ChatView;
import by.ak.chat.view.UserView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import static by.ak.chat.view.ChatView.LOG_OUT;

@Service
@SessionScope
@RequiredArgsConstructor
// todo extends AppLayout implements RouterLayout looks like bad practice it's not a main layout and should be placed somewhere else
public class Header extends AppLayout implements RouterLayout {
  public static final String CHAT = "Chat";
  private final SecurityService securityService;

  public VerticalLayout init() {
    VerticalLayout container = new VerticalLayout();
    HorizontalLayout header = new HorizontalLayout();
    UI current = UI.getCurrent();

    if (securityService.isAdmin()) {
      Button management = new Button("Users management", e -> current.navigate(UserView.PATH));
      header.add(management);
    }

    Button chatButton = new Button(CHAT, e -> current.navigate(ChatView.PATH));
    header.add(chatButton);

    Button darkTheme = new Button(VaadinIcon.MOON_O.create(), click -> {
      ThemeList themeList = UI.getCurrent().getElement().getThemeList();

      if (themeList.contains(Lumo.DARK)) {
        themeList.remove(Lumo.DARK);
        click.getSource().setIcon(VaadinIcon.MOON_O.create());
      } else {
        themeList.add(Lumo.DARK);
        click.getSource().setIcon(VaadinIcon.MOON.create());
      }
    });

    header.add(darkTheme);

    Button logoutButton = new Button(LOG_OUT, e -> securityService.logout());
    header.add(logoutButton);

    container.setHeight(10, Unit.PERCENTAGE);
    container.setAlignItems(FlexComponent.Alignment.END);
    container.add(header);

    return container;
  }
}
