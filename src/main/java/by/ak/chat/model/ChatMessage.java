package by.ak.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@Document
public class ChatMessage {
  @Id
  private String id;
  @NotBlank
  @Size(min = 5, max = 20)
  private String chat;
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
    var simpleDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    return created.format(simpleDateFormat);
  }
}
