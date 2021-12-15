package by.ak.chat.view;

import by.ak.chat.component.Header;
import by.ak.chat.model.Storage;
import by.ak.chat.security.SecurityService;
import by.ak.chat.util.ChatSelector;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import static by.ak.chat.view.ChatSelectView.PATH;
import static by.ak.chat.view.ChatSelectView.TITLE;
import static org.springframework.util.StringUtils.hasText;

@Route(PATH)
@PageTitle(TITLE)
public class ChatSelectView extends VerticalLayout {
  public static final String PATH = "/select";
  public static final String TITLE = "FUAGRA";
  private static final String USER_JOINED_MSG = "**%s** joined";

  private final ChatSelector chatSelector;
  private final Header header;
  private final Storage storage;
  private final SecurityService securityService;

  public ChatSelectView(ChatSelector chatSelector, Header header, Storage storage, SecurityService securityService) {
    this.chatSelector = chatSelector;
    this.header = header;
    this.storage = storage;
    this.securityService = securityService;

    String user = securityService.getLoggedInUserName();

    add(header.init());
    H3 title = new H3("Available chats");
    add(title);
    storage.chats().map(chat -> new Button(chat, e -> {
      selectAndNavigate(chatSelector, chat, user);
    })).forEach(this::add);

    TextField newChatName = new TextField();
    newChatName.setWidthFull();
    Button sendButton = new Button("Join/Create", e -> {
      if (hasText(newChatName.getValue())) {
        selectAndNavigate(chatSelector, newChatName.getValue(), user);
      }
    });
    sendButton.addClickShortcut(Key.ENTER);
    sendButton.setWidthFull();
    HorizontalLayout newChatLayout = new HorizontalLayout(newChatName, sendButton);

    add(newChatLayout);

    this.setAlignItems(Alignment.CENTER);
  }

  private void selectAndNavigate(ChatSelector chatSelector, String chat, String user) {
    chatSelector.select(chat);
    storage.addMessageUserPresence(String.format(USER_JOINED_MSG, user));
    UI.getCurrent().navigate(ChatView.PATH);
  }
}
