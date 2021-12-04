package by.ak.chat.model;

import by.ak.chat.service.ChatService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import org.atmosphere.inject.annotation.ApplicationScoped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@ApplicationScoped
public class Storage {
  @Getter
  private final Queue<ChatMessage> messages = new ConcurrentLinkedQueue<>();
  // todo change to private final ConcurrentLinkedDeque<ChatMessage> messages = new ConcurrentLinkedDeque<>();
  //   use     messages.pollLast(); if messages.size() > 200
  private final ComponentEventBus eventBus = new ComponentEventBus(new Div());
  @Autowired
  private ChatService chatService;

  public void addMessage(String user, String message) {
    ChatMessage newMessage = new ChatMessage(user, message);
    messages.add(newMessage);
    eventBus.fireEvent(new ChatEvent());
    chatService.save(newMessage).subscribe();
  }

  public void addMessageUserJoined(String message) {
    ChatMessage userJoined = new ChatMessage();
    userJoined.setText(message);
    messages.add(userJoined);
    eventBus.fireEvent(new ChatEvent());
    chatService.save(userJoined).subscribe();
  }

  public void addMessageUserLeft(String message) {
    ChatMessage userLeft = new ChatMessage();
    userLeft.setText(message);
    messages.add(userLeft);
    eventBus.fireEvent(new ChatEvent());
    chatService.save(userLeft).subscribe();
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


  /*
  * Fetch messages from DB before app is started to show to user previous messages
  * */
  @PostConstruct
  public void init() {
    chatService.findAll().subscribe(messages::add);
  }
}
