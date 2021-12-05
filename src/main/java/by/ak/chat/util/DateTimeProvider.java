package by.ak.chat.util;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@VaadinSessionScope
public class DateTimeProvider {
  public int timeOffset;
  public ZoneId zoneId;

  public String stringCurrentDateTime() {
    return LocalDateTime.now().toString();
  }

  public String stringCurrentDate() {
    return LocalDate.now().toString();
  }

  public String stringCurrentTime() {
    return LocalTime.now().toString();
  }

  public LocalDateTime localDateTimeCurrent() {
    return LocalDateTime.now();
  }

  public LocalDate localDateCurrent() {
    return LocalDate.now();
  }

  public LocalTime localTimeCurrent() {
    return LocalTime.now();
  }

  public LocalDateTime localDateTimeFromString(String dateTime) {
    return LocalDateTime.parse(dateTime);
  }

  public LocalDate localDateFromString(String date) {
    return LocalDate.parse(date);
  }

  public LocalTime localTimeFromString(String time) {
    return LocalTime.parse(time);
  }

  public String stringFromLocalDateTime(LocalDateTime dateTime) {
    return dateTime.toString();
  }

  public String stringFromLocalDateTimeBrowserOffset(LocalDateTime dateTime) {
    if (zoneId == null) {
      int MILLISECONDS_IN_HOUR = 3_600_000;
      int MILLISECONDS_IN_MINUTE = 60000;
      int SECONDS_IN_MINUTE = 60;
      String offset = String.format("%02d:%02d",
        Math.abs(timeOffset / MILLISECONDS_IN_HOUR),
        Math.abs((timeOffset / MILLISECONDS_IN_MINUTE) % SECONDS_IN_MINUTE));
      offset = (timeOffset >= 0 ? "+" : "-") + offset;
      zoneId = ZoneId.of(offset);
    }

    DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    return ZonedDateTime.of(dateTime, zoneId).format(simpleDateFormat);
  }
}
