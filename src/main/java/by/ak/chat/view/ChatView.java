package by.ak.chat.view;

import by.ak.chat.component.Header;
import by.ak.chat.component.MessageEditor;
import by.ak.chat.model.ChatMessage;
import by.ak.chat.model.MessageQueue;
import by.ak.chat.model.Storage;
import by.ak.chat.security.SecurityService;
import by.ak.chat.util.ChatSelector;
import by.ak.chat.util.DateTimeProvider;
import com.github.rjeschke.txtmark.Processor;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

import static by.ak.chat.model.MessageQueue.empty;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Route(value = ChatView.PATH, layout = Header.class)
@PageTitle(ChatView.TITLE)
public class ChatView extends VerticalLayout
//  implements HasDynamicTitle
{
  public static final String CHAT_MESSAGE_TEMPLATE = "%s   **%s**: %s";
  public static final String PATH = "/";
  public static final String TITLE = "FUAGRA";
  public static final String LOG_OUT = "Log out";
  public static final int MESSAGE_LENGTH_LIMIT = 255;
  public static final String MSG_LONG = "Message is too long";
  public static final String MSG_EMPTY = "Message is empty";
  public static final String REDUNDANT_WHITESPACES = "\\s+";
  public static final String SINGLE_WHITESPACE = " ";
  public static final int DEFAULT_HEIGHT = 10;
  public static final String DELIMITER = " | ";

  private final Grid<ChatMessage> grid;
  private final Storage storage;
  private final DateTimeProvider dateTimeProvider;
  private final SecurityService securityService;
//  private final Header header;
  private final MessageEditor editor;
  private final ChatSelector selector;
  private Registration registration;
  //todo show only last 200 messages
  private VerticalLayout chat;

  public ChatView(Storage storage, SecurityService securityService, DateTimeProvider dateTimeProvider, Header header, MessageEditor editor, ChatSelector selector) {
    this.dateTimeProvider = dateTimeProvider;
    this.storage = storage;
    this.securityService = securityService;
//    this.header = header;
    this.editor = editor;
    this.selector = selector;

    add(
//      header.init(),
      title());

    var filter = new TextField();
    filter.setPlaceholder("search...");
    // Hook logic to components
    // Replace listing with filtered content when user changes filter
    filter.setValueChangeMode(ValueChangeMode.EAGER);
    filter.addValueChangeListener(e -> listMessages(e.getValue()));

    var filterLayout = new HorizontalLayout(filter);
    // todo maybe move to the right?
    add(filterLayout);

    grid = buildChatGrid();
    add(editor);

    // Connect selected ChatMessage to editor or hide if none is selected
    grid.asSingleSelect().addValueChangeListener(e -> {
      if (e.getValue() != null && isAuthorOrAdmin(securityService, e)) { // only author or admin can edit
        editor.editMessage(e.getValue());
      } else {
        if (editor.isVisible()) { // if editor is visible or user is not the author of message, hide it
          editor.setVisible(false);
          grid.deselect(e.getValue());
        }
      }
    });

    // Listen changes made by the editor, refresh data from backend
    editor.setChangeHandler(this::refreshAfterEdit);
  }

  private boolean isAuthorOrAdmin(SecurityService securityService, AbstractField.ComponentValueChangeEvent<Grid<ChatMessage>, ChatMessage> e) {
    return securityService.getLoggedInUserName().equals(e.getValue().getUser()) || securityService.isAdmin();
  }

  private Grid<ChatMessage> buildChatGrid() {
    chat = new VerticalLayout();
    chat.setVisible(true);
    add(chat);
    final Grid<ChatMessage> grid;
    grid = new Grid<>();
    grid.setItems(Optional.ofNullable(currentChat()).orElse(empty()));
    grid.addColumn(new ComponentRenderer<>(message -> new Html(renderRow(message))));
    grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT); // wrap cell content, so that the text wraps
    chat.addAndExpand(
      grid, messageInput());
    return grid;
  }

  private VerticalLayout title() {
    var title = new H3(TITLE + DELIMITER + selector.getCurrent());
    var titleLayout = new VerticalLayout();
    titleLayout.add(title);
    titleLayout.setHeight(DEFAULT_HEIGHT, Unit.PERCENTAGE);
    titleLayout.setAlignItems(Alignment.CENTER);
    return titleLayout;
  }

  private MessageInput messageInput() {
    var input = new MessageInput();

    input.addSubmitListener(e -> {
      String sanitizedText = e.getValue()
        .trim()
        .replaceAll(REDUNDANT_WHITESPACES, SINGLE_WHITESPACE);
      if (!hasText(sanitizedText)) {
        Notification.show(MSG_EMPTY);
      } else if (sanitizedText.length() > MESSAGE_LENGTH_LIMIT) {
        Notification.show(MSG_LONG);
      } else {
        storage.addMessage(securityService.getLoggedInUserName(), sanitizedText);
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

  private void refreshAfterEdit() {
    editor.setVisible(false);
    getUI()
      .ifPresent(
        ui ->
          ui.access(
            () -> {
              getUI().ifPresent(UI::push);
              ui.beforeClientResponse(grid, ctx -> {
                listMessages();
              });
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

  private MessageQueue currentChat() {
    return storage.getChat(selector.getCurrent());
  }

  private void listMessages(String textFilter) {
    if (hasText(textFilter)) {
      grid.setItems(storage.searchMessages(currentChat(), textFilter));
    } else {
      grid.setItems(currentChat());
    }
  }

  private void listMessages() {
    listMessages(null);
  }
//
//  @Override
//  public String getPageTitle() {
//    return TITLE + DELIMITER + selector.getCurrent();
//  }
}

