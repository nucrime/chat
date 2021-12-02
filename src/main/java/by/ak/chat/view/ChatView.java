package by.ak.chat.view;

import by.ak.chat.model.ChatMessage;
import by.ak.chat.model.Storage;
import by.ak.chat.security.SecurityService;
import com.github.rjeschke.txtmark.Processor;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import java.util.Objects;

@Route(ChatView.PATH)
@Push
public class ChatView extends VerticalLayout {
  public static final String CHAT_MESSAGE_TEMPLATE = "**%s**: %s";
  public static final String PATH = "/";
  public static final String TITLE = "FUAGRA";
  private final Grid<ChatMessage> grid;
  private final Storage storage;
  private Registration registration;
  //todo show only last 200 messages
  private VerticalLayout chat;
  private final SecurityService securityService;

  public ChatView(Storage storage, SecurityService securityService) {
    this.storage = storage;
    this.securityService = securityService;

    grid = buildChatGrid(storage);
  }

  private Grid<ChatMessage> buildChatGrid(Storage storage) {
    chat = new VerticalLayout();
    chat.setVisible(true);
    add(chat);
    final Grid<ChatMessage> grid;
    grid = new Grid<>();
    grid.setItems(storage.getMessages());
    grid.addColumn(new ComponentRenderer<>(message -> new Html(renderRow(message))))
      .setAutoWidth(true);
    TextField textField = new TextField();
    textField.setAutofocus(true);
    Button logout = new Button("Log out", e -> securityService.logout());
    chat.add(
      logout,
      new H3(TITLE),
      grid,
      new HorizontalLayout() {
        {
          add(
            textField,
            new Button("➤") {
              {
                addClickListener(
                  click -> {
                    storage.addMessage(securityService.getAuthenticatedUser().getUsername(), textField.getValue());
                    textField.clear();
                  });
                addClickShortcut(Key.ENTER);
              }
            });
        }
      });
    return grid;
  }

  public void onMessage(Storage.ChatEvent event) {
    getUI()
      .ifPresent(
        ui ->
          ui.access(
            () -> {
              grid.getDataProvider().refreshAll();
              getUI().ifPresent(UI::push);
              ui.beforeClientResponse(grid, ctx -> grid.scrollToEnd());
            }));
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    registration = storage.attachListener(this::onMessage);
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    registration.remove();
  }

  private String renderRow(ChatMessage message) {
    if (Objects.isNull(message.getUser())) {
      return Processor.process(message.getText());
    } else return Processor.process(String.format(CHAT_MESSAGE_TEMPLATE, message.getUser(), message.getText()));
  }
}
