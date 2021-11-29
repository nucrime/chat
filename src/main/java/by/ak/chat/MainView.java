package by.ak.chat;

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
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Route("chat")
@Push
public class MainView extends VerticalLayout {
  public static final String CHAT_MESSAGE_TEMPLATE = "**%s**: %s";
  private final Grid<ChatMessage> grid;
  private final Storage storage;
  private Registration registration;
  private VerticalLayout chat;
  private VerticalLayout login;
  @Getter
  @Setter
  private String user;

  public MainView(Storage storage) {
    this.storage = storage;

    buildLogin();
    grid = buildChatGrid(storage);
  }

  private void buildLogin() {
    login = new VerticalLayout();
    login.setVisible(true);
    var username = new TextField("Username");
    username.setAutofocus(true);
    login.add(username, new Button("Login") {
      {
        addClickListener(click -> {
          var user = username.getValue();
          setUser(user);
          login.setVisible(false);
          chat.setVisible(true);
          storage.addMessageUserJoined(String.format("**%s** joined", user));
        });
        addClickShortcut(Key.ENTER);
      }
    });

    add(login);
  }

  private Grid<ChatMessage> buildChatGrid(Storage storage) {
    chat = new VerticalLayout();
    chat.setVisible(false);
    add(chat);
    final Grid<ChatMessage> grid;
    grid = new Grid<>();
    grid.setItems(storage.getMessages());
    grid.addColumn(new ComponentRenderer<>(message -> new Html(renderRow(message))))
      .setAutoWidth(true);
    var textField = new TextField();
    textField.setAutofocus(true);
    chat.add(
      new H3("Chat"),
      grid,
      new HorizontalLayout() {
        {
          add(
            textField,
            new Button("➤") {
              {
                addClickListener(
                  click -> {
                    storage.addMessage(user, textField.getValue());
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
