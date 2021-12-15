package by.ak.chat.view;

import by.ak.chat.component.Header;
import by.ak.chat.model.Storage;
import by.ak.chat.util.ChatSelector;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import static by.ak.chat.view.ChatSelectView.PATH;
import static by.ak.chat.view.ChatSelectView.TITLE;

@Route(PATH)
@PageTitle(TITLE)
public class ChatSelectView extends VerticalLayout {
  public static final String PATH = "/select";
  public static final String TITLE = "FUAGRA";

  private final ChatSelector chatSelector;
  private final Header header;
  private final Storage storage;

  public ChatSelectView(ChatSelector chatSelector, Header header, Storage storage) {
    this.chatSelector = chatSelector;
    this.header = header;
    this.storage = storage;

    add(header.init());
    storage.chats().map(chat -> new Button(chat, (e) -> {
      chatSelector.select(chat);
      UI.getCurrent().navigate(ChatView.PATH);
    })).forEach(this::add);


  }
}
