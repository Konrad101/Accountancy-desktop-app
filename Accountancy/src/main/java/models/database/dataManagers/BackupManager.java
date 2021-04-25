package models.database.dataManagers;

import models.DateParser;
import models.database.DataReader;
import models.database.DataWriter;
import models.database.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;

public class BackupManager {
    private static final int DAYS_BETWEEN_BACKUPS = 3;
    private static final String BACKUP_PATH = "backup";
    private static final String CONFIG_FILE_PATH = BACKUP_PATH + "/backupConfig.csv";
    private static final String COLUMN_HEADER_TEXT = "LAST_BACKUP_DATE";

    public static String getConfigFilePath() {
        return CONFIG_FILE_PATH;
    }

    public boolean backupIsNecessary() {
        DataReader reader = new DataReader(CONFIG_FILE_PATH);
        String[][] fileData = reader.getFileData();
        if (fileData != null) {
            if (fileData.length == 2) {
                LocalDate lastBackupDate = DateParser.parseCalendarToLocalDate(DateParser.parseStringToDate(fileData[1][0]));
                LocalDate currentDate = LocalDate.now();
                long daysBetween = DAYS.between(lastBackupDate, currentDate);
                return daysBetween >= DAYS_BETWEEN_BACKUPS;
            }
        }

        this.createBackupConfig(LocalDate.of(1990, 1, 1));
        return true;
    }

    public void makeBackup() {
        try {
            Path sourceDir = Path.of("programData");
            Path destinationDir = Path.of(BACKUP_PATH);
            copyWholeDir(sourceDir, destinationDir);
            createBackupConfig(LocalDate.now());
        } catch (IOException ex) {
            new Log().saveError("An error occurred while making backup.");
        }
    }

    private void copyWholeDir(Path src, Path dest) throws IOException {
        try (Stream<Path> stream = Files.walk(src)) {
            stream.forEach(source -> copy(source, dest.resolve(src.relativize(source))));
        }
    }

    private void copy(Path src, Path dest) {
        try {
            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ignored) {
        }
    }


    private void createBackupConfig(LocalDate backupDate) {
        String[][] backupInfo = new String[2][];
        backupInfo[0] = new String[1];
        backupInfo[0][0] = COLUMN_HEADER_TEXT;
        backupInfo[1] = new String[1];
        backupInfo[1][0] = DateParser.parseLocalDate(backupDate);
        DataWriter writer = new DataWriter(CONFIG_FILE_PATH);
        writer.saveInfo(backupInfo, false);
    }
}
