package by.ak.chat.view;

import by.ak.chat.model.ChatMessage;
import by.ak.chat.model.Storage;
import by.ak.chat.security.SecurityService;
import by.ak.chat.component.Header;
import by.ak.chat.util.DateTimeProvider;
import com.github.rjeschke.txtmark.Processor;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Route(ChatView.PATH)
@Push
public class ChatView extends VerticalLayout {
  public static final String CHAT_MESSAGE_TEMPLATE = "%s   **%s**: %s";
  public static final String PATH = "/";
  public static final String TITLE = "FUAGRA";
  public static final String LOG_OUT = "Log out";
  private final Grid<ChatMessage> grid;
  private final Storage storage;
  private final DateTimeProvider dateTimeProvider;
  private Registration registration;
  //todo show only last 200 messages
  private VerticalLayout chat;
  private final SecurityService securityService;
  private final Header header;

  public ChatView(Storage storage, SecurityService securityService, DateTimeProvider dateTimeProvider, Header header) {
    this.dateTimeProvider = dateTimeProvider;
    this.storage = storage;
    this.securityService = securityService;
    this.header = header;

    grid = buildChatGrid(storage);
  }

  // todo add edit messages??
  private Grid<ChatMessage> buildChatGrid(Storage storage) {
    chat = new VerticalLayout();
    chat.setVisible(true);
    addAndExpand(chat);
    final Grid<ChatMessage> grid;
    grid = new Grid<>();
    grid.setItems(storage.getMessages());
    grid.addColumn(new ComponentRenderer<>(message -> new Html(renderRow(message))));
    grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT); // wrap cell content, so that the text wraps
    chat.add(
      header.init(),
      title());
    chat.addAndExpand(
      grid);
    add(messageInput());
    return grid;
  }

  private VerticalLayout title() {
    H3 title = new H3(TITLE);
    VerticalLayout titleLayout = new VerticalLayout();
    titleLayout.add(title);
    titleLayout.setHeight(10, Unit.PERCENTAGE);
    titleLayout.setAlignItems(Alignment.CENTER);
    return titleLayout;
  }

  private HorizontalLayout inputAndSendButton() {
    TextField textField = textField();
    return new HorizontalLayout() {
      {
        addAndExpand(
          textField,
          sendButton(textField));
      }
    };
  }

  private TextField textField() {
    TextField textField = new TextField();
    textField.setAutofocus(true);
    textField.setWidthFull();
    return textField;
  }

  private Button sendButton(TextField textField) {
    Button sendButton = new Button("➤", e -> {
      if (hasText(textField.getValue())) {
        storage.addMessage(securityService.getLoggedInUserName(), textField.getValue());
        textField.clear();
      }
    });
    sendButton.addClickShortcut(Key.ENTER);
    return sendButton;
  }

  private MessageInput messageInput() {
    MessageInput input = new MessageInput();
    input.addSubmitListener(e -> {
      if (hasText(e.getValue())) {
        storage.addMessage(securityService.getLoggedInUserName(), e.getValue());
      }
    });
    input.setWidthFull();
    return input;
  }

  public void onMessage(Storage.ChatEvent event) {
    refreshGridContent();
  }

  private void refreshGridContent() {
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
    } else
      return Processor.process(
        String.format(
          CHAT_MESSAGE_TEMPLATE,
          dateTimeProvider.formatTime(message.getCreated()),
          message.getUser(),
          message.getText()));
  }
}

