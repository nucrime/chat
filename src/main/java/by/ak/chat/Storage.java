package by.ak.chat;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class Storage {
  @Getter
  private final Queue<ChatMessage> messages = new ConcurrentLinkedQueue<>();
  private final ComponentEventBus eventBus = new ComponentEventBus(new Div());

  public void addMessage(String user, String message) {
    messages.add(new ChatMessage(user, message));
    eventBus.fireEvent(new ChatEvent());
  }

  public void addMessageUserJoined(String message) {
    var userJoined = new ChatMessage();
    userJoined.setText(message);
    messages.add(userJoined);
    eventBus.fireEvent(new ChatEvent());
  }

  public static class ChatEvent extends ComponentEvent<Div> {
    public ChatEvent() {
      super(new Div(), false);
    }
  }

  public Registration attachListener(ComponentEventListener<ChatEvent> listener) {
    return eventBus.addListener(ChatEvent.class, listener);
  }
}
