package by.ak.chat.model;

import by.ak.chat.repository.ChatRepository;
import by.ak.chat.service.ChatService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class Storage {
  // todo google: redis, amqp spring boot / or any in-memory concurrency stable messaging.
  //  have a look to activeMQ (see application-dev.yaml)
  //  !limit max size 200 messages with ability to remove older than 200.
  //  ability to set max size of 200 messages > should be deleted to keep Messaging platform clean
  //  instead of app storage ConcurrentLinkedQueue
  @Getter
  private final Queue<ChatMessage> messages = new ConcurrentLinkedQueue<>();
  private final ComponentEventBus eventBus = new ComponentEventBus(new Div());
  @Autowired
  private ChatService chatService;

  public void addMessage(String user, String message) {
    ChatMessage newMessage = new ChatMessage(user, message);
    messages.add(newMessage);
    eventBus.fireEvent(new ChatEvent());
    // todo do it reactively
    chatService.save(newMessage);
  }

  public void addMessageUserJoined(String message) {
    ChatMessage userJoined = new ChatMessage();
    userJoined.setText(message);
    messages.add(userJoined);
    eventBus.fireEvent(new ChatEvent());
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
}
