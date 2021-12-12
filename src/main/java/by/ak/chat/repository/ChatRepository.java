package by.ak.chat.repository;

import by.ak.chat.model.ChatMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ChatRepository extends ReactiveMongoRepository<ChatMessage, String> {
  ChatMessage findByUser(String user);

//  @Override
//  @Tailable
//  Flux<ChatMessage> findAll(); // migration to tailable cursor
}
