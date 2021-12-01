package by.ak.chat.service;

import by.ak.chat.model.ChatMessage;
import by.ak.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
  private final ChatRepository repository;

  public Mono<ChatMessage> save(ChatMessage chatMessage) {
    log.info("[{}] Saving chat message: {}", log.getName(), chatMessage);
    return repository.save(chatMessage);
  }

  public ChatMessage find(String user) {
    log.info("[{}] Searching chat message by email: {}", log.getName(), user);
    return repository.findByUser(user);
  }

  /*  todo decide if it is needed at all.
  public void delete(ChatMessage chatMessage) {
    repository.delete(chatMessage);
  }
  */
}
