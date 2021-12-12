package by.ak.chat.model;

import by.ak.chat.service.ChatService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import org.atmosphere.inject.annotation.ApplicationScoped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@ApplicationScoped
public class Storage {
  public static final int ONE = 1;
  public static final int TWO = 2;
  @Getter
  private final Queue<ChatMessage> messages = new ConcurrentLinkedQueue<>();
  // todo change to private final ConcurrentLinkedDeque<ChatMessage> messages = new ConcurrentLinkedDeque<>();
  //   use     messages.pollLast(); if messages.size() > 200
  private final ComponentEventBus eventBus = new ComponentEventBus(new Div());
  @Autowired
  private ChatService chatService;

  public void addMessage(String user, String message) {
    ChatMessage newMessage = new ChatMessage(user, message);
//    messages.add(newMessage);
//    eventBus.fireEvent(new ChatEvent());
    addAndSave(newMessage);
    fireEvent();
//    chatService.save(newMessage).subscribe();
  }

  // chat V2
  public void addMessage(MessageListItem message) {
    addMessage(message.getUserName(), message.getText());
  }

  public void addMessageUserPresence(String message) {
    ChatMessage presence = new ChatMessage();
    presence.setText(message);
    addAndSave(presence);
    delayFireEvent();
//    Mono.delay(Duration.ofSeconds(TWO)).subscribe(e -> eventBus.fireEvent(new ChatEvent()));
//    addAndSave(userJoined);
//    delayFireEvent();
//    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//    executorService.schedule(() -> eventBus.fireEvent(new ChatEvent()), ONE, TimeUnit.SECONDS); // I want to ride my bicycle...
//    chatService.save(userJoined).subscribe();
  }

  public void updateMessage(ChatMessage message) {
    messages.stream()
      .filter(m -> message.getId().equals(m.getId()))
      .forEach(chatMessage -> chatMessage.setText(message.getText()));
  }

  public void removeMessage(ChatMessage message) {
    messages.remove(message);
  }

  //todo google: vaadin events, component events
  public static class ChatEvent extends ComponentEvent<Div> {
    public ChatEvent() {
      super(new Div(), false);
    }
  }

  // todo listener for storage? or directly after firing event, :25#addMessage.
  //  On new message asynchronously saves to mongo
  public Registration attachListener(ComponentEventListener<ChatEvent> listener) {
    return eventBus.addListener(ChatEvent.class, listener);
  }

  private void addAndSave(ChatMessage message) {
    messages.add(message);
    chatService.save(message).subscribe();
  }

  public void fireEvent() {
    eventBus.fireEvent(new ChatEvent());
  }

  /*
-------- __@      __@       __@       __@      __~@
----- _`\>,_    _`\>,_    _`\>,_    _`\>,_    _`\>,_     <--- reactive bicycle
---- (*)/ (*)  (*)/ (*)  (*)/ (*)  (*)/ (*)  (*)/ (*)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*/
  private void delayFireEvent() {
    Mono.delay(Duration.ofSeconds(TWO)).subscribe(e -> fireEvent());
  }

  /*
  * Fetch messages from DB before app is started to show to user previous messages
  * */
  @PostConstruct
  public void init() {
/*    chatService.findAll()
      .subscribeOn(Schedulers.boundedElastic())
      .subscribe(messages::add); // migration to tailable cursor*/
    chatService.findAll().subscribe(messages::add);
  }
}
