package by.ak.chat.service;

import by.ak.chat.exception.UserExists;
import by.ak.chat.model.User;
import by.ak.chat.repository.UserRepository;
import by.ak.chat.security.SecurityService;
import com.github.rjeschke.txtmark.Run;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository repository;
  private final SecurityService securityService;

  public void save(User user) {
    log.info("[FUAGRA] Saving user: {}", user);
    find(user.getUsername())
      .ifPresent(u -> {
        if (!(securityService.isAdmin() ||
          securityService.getLoggedInUserName().equals(u.getUsername())))
          throw new UserExists();
      });
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    repository.save(user).subscribe();
  }

  public List<User> findAll() {
    log.info("[FUAGRA] Retrieving all users");
    return repository.findAll().toStream().collect(Collectors.toList());
  }

  public List<User> findByLastName(String lastName) {
    log.info("[FUAGRA] Searching user by lastname: {}", lastName);
    return repository.findByLastNameLike(lastName).toStream().collect(Collectors.toList());
  }

  public Optional<User> findById(String id) {
//    log.info("[FUAGRA] Searching user by id: {}", id); // caused too many logs
    return repository.findById(id).blockOptional();
  }

  public Optional<User> find(String username) {
    log.info("[FUAGRA] Searching user by username: {}", username);
    return repository.findByUsername(username).blockOptional();
  }

  public void delete(User user) {
    log.info("[FUAGRA] Deleting user {}", user);
    repository.delete(user).subscribe();
  }
}
