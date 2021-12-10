package by.ak.chat.service;

import by.ak.chat.model.User;
import by.ak.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository repository;

  public void save(User user) {
    log.info("[FUAGRA] Saving user: {}", user);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    repository.save(user).subscribe();
  }

  public List<User> findAll() {
    return repository.findAll().toStream().collect(Collectors.toList());
  }

  public Optional<User> find(String username) {
    log.info("[FUAGRA] Searching user by username: {}", username);
    return repository.findByUsername(username).blockOptional(); //todo subscribe instead of block?
  }

  public void delete(User user) {
    log.info("[FUAGRA] Deleting user {}", user);
    repository.delete(user).subscribe();
  }
}
