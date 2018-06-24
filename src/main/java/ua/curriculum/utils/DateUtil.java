package ua.curriculum.utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by AnGo on 08.06.2017.
 */
public class DateUtil {
    //    private static final String DATE_PATTERN = "dd.MM.yyyy hh:mm:ss";
    //"(0[1-9]|1[012])[/](0[1-9]|[12][0-9]|3[01])[/](19|20)\d\d ([0-9]|1[0-1]):[0-5][0-9](:[0-5][0-9])? ?[APap][mM]$"
    private static final String DATE_PATTERN = "dd.MM.yyyy";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);


    /**
     * Возвращает полученную дату в виде хорошо отформатированной строки.
     * Используется определённый выше {@link DateUtil#DATE_PATTERN}.
     *
     * @param date - дата, которая будет возвращена в виде строки
     * @return отформатированную строку
     */
    public static String format(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return DATE_TIME_FORMATTER.format(date);
    }

    /**
     * Возвращает полученную дату в виде хорошо отформатированной строки.
     * Используется определённый выше {@link DateUtil#DATE_PATTERN}.
     *
     * @param date         - дата, которая будет возвращена в виде строки ,
     * @param defaultValue - значение, которое вернуть,
     *                     если @dateString не дата.
     * @return отформатированную строку
     */
    public static String format(LocalDateTime date, String defaultValue) {
        if (date == null) {
            return defaultValue;
        }
        return DATE_TIME_FORMATTER.format(date);
    }


    /**
     * Возвращает полученную дату в виде хорошо отформатированной строки.
     * Используется определённый выше {@link DateUtil#DATE_PATTERN}.
     *
     * @param date - дата, которая будет возвращена в виде строки
     * @return отформатированную строку
     */
    public static String format(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DATE_TIME_FORMATTER.format(date);
    }

    /**
     * Преобразует строку, которая отформатирована по правилам
     * шаблона {@link DateUtil#DATE_PATTERN} в объект {@link LocalDate}.
     *
     * Возвращает null, если строка не может быть преобразована.
     *
     * @param dateString - дата в виде String
     * @return объект даты или null, если строка не может быть преобразована
     */
    public static LocalDate parseToLocalDate(String dateString) {
        try {
            return DATE_TIME_FORMATTER.parse(dateString, LocalDate::from);
        } catch (DateTimeParseException e) {
            return null;
        }
    }


    /**
     * Преобразует строку, которая отформатирована по правилам
     * шаблона {@link DateUtil#DATE_PATTERN} в объект {@link LocalDateTime}.
     * <p>
     * Возвращает null, если строка не может быть преобразована.
     *
     * @param dateString - дата в виде String
     * @return объект даты или null, если строка не может быть преобразована
     */
    public static LocalDateTime parseToLocalDateTime(String dateString) {
        try {
            return DATE_TIME_FORMATTER.parse(dateString, LocalDateTime::from);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Проверяет, является ли строка корректной датой.
     *
     * @param dateString - дата в виде String
     * @return true, если строка является корректной датой
     */
    public static boolean validDate(String dateString) {
        // Пытаемся разобрать строку.
        return DateUtil.parseToLocalDate(dateString) != null;
    }

    /**
     * Проверяет, является ли строка корректной датой,
     * и возвращает строку или же .
     *
     * @param dateString   - дата в виде String ,
     * @param defaultValue - значение, которое вернуть,
     *                     если @dateString не дата.
     * @return true, если строка является корректной датой
     */
    public static String getValidStringDate(String dateString, String defaultValue) {
        return (DateUtil.parseToLocalDate(dateString) != null ? dateString : defaultValue);
    }

    public static LocalDateTime getLocalDateTime(Date date) {
        //Date date = new Date();
        if (date == null) {
            date = new Date();
        }
        Instant instant = Instant.ofEpochMilli(date.getTime());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

    }

    public static LocalDate getLocalDate(Date date) {
        //Date date = new Date();
        if (date == null) {
            date = new Date();
        }
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDate localDate = instant.atZone(defaultZoneId).toLocalDate();
        return localDate;

    }

    public static String getDatePattern() {
        return DATE_PATTERN;
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return DATE_TIME_FORMATTER;
    }

    public static Date addToDate(Date date, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, seconds);
        Date newDate = calendar.getTime();
        //System.out.println("1 min later: " + newDate);
        return newDate;
    }

    public static Date getDateFromLocalDate(LocalDate localDate){
        if (localDate== null){
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date getDateFromLocalDateTime(LocalDateTime localDateTime){
        if (localDateTime== null){
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String dateToString(Date date){
        if (date== null){
            return "";
        }
        return DATE_FORMAT.format(date);
    }

}
