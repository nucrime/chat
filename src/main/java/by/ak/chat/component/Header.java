package by.ak.chat.component;

import by.ak.chat.security.SecurityService;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import static by.ak.chat.view.ChatView.LOG_OUT;

@Service
@SessionScope
@RequiredArgsConstructor
public class Header {
  private final SecurityService securityService;

  public VerticalLayout init() {
    Button logoutButton = new Button(LOG_OUT, e -> securityService.logout());
    VerticalLayout header = new VerticalLayout();
    header.add(logoutButton);
    header.setHeight(10, Unit.PERCENTAGE);
    header.setAlignItems(FlexComponent.Alignment.END);
    return header;
  }
}
