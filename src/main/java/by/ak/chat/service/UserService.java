package by.ak.chat.service;

import by.ak.chat.model.User;
import by.ak.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository repository;

  public void save(User user) {
    log.info("[{}] Saving user: {}", log.getName(), user);
    repository.save(user);
  }

  public User find(String email) {
    log.info("[{}] Searching user by email: {}", log.getName(), email);
    return repository.findByEmail(email).block();
  }

  public void delete(User user) {
    log.info("[{}] Deleting user {}", log.getName(), user);
    repository.delete(user);
  }
}
