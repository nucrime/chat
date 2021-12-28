package by.ak.chat.repository;

import by.ak.chat.model.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

  String QUERY_ANY_OCCURRENCE = "{ $or: [ " +
    "{ 'email': { $regex: ?0 } }, " +
    "{ 'username': { $regex: ?0 } }, " +
    "{ 'firstName': { $regex: ?0 } }, " +
    "{ 'lastName': { $regex: ?0 } }, " +
    "{ 'enabled': { $regex: ?0 } }, " +
    "{ 'roles': { $regex: ?0 } }, " +
    "{ 'dob': { $regex: ?0 } }, " +
    "{ 'created': { $regex: ?0 } } ] }";

  Mono<User> findByUsername(String email);

  @Query(QUERY_ANY_OCCURRENCE)
  Flux<User> findByAnyField(String query);
}
