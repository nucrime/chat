package by.ak.chat.model;

import lombok.Data;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

@Data
public class Chats {
  private final Map<String, Queue<ChatMessage>> messages = new ConcurrentHashMap<>();

  public Stream<ChatMessage> stream(ChatMessage message) {
    return messages.get(message.getChat()).stream();
  }

  public Queue<ChatMessage> one(String chat) {
    return messages.get(chat);
  }

  public Stream<String> all() {
    return messages.keySet().stream();
  }

  public void add(ChatMessage message) {
    messages.computeIfAbsent(message.getChat(), k -> new ConcurrentLinkedQueue<>());
    messages.get(message.getChat()).add(message);
  }

  public void remove(ChatMessage message) {
    messages.get(message.getChat()).remove(message);
  }
}
