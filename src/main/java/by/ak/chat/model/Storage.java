package by.ak.chat.model;

import by.ak.chat.config.ChatProperties;
import by.ak.chat.service.ChatService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.shared.Registration;
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
public class Storage {
  public static final int ONE_SECOND = 1;
  public static final int HUNDRED_MILLISECONDS = 100;

  private final Chats chats;
  private final ChatProperties chatProperties;
  private final ComponentEventBus eventBus = new ComponentEventBus(new Div());

  private final ChatService chatService;

  public Storage(ChatProperties chatProperties, ChatService chatService) {
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
    delayFireEvent(); // Delay fire event, so frontend can render message before user joined and scroll to bottom
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

  public Registration attachListener(ComponentEventListener<ChatEvent> listener) {
    return eventBus.addListener(ChatEvent.class, listener);
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
    Mono.delay(Duration.ofMillis(HUNDRED_MILLISECONDS)).subscribe(e -> eventBus.fireEvent(new ChatEvent()));
    // default delay is 100ms. Enough for illusion of a real time, as well enough for frontend to render messages.
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

  //todo google: vaadin events, component events
  public static class ChatEvent extends ComponentEvent<Div> {
    public ChatEvent() {
      super(new Div(), false);
    }
  }
}
