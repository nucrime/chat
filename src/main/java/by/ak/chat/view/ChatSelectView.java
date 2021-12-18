package by.ak.chat.view;

import by.ak.chat.component.Header;
import by.ak.chat.form.ChatNameBinder;
import by.ak.chat.form.ShowCreateChatForm;
import by.ak.chat.model.Storage;
import by.ak.chat.security.SecurityService;
import by.ak.chat.util.ChatSelector;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
    ShowCreateChatForm showCreateChatForm = new ShowCreateChatForm(storage, chatSelector, securityService);

    add(header.init());
    H3 title = new H3("Available chats");
    add(title);

    HorizontalLayout chatSelectLayout = new HorizontalLayout(showCreateChatForm);

    add(chatSelectLayout);

    this.setAlignItems(Alignment.CENTER);

    ChatNameBinder chatNameBinder = new ChatNameBinder(showCreateChatForm, securityService);
    chatNameBinder.addBindingAndValidation();
  }
}
