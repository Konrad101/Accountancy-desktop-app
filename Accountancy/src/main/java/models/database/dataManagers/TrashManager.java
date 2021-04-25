package models.database.dataManagers;

import models.DataTypeEnum;
import models.DateParser;
import models.database.DataReader;
import models.database.DataWriter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class TrashManager {
    private final String REMOVED_DATA_FILEPATH = DatabaseManager.getBasePath() + "/trash/removedData.csv";

    public void inspectRemovedData() {
        DataReader reader = new DataReader(REMOVED_DATA_FILEPATH);
        String[][] removedData = reader.getFileData();
        List<String[]> removedDataList = new ArrayList<>();
        if (removedData != null) {
            removedDataList.add(new String[1]);
            removedDataList.get(0)[0] = DateParser.parseLocalDate(LocalDate.now());
            for (int i = 1; i < removedData.length; i++) {
                LocalDate currentDate = LocalDate.now();
                LocalDate deleteDate = DateParser.parseCalendarToLocalDate(DateParser.parseStringToDate(removedData[i][0]));
                long daysBetween = DAYS.between(currentDate, deleteDate);
                if (daysBetween > -15) {
                    removedDataList.add(removedData[i]);
                }
            }
            if (removedData.length != removedDataList.size()) {
                removedData = new String[removedDataList.size()][];
                for (int i = 0; i < removedDataList.size(); i++) {
                    removedData[i] = removedDataList.get(i);
                }
                DataWriter writer = new DataWriter(REMOVED_DATA_FILEPATH);
                writer.saveInfo(removedData, false);
            }
        } else {
            DataWriter writer = new DataWriter(REMOVED_DATA_FILEPATH);
            String[][] lastInspectionData = new String[1][];
            lastInspectionData[0] = new String[1];
            lastInspectionData[0][0] = DateParser.parseLocalDate(LocalDate.now());
            writer.saveInfo(lastInspectionData, false);
        }
    }

    public void addRemovedContractor(String[] data) {
        if (data != null) {
            int dataOffset = 2;
            String[] removedData = new String[data.length + dataOffset];
            removedData[0] = DateParser.parseLocalDate(LocalDate.now());
            removedData[1] = DataTypeEnum.CONTRACTORS.toString();
            saveRemovedData(removedData, data);
        }
    }

    public void addRemovedInvoice(String[] data, DataTypeEnum type, int year, int month) {
        if (data != null) {
            int dataOffset = 4;
            String[] removedData = new String[data.length + dataOffset];
            removedData[0] = DateParser.parseLocalDate(LocalDate.now());
            removedData[1] = type.toString();
            removedData[2] = String.valueOf(year);
            removedData[3] = CompanyDataManager.getMonths().get(month - 1);
            saveRemovedData(removedData, data);
        }
    }

    private void saveRemovedData(String[] removedDataToTrashFile, String[] dataToRemove) {
        if (removedDataToTrashFile != null && dataToRemove != null) {
            int dataOffset = removedDataToTrashFile.length - dataToRemove.length;
            System.arraycopy(dataToRemove, 0, removedDataToTrashFile, dataOffset, dataToRemove.length);
            DataWriter writer = new DataWriter(REMOVED_DATA_FILEPATH);
            String[][] fileData = new String[1][];
            fileData[0] = removedDataToTrashFile;
            writer.saveInfo(fileData, true);
        }
    }
}
