package by.ak.chat.repository;

import by.ak.chat.model.ChatMessage;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface ChatRepository extends ReactiveMongoRepository<ChatMessage, Long> {
  ChatMessage findByUser(String user);

//  @Override
//  @Tailable
//  Flux<ChatMessage> findAll(); // migration to tailable cursor
}
