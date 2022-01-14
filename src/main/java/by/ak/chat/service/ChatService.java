package by.ak.chat.service;

import by.ak.chat.model.ChatMessage;
import by.ak.chat.repository.ChatRepository;
import by.ak.chat.util.ChatSelector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public record ChatService(ChatRepository repository, ChatSelector selector) {
  public Mono<ChatMessage> save(ChatMessage chatMessage) {
    log.info("[FUAGRA] Saving chat message: {}", chatMessage);
    return repository.save(chatMessage);
  }

  public ChatMessage find(String user) {
    log.info("[FUAGRA] Searching chat message by email: {}", user);
    return repository.findByUser(user);
  }

  public Flux<ChatMessage> findAll() {
    log.info("[FUAGRA] Searching all chat messages");
    return repository.findAll();
  }

  public Mono<ChatMessage> findById(String id) {
    log.info("[FUAGRA] Searching chat message by id: {}", id);
    return repository.findById(id);
  }

  public String current() {
    return selector.getCurrent();
  }

  public void delete(ChatMessage chatMessage) {
    repository.delete(chatMessage).subscribe();
  }
}
