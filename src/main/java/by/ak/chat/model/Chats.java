package by.ak.chat.model;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Data
public class Chats {
  // todo limit to 200 for each chat
  private final Map<String, List<ChatMessage>> messages = new ConcurrentHashMap<>();

  public Stream<ChatMessage> stream(ChatMessage message) {
    return messages.get(message.getChat()).stream();
  }

  public List<ChatMessage> one(String chat) {
    return messages.get(chat);
  }

  public Stream<String> all() {
    return messages.keySet().stream();
  }

  public void add(ChatMessage message) {
    messages.computeIfAbsent(message.getChat(), k -> new LinkedList<>());
    messages.get(message.getChat()).add(message);
  }

  public void remove(ChatMessage message) {
    messages.get(message.getChat()).remove(message);
  }
}
