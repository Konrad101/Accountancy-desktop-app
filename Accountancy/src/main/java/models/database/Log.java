package models.database;

import models.database.dataManagers.CompanyDataManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Log {
    private static final String LOG_FILEPATH = "programData/logs.txt";

    public Log() {
        CompanyDataManager.createDirectoryWithFiles(LOG_FILEPATH);
    }

    private enum LogType {
        INFO, ERROR
    }

    public void saveInfo(String infoText) {
        saveLog(infoText, LogType.INFO);
    }

    public void saveError(String errorText) {
        saveLog(errorText, LogType.ERROR);
    }

    private void saveLog(String log, LogType type) {
        File file = new File(LOG_FILEPATH);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(getCurrentTime());
            writer.write("[" + type + "]: ");
            writer.write(log + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        return "[ " + getIntegerPrintFormat(now.getHour()) + ":" + getIntegerPrintFormat(now.getMinute()) + ":" +
                getIntegerPrintFormat(now.getSecond()) + ", " + getIntegerPrintFormat(now.getDayOfMonth()) + "." +
                getIntegerPrintFormat(now.getMonthValue()) + "." + getIntegerPrintFormat(now.getYear()) + " ]";
    }

    private String getIntegerPrintFormat(int value) {
        if (value < 10) {
            return "0" + value;
        }

        return String.valueOf(value);
    }
}
