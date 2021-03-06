package by.ak.chat.form;

import by.ak.chat.security.service.SecurityService;
import by.ak.chat.service.StorageService;
import by.ak.chat.util.ChatSelector;
import by.ak.chat.view.chat.ChatView;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.function.Function;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = false)
public class ShowCreateChatForm extends FormLayout {

  private static final String USER_JOINED_MSG = "**%s** joined";

  private final StorageService storage;
  private final ChatSelector chatSelector;
  private final SecurityService securityService;

  private TextField newChatName;

  private Span errorMessageField;

  private Button submitButton;

  public ShowCreateChatForm(StorageService storage, ChatSelector chatSelector, SecurityService securityService) {
    this.storage = storage;
    this.chatSelector = chatSelector;
    this.securityService = securityService;

    var user = this.securityService.getLoggedInUserName();

    newChatName = new TextField("New chat name");
    newChatName.setWidthFull();
    newChatName.setRequired(true);

    errorMessageField = new Span();
    add(newChatName, errorMessageField);

    submitButton = new Button("Join/Create");
    submitButton.addClickShortcut(Key.ENTER);

    setRequiredIndicatorVisible(newChatName);

    add(submitButton);

//    storage.chats().map(chat -> new Button(chat, e -> {
//      selectAndNavigate(chat, user);
//    })).forEach(this::add);

    var list = new ListBox<>();
    add(list);
    storage.chats()
      .map(toButton(user))
      .forEach(button -> {
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY); // makes it look like a link rather than a button
        list.add(button);
        list.add(new Hr());
      });
  }

  private Function<String, Button> toButton(String user) {
    return chat -> new Button(chat, e -> {
      selectAndNavigate(chat, user);
    });
  }

  public void selectAndNavigate(String chat, String user) {
    chatSelector.select(chat);
    storage.addMessageUserPresence(String.format(USER_JOINED_MSG, user));
    UI.getCurrent().navigate(ChatView.PATH);
  }

  private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
    Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
  }
}
