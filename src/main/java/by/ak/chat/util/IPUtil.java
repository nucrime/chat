package by.ak.chat.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static com.google.common.net.HttpHeaders.X_FORWARDED_FOR;

@Component
public record IPUtil(HttpServletRequest request) {

  private static final int ORIGINATING_ADDRESS = 0;
  private static final String DELIMITER = ",";

  public String getClientIP() {
    String xfHeader = request.getHeader(X_FORWARDED_FOR);
    if (xfHeader == null) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(DELIMITER)[ORIGINATING_ADDRESS];
  }
}
