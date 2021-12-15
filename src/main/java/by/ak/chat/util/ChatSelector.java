package by.ak.chat.util;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
@Data
public class ChatSelector {
  public static final String DEFAULT = "General";
  private String current = DEFAULT;

  public void select(String chat) {
    this.current = chat;
  }
}
