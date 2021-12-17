package by.ak.chat.view;

import by.ak.chat.model.Storage;
import by.ak.chat.security.SecurityService;
import by.ak.chat.util.DateTimeProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Route(ChatViewV2.PATH)
@PageTitle("Chat V2")
public class ChatViewV2 extends VerticalLayout {

  public static final String PATH = "/chatV2";
  public static final String TITLE = "Chat";
  private final SecurityService securityService;
  private final Storage storage;
  private final DateTimeProvider dateTimeProvider;

  public ChatViewV2(SecurityService securityService, Storage storage, DateTimeProvider dateTimeProvider) {
    this.securityService = securityService;
    this.storage = storage;
    this.dateTimeProvider = dateTimeProvider;

    addAndExpand(getHeader());

    MessageList list = new MessageList();
//    initMessageList(list);
    MessageInput input = getMessageInput(list);
    VerticalLayout chatLayout = getChatLayout(list, input);

    add(chatLayout);
  }

  private Component getHeader() {
    HorizontalLayout header = new HorizontalLayout();
    header.setWidthFull();
    header.setAlignItems(HorizontalLayout.Alignment.BASELINE);
    H1 title = new H1(TITLE);
    header.addAndExpand(title);
    return header;
  }

//  private void initMessageList(MessageList messageList) {
//    List<MessageListItem> messageListItems = storage.getChats().stream().map(message ->
//        new MessageListItem(
//          message.getText(),
//          dateTimeProvider.format(message.getCreated()),
//          message.getUser()))
//      .collect(Collectors.toList());
//    messageList.setItems(messageListItems);
//  }

  private MessageInput getMessageInput(MessageList list) {
    MessageInput input = new MessageInput();
    input.addSubmitListener(submitEvent -> {
      MessageListItem newMessage = new MessageListItem(
        submitEvent.getValue(), Instant.now(), securityService.getLoggedInUserName());
      newMessage.setUserColorIndex(new Random().nextInt(3));
      List<MessageListItem> items = new ArrayList<>(list.getItems());
      items.add(newMessage);
      list.setItems(items);
      storage.addMessage(newMessage);
    });
    return input;
  }

  private VerticalLayout getChatLayout(MessageList list, MessageInput input) {
    VerticalLayout chatLayout = new VerticalLayout(list, input);
    chatLayout.setHeight(60, Unit.PERCENTAGE);
    chatLayout.setWidth(70, Unit.PERCENTAGE);
    chatLayout.expand(list);
    return chatLayout;
  }
}
