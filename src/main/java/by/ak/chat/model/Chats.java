package by.ak.chat.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Chats {
  private int limit;
  private final Map<String, MessageQueue> messages = new ConcurrentHashMap<>();

  public Stream<ChatMessage> stream(ChatMessage message) {
    return messages.get(message.getChat()).stream();
  }

  public MessageQueue one(String chat) {
    return messages.get(chat);
  }

  public Stream<String> all() {
    return messages.keySet().stream();
  }

  public void add(ChatMessage message) {
    messages.computeIfAbsent(message.getChat(), k -> limitedQueue());
    messages.get(message.getChat()).add(message);
  }

  public void remove(ChatMessage message) {
    messages.get(message.getChat()).remove(message);
  }

  public static Chats withMessageLimitOf(int limit) {
    Chats chats = new Chats();
    chats.limit = limit;
    return chats;
  }

  public MessageQueue limitedQueue() {
    return MessageQueue.of(limit);
  }
}
