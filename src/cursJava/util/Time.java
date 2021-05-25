package cursJava.util;

public class Time {
  public static final int MIN_IN_HOUR = 60;
  public static final int SEC_IN_MIN = 60;
  public static final int MILLIS_IN_SEC = 1000;
  public static final int SEC_IN_HOUR = 3600;
  public static final int HOURS_IN_DAY = 24;
  private long time;

  public Time() {
    time = 0;
  }

  public Time(long n) {
    time = n;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long n) {
    time = n;
  }

  @Override
  public String toString() {
    long days = (time / (HOURS_IN_DAY * SEC_IN_HOUR * MILLIS_IN_SEC));
    long hours = (time - days * (HOURS_IN_DAY * SEC_IN_HOUR * MILLIS_IN_SEC)) / (SEC_IN_HOUR * MILLIS_IN_SEC);
    long minutes = (time - days * (HOURS_IN_DAY * SEC_IN_HOUR * MILLIS_IN_SEC) - hours * (SEC_IN_HOUR * MILLIS_IN_SEC))
            / (SEC_IN_MIN * MILLIS_IN_SEC);
    long seconds = (time - days * (HOURS_IN_DAY * SEC_IN_HOUR * MILLIS_IN_SEC) - hours * (SEC_IN_HOUR * MILLIS_IN_SEC)
            - minutes * (SEC_IN_MIN * MILLIS_IN_SEC)) / (MILLIS_IN_SEC);
    return String.format("%d days %02d:%02d:%02d", days, hours, minutes, seconds);
  }
}
