package by.ak.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class Storage {
  @Getter
  private Queue<ChatMessage> messages = new ConcurrentLinkedQueue<>();

  public void addMessage(String user, String message) {
    messages.add(new ChatMessage(user, message));}

  @Getter
  @AllArgsConstructor
  public static class ChatMessage {
    private String user;
    private String message;
  }
}
