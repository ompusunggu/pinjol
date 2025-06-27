package com.project.loan.command.helper;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeHelper {
  public static final int EOD_HOUR = 23;
  public static final int EOD_MINUTE = 59;
  public static final int EOD_SECOND = 59;
  public static final int EOD_NANOSECOND = 999999999;

  public LocalDateTime getCurrentTime() {
    return LocalDateTime.now();
  }

  public long getCurrentEpoch() {
    return this.toEpochMillis(this.getCurrentTime());
  }

  public long getNDaysAhead(int n) {
    return this.toEpochMillis(this.getCurrentTime().plusDays((long)n));
  }

  public long getNDaysAhead(long epoch, int n) {
    return this.toEpochMillis(toLocalDateTime(epoch).plusDays((long)n));
  }

  public LocalDateTime getBeginningOfDay() {
    return this.getBeginningOfDay(this.getCurrentTime());
  }

  public LocalDateTime getBeginningOfDay(LocalDateTime localDateTime) {
    return localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
  }

  public LocalDateTime getEndOfDay() {
    return this.getBeginningOfDay().plusDays(1L).minusNanos(1L);
  }

  public Long toEpochMillis(LocalDateTime localDateTime) {
    return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

  public long addDurationTimeIgnoreWeekend(long durationTime) {
    return this.addDurationTimeIgnoreWeekend(this.getCurrentTime(), durationTime);
  }

  public long addDurationTimeIgnoreWeekend(LocalDateTime startDate, long durationTime) {
    int dayOfWeek = startDate.getDayOfWeek().getValue();
    LocalDateTime endDate = startDate.plusDays(this.totalDaysToAdd(dayOfWeek, durationTime));
    if (dayOfWeek > 5) {
      endDate = endDate.plusDays(1L).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    return this.toEpochMillis(endDate);
  }

  private long totalDaysToAdd(int dayOfWeek, long duration) {
    long result = 0L;
    if (duration != 0L) {
      boolean isStartOnWorkday = dayOfWeek < 6;
      if (isStartOnWorkday) {
        result = duration + (duration + (long)dayOfWeek - 1L) / 5L * 2L;
      } else {
        result = duration + (duration - 1L) / 5L * 2L + (long)(7 - dayOfWeek);
      }
    }

    return result;
  }

  public LocalDateTime toLocalDateTime(long time) {
    return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  public LocalDateTime toLocalDateTime(long time, ZoneId zoneId) {
    return Instant.ofEpochMilli(time).atZone(zoneId).toLocalDateTime();
  }

  public long getBeginningOfTheDayEpoch() {
    return this.toEpochMillis(this.getBeginningOfDay(this.getCurrentTime()));
  }

  public long getBeginningOfTheDayEpoch(long epoch) {
    LocalDateTime localDateTime = this.getBeginningOfDay(this.toLocalDateTime(epoch));
    return this.toEpochMillis(localDateTime);
  }

  public long dateToMillis(String date, String format) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    LocalDate localDate = LocalDate.parse(date, formatter);
    return this.toEpochMillis(localDate.atStartOfDay());
  }

  public long getStartOfTheMonthEpoch(int year, int month) {
    return this.toEpochMillis(LocalDateTime.of(year, month, 1, 0, 0));
  }

  public long getEndOfTheMonthEpoch(int year, int month) {
    Year selectedYear = Year.of(year);
    Month selectedMonth = Month.of(month);
    int lastDayOfMonth = selectedMonth.length(selectedYear.isLeap());
    return this.toEpochMillis(LocalDateTime.of(year, month, lastDayOfMonth, 23, 59, 59, 999999999));
  }
}