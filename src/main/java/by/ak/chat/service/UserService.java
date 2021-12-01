package by.ak.chat.service;

import by.ak.chat.model.User;
import by.ak.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository repository;

  public void save(User user) {
    log.info("[{}] Saving user: {}", log.getName(), user);
    repository.save(user).block();
  }

  public Optional<User> find(String username) {
    log.info("[{}] Searching user by username: {}", log.getName(), username);
    return repository.findByUsername(username).blockOptional();
  }

  public void delete(User user) {
    log.info("[{}] Deleting user {}", log.getName(), user);
    repository.delete(user);
  }
}
