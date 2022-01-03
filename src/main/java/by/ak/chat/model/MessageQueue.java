package by.ak.chat.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentLinkedDeque;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class MessageQueue extends ConcurrentLinkedDeque<ChatMessage> {
  private int sizeLimit;

  @Override
  public boolean add(ChatMessage message) {
    if (limitExceeded()) {
      removeFirst(); // remove oldest message
    }
    return super.add(message);
  }

  private boolean limitExceeded() {
    return !this.isEmpty() && sizeLimit != 0 && size() >= sizeLimit;
  }

  public static MessageQueue of(int sizeLimit) {
    MessageQueue messageQueue = new MessageQueue();
    messageQueue.setSizeLimit(sizeLimit);
    return messageQueue;
  }

  public static MessageQueue empty() {
    return new MessageQueue();
  }
}


