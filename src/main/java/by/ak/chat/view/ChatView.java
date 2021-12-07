package by.ak.chat.view;

import by.ak.chat.model.ChatMessage;
import by.ak.chat.model.Storage;
import by.ak.chat.security.SecurityService;
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
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
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

  public ChatView(Storage storage, SecurityService securityService, DateTimeProvider dateTimeProvider) {
    this.dateTimeProvider = dateTimeProvider;
    this.storage = storage;
    this.securityService = securityService;

    grid = buildChatGrid(storage);
  }

  // todo add edit messages??
  private Grid<ChatMessage> buildChatGrid(Storage storage) {
    chat = new VerticalLayout();
    chat.setVisible(true);
    addAndExpand(chat);
    log.info("ChatGrid init");
    final Grid<ChatMessage> grid;
    grid = new Grid<>();
    grid.setItems(storage.getMessages());
    grid.addColumn(new ComponentRenderer<>(message -> new Html(renderRow(message))))
      .setAutoWidth(true);
    chat.add(
      logoutButton(),
      title());
    chat.addAndExpand(
      grid,
      inputAndSendButton());
    //todo scroll to last message. does not work yet
    return grid;
  }

  private Button logoutButton() {
    log.info("logoutButton init");
    Button logoutButton = new Button(LOG_OUT, e -> securityService.logout());
    logoutButton.setHeight(3, Unit.PERCENTAGE);
    HorizontalLayout header = new HorizontalLayout();
    header.setJustifyContentMode(JustifyContentMode.END);
    header.add(logoutButton);
    return logoutButton;
  }

  private HorizontalLayout title() {
    log.info("title init");
    H3 title = new H3(TITLE);
    HorizontalLayout titleLayout = new HorizontalLayout();
    titleLayout.addAndExpand(title);
    titleLayout.setAlignItems(Alignment.CENTER);
    return titleLayout;
  }

  private HorizontalLayout inputAndSendButton() {
    log.info("inputAndSendButton init");
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
    refreshGridContent();
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    registration.remove();
  }

  private String renderRow(ChatMessage message) {
    if (Objects.isNull(message.getUser())) {
      return Processor.process(message.getText());
    } else
      return Processor.process(String.format(CHAT_MESSAGE_TEMPLATE, formatTime(message.getCreated()), message.getUser(), message.getText()));
  }

  private String formatTime(LocalDateTime dateTime) {
    // why in the world this being called after logout?
    return dateTimeProvider.stringFromLocalDateTimeBrowserOffset(dateTime);
  }
}

