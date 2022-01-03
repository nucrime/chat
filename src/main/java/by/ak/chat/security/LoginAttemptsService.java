package by.ak.chat.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptsService {
  private final AuthenticationProperties properties;

  private LoadingCache<String, Integer> attemptsCache;

  public LoginAttemptsService(AuthenticationProperties properties) {
    super();
    this.properties = properties;
    attemptsCache =
      CacheBuilder.newBuilder()
        .expireAfterWrite(properties.getFailedAttemptsExpiration(), TimeUnit.MINUTES)
        .build(new CacheLoader<String, Integer>() {
          @Override
          public Integer load(String key) {
            return 0;
          }
        });
  }

  public void loginSucceeded(String key) {
    attemptsCache.invalidate(key);
  }

  public void loginFailed(String key) {
    int attempts;
    try {
      attempts = attemptsCache.get(key);
    } catch (ExecutionException e) {
      attempts = 0;
    }
    attempts++;
    attemptsCache.put(key, attempts);
  }

  public boolean isBlocked(String key) {
    try {
      return attemptsCache.get(key) >= properties.getMaxLoginAttempts();
    } catch (ExecutionException e) {
      return false;
    }
  }
}

