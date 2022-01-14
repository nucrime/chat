package by.ak.chat.service;

import by.ak.chat.config.ChatProperties;
import by.ak.chat.model.ChatMessage;
import by.ak.chat.model.Chats;
import by.ak.chat.model.MessageQueue;
import by.ak.chat.util.EventBus;
import by.ak.chat.view.ChatView;
import com.vaadin.flow.component.messages.MessageListItem;
import org.atmosphere.inject.annotation.ApplicationScoped;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Component
@ApplicationScoped
public class StorageService {
  public static final int ONE_SECOND = 1;
  public static final int HUNDRED_MILLISECONDS = 100;

  private final Chats chats;
  private final ChatProperties chatProperties;

  private final ChatService chatService;

  public StorageService(ChatProperties chatProperties, ChatService chatService) {
    this.chatProperties = chatProperties;
    this.chatService = chatService;
    this.chats = Chats.withMessageLimitOf(chatProperties.getMessage().getLimit());
  }

  public void addMessage(String user, String message) {
    var newMessage = new ChatMessage(user, message);
    addAndSave(newMessage);
    fireEvent();
  }

  public Stream<String> chats() {
    return chats.all();
  }

  // chat V2
  public void addMessage(MessageListItem message) {
    addMessage(message.getUserName(), message.getText());
  }

  public void addMessageUserPresence(String message) {
    var presence = new ChatMessage();
    presence.setText(message);
    addAndSave(presence);
    delayFireEvent(); // Delay fire event, so frontend can render messages
    // before user joined and then scroll to bottom. Works as sure-fire if
    // grid was not refreshed after user joined the chat.
  }

  public Optional<ChatMessage> getMessage(ChatMessage message) {
    return chats.stream(message)
      .filter(m -> message.getId().equals(m.getId()))
      .findFirst();
  }

  public void updateMessage(ChatMessage message) {
    chats.stream(message)
      .filter(m -> message.getId().equals(m.getId()))
      .forEach(chatMessage -> chatMessage.setText(message.getText()));
  }

  public void removeMessage(ChatMessage message) {
    chats.remove(message);
  }

  private void addAndSave(ChatMessage message) {
    message.setChat(chatService.current());
    chats.add(message);
    chatService.save(message).subscribe();
  }

  public MessageQueue getChat(String name) {
    return chats.one(name);
  }

  public List<ChatMessage> searchMessages(MessageQueue messages, String text) {
    return messages.stream().filter(m -> m.getText().contains(text)).collect(toList());
  }

  public void fireEvent() {
    Mono.delay(Duration.ofMillis(HUNDRED_MILLISECONDS))
      .subscribe(e ->
        EventBus.fireEvent(new ChatView.ChatEvent()));
    // default delay is 100ms. Enough for illusion of a real time,
    // as well enough for frontend to render messages.
  }

  /*
-------- __@      __@       __@       __@      __~@
----- _`\>,_    _`\>,_    _`\>,_    _`\>,_    _`\>,_     <--- reactive bicycle
---- (*)/ (*)  (*)/ (*)  (*)/ (*)  (*)/ (*)  (*)/ (*)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*/
  private void delayFireEvent() {
    Mono.delay(Duration.ofSeconds(ONE_SECOND)).subscribe(e -> fireEvent());
  }

  /*
   * Fetch messages from DB before app is started to show to user previous messages
   * */
  @PostConstruct
  public void init() {
    chatService.findAll().subscribe(chats::add);
  }
}
