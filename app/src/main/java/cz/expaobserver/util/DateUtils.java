package cz.expaobserver.util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by pechanecjr on 28. 11. 2014.
 */
public class DateUtils {
  public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd kk:mm:ss");

  private DateUtils() {
  }
}
