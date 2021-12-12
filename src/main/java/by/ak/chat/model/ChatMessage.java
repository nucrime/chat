package by.ak.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@Document
public class ChatMessage {
  @Id
  private String id;
  private LocalDateTime created;
  private String user;
  private String text;

  public ChatMessage() {
    this.created = LocalDateTime.now();
  }

  public ChatMessage(String user, String text) {
    this.created = LocalDateTime.now();
    this.user = user;
    this.text = text;
  }

  public String created() {
    DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    return created.format(simpleDateFormat);
  }
}
