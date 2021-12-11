package by.ak.chat.service;

import by.ak.chat.model.ChatMessage;
import by.ak.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
  private final ChatRepository repository;

  public Mono<ChatMessage> save(ChatMessage chatMessage) {
    log.info("[FUAGRA] Saving chat message: {}", chatMessage);
    return repository.save(chatMessage);
  }

  public ChatMessage find(String user) {
    log.info("[FUAGRA] Searching chat message by email: {}", user);
    return repository.findByUser(user);
  }

  @Tailable
  public Flux<ChatMessage> findAll() {
    log.info("[FUAGRA] Searching all chat messages");
    return repository.findAll();
  }

  // todo add update message

  /*  todo decide if it is needed at all.
  public void delete(ChatMessage chatMessage) {
    repository.delete(chatMessage);
  }
  */
}
