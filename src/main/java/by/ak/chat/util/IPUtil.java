package by.ak.chat.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static com.google.common.net.HttpHeaders.X_FORWARDED_FOR;

@Component
@RequiredArgsConstructor
public class IPUtil {

  private static final int ORIGINATING_ADDRESS = 0;
  private static final String DELIMITER = ",";
  private final HttpServletRequest request;

  public String getClientIP() {
    String xfHeader = request.getHeader(X_FORWARDED_FOR);
    if (xfHeader == null) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(DELIMITER)[ORIGINATING_ADDRESS];
  }
}
