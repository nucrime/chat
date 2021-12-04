package by.ak.chat.util;

import com.vaadin.flow.component.UI;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DateTimeProvider {

  public static String stringCurrentDateTime() {
    return LocalDateTime.now().toString();
  }

  public static String stringCurrentDate() {
    return LocalDate.now().toString();
  }

  public static String stringCurrentTime() {
    return LocalTime.now().toString();
  }

  public static LocalDateTime localDateTimeCurrent() {
    return LocalDateTime.now();
  }

  public static LocalDate localDateCurrent() {
    return LocalDate.now();
  }

  public static LocalTime localTimeCurrent() {
    return LocalTime.now();
  }

  public static LocalDateTime localDateTimeFromString(String dateTime) {
    return LocalDateTime.parse(dateTime);
  }

  public static LocalDate localDateFromString(String date) {
    return LocalDate.parse(date);
  }

  public static LocalTime localTimeFromString(String time) {
    return LocalTime.parse(time);
  }

  public static String stringFromLocalDateTime(LocalDateTime dateTime) {
    return dateTime.toString();
  }

  public static String stringFromLocalDateTimeBrowserOffset(Optional<UI> ui, LocalDateTime dateTime) {
    AtomicInteger offsetInt = new AtomicInteger();
    ui.ifPresent(uiElement -> {
      uiElement.getPage().retrieveExtendedClientDetails(extendedClientDetails ->
        offsetInt.set(extendedClientDetails.getRawTimezoneOffset()));
    });

    int offsetVal = offsetInt.intValue();
    int MILLISECONDS_IN_HOUR = 3_600_000;
    int MILLISECONDS_IN_MINUTE = 60000;
    int SECONDS_IN_MINUTE = 60;
    String offset = String.format("%02d:%02d",
      Math.abs(offsetVal / MILLISECONDS_IN_HOUR),
      Math.abs((offsetVal / MILLISECONDS_IN_MINUTE) % SECONDS_IN_MINUTE));
    offset = (offsetVal >= 0 ? "+" : "-") + offset;
    ZoneId zoneId = ZoneId.of(offset);

    DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    return ZonedDateTime.of(dateTime, zoneId).format(simpleDateFormat);
  }
}
