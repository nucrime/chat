package by.ak.chat.repository;

import by.ak.chat.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
  Mono<User> findByUsername(String email);

  Flux<User> findByLastNameLike(String lastName);
}
