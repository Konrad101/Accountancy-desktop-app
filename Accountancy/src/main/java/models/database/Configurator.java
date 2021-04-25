package models.database;

import models.Company;
import models.DataTypeEnum;
import models.database.dataManagers.CompanyDataManager;
import models.database.dataManagers.DatabaseManager;

import java.util.ArrayList;
import java.util.Arrays;

public class Configurator {
    private final String RUNNING_PATH = DatabaseManager.getBasePath() + "/config/runningConfiguration.csv";
    private final String START_CONFIG_PATH = DatabaseManager.getBasePath() + "/config/startConfiguration.csv";
    private final String COMPANY_CONFIG_PATH = "/config/companyConfig.csv";

    public String[] getStartConfigData() {
        DataReader reader = new DataReader(START_CONFIG_PATH);
        String[][] data = reader.getFileData();
        if (data != null && data.length == 1 && data[0].length != 0) {
            return data[0];
        }

        return null;
    }

    public String[] getRunningConfigData(DataTypeEnum type) {
        DataReader reader = new DataReader(RUNNING_PATH);
        String[][] data = reader.getFileData();
        if (data != null) {
            for (String[] singleData : data) {
                if (singleData.length > 0 && singleData[0].toUpperCase().equals(CompanyDataManager.getEnumTypeName(type).toUpperCase())) {
                    return singleData;
                }
            }
        }

        return null;
    }

    public void setRunningConfiguration(DataTypeEnum type, int month, int year) {
        if (type == null) {
            return;
        }

        if (type != DataTypeEnum.CONTRACTORS) {
            DataReader reader = new DataReader(RUNNING_PATH);
            String[][] runningConfig = reader.getFileData();

            if (runningConfig == null) {
                runningConfig = fillEmptyArrayWithData(type, month, year);
            } else {
                boolean found = false;
                for (int i = 0; i < runningConfig.length && !found; i++) {
                    if (runningConfig[i].length == 3) {
                        if (runningConfig[i][0].toUpperCase().equals(CompanyDataManager.getEnumTypeName(type).toUpperCase())) {
                            runningConfig[i][1] = changeMonthNumberToName(month);
                            runningConfig[i][2] = String.valueOf(year);
                            found = true;
                        }
                    }
                }

                if (!found) {
                    ArrayList<String[]> configList = new ArrayList<>(Arrays.asList(runningConfig));
                    String[] newConfigData = new String[3];
                    newConfigData[0] = CompanyDataManager.getEnumTypeName(type);
                    newConfigData[1] = changeMonthNumberToName(month);
                    newConfigData[2] = String.valueOf(year);
                    configList.add(newConfigData);
                    runningConfig = new String[configList.size()][];
                    int index = 0;
                    for (String[] arr : configList) {
                        runningConfig[index++] = arr;
                    }
                }
            }
            new DataWriter(RUNNING_PATH).saveInfo(runningConfig, false);
        }
    }

    public void setStartConfiguration(DataTypeEnum type, int month, int year) {
        DataWriter writer = new DataWriter(START_CONFIG_PATH);
        String[][] data = fillEmptyArrayWithData(type, month, year);
        writer.saveInfo(data, false);
    }

    public void setCompanyConfigData(Company company, String basePath) {
        if (company != null) {
            DataWriter writer = new DataWriter(basePath + COMPANY_CONFIG_PATH);
            String[][] configData = new String[2][];
            configData[0] = new String[2];
            configData[0][0] = "COMPANY_NAME";
            configData[0][1] = "NIP";
            configData[1] = company.getData();
            writer.saveInfo(configData, false);
        }
    }

    public Company getCompanyFromConfigData() {
        DataReader reader = new DataReader(COMPANY_CONFIG_PATH);
        String[][] fileData = reader.getFileData();
        if (fileData != null) {
            Company company = new Company();
            if (fileData.length == 2) {
                if (fileData[1].length > 1) {
                    company.setCompanyName(fileData[1][0]);
                    company.setNIP(fileData[1][1]);
                }
            }

            return company;
        }

        return null;
    }

    private String[][] fillEmptyArrayWithData(DataTypeEnum type, int month, int year) {
        if (type == null) {
            return null;
        }

        String[][] array = new String[1][];
        array[0] = new String[3];
        array[0][0] = CompanyDataManager.getEnumTypeName(type);
        array[0][1] = changeMonthNumberToName(month);
        array[0][2] = String.valueOf(year);

        return array;
    }

    private String changeMonthNumberToName(int month) {
        int monthNumber = 1;
        for (String monthName : CompanyDataManager.getMonths()) {
            if (monthNumber == month) {
                return monthName;
            }
            monthNumber++;
        }

        return String.valueOf(month);
    }
}
