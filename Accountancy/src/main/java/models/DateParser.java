package models;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateParser {

    public static Calendar parseStringToDate(String date) {
        if (date == null) {
            return null;
        }

        String[] dateArr = date.split("-");
        try {
            int year = Integer.parseInt(dateArr[2]);
            int month;
            switch (Integer.parseInt(dateArr[1])) {
                case 1:
                    month = Calendar.JANUARY;
                    break;
                case 2:
                    month = Calendar.FEBRUARY;
                    break;
                case 3:
                    month = Calendar.MARCH;
                    break;
                case 4:
                    month = Calendar.APRIL;
                    break;
                case 5:
                    month = Calendar.MAY;
                    break;
                case 6:
                    month = Calendar.JUNE;
                    break;
                case 7:
                    month = Calendar.JULY;
                    break;
                case 8:
                    month = Calendar.AUGUST;
                    break;
                case 9:
                    month = Calendar.SEPTEMBER;
                    break;
                case 10:
                    month = Calendar.OCTOBER;
                    break;
                case 11:
                    month = Calendar.NOVEMBER;
                    break;
                case 12:
                    month = Calendar.DECEMBER;
                    break;
                default:
                    month = -1;

            }
            int day = Integer.parseInt(dateArr[0]);

            return new GregorianCalendar(year, month, day);
        } catch (NumberFormatException | IndexOutOfBoundsException ex) {
            return null;
        }
    }

    public static String parseLocalDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        return day + "-" + month + "-" + year;
    }

    public static String parseDate(Calendar date) {
        if (date == null) {
            return null;
        }
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DAY_OF_MONTH);
        return day + "-" + month + "-" + year;
    }

    public static LocalDate parseCalendarToLocalDate(Calendar date) {
        if (date == null) {
            return null;
        }

        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DAY_OF_MONTH);
        return LocalDate.of(year, month, day);
    }
}
