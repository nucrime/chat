package by.ak.chat.repository;

import by.ak.chat.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, Long> {
  Mono<User> findByEmail(String email);
}
