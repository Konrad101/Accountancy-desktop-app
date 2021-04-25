package models.database.dataManagers;

import models.DataTypeEnum;
import models.TableDataPattern;
import models.database.DataReader;
import models.database.DataWriter;
import models.database.Log;
import models.invoices.Invoice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class CompanyDataManager extends DatabaseManager {
    private final DataTypeEnum managerType;
    private int year = -1;
    private int month = -1;

    private static final ArrayList<String> months = new ArrayList<>(Arrays.asList(
            "january", "february", "march", "april",
            "may", "june", "july", "august",
            "september", "october", "november", "december"
    ));

    private static final ArrayList<String> polishMonths = new ArrayList<>(Arrays.asList(
            "styczeń", "luty", "marzec", "kwiecień",
            "maj", "czerwiec", "lipiec", "sierpień",
            "wrzesień", "październik", "listopad", "grudzień"
    ));

    public CompanyDataManager(DataTypeEnum type) {
        this.managerType = type;
        this.log = new Log();
    }

    public CompanyDataManager(DataTypeEnum type, int year, int month) {
        this.managerType = type;
        this.year = year;
        this.month = month;
        this.log = new Log();
    }

    public static ArrayList<String> getMonths() {
        return months;
    }

    public static ArrayList<String> getPolishMonths() {
        return polishMonths;
    }

    ////    OPERATIONS

    public int addData(TableDataPattern cell) {
        if (cell != null) {
            String filePath = createFilePathFromGivenData();

            String[][] cellData = new String[1][];
            cellData[0] = cell.getData();
            if (managerType == DataTypeEnum.SALE_INVOICES || managerType == DataTypeEnum.PURCHASE_INVOICES) {
                Invoice invoice = (Invoice) cell;
                ContractorInvoicesManager contractorManager = new ContractorInvoicesManager();
                contractorManager.incrementAmount(invoice.getContractorNumber());
            }
            createDirectoryWithFiles(filePath);

            DataWriter writer = new DataWriter(filePath);
            writer.saveInfo(cellData, true);

            return 0;
        }

        return -1;
    }

    public int removeData(int id) {
        String filePath = createFilePathFromGivenData();
        DataReader reader = new DataReader(filePath);
        String[][] data = reader.getFileData();
        if (data == null) {
            log.saveError("File not found in removing data" + ", " + currentCompanyName);
            return -2;
        }

        String[][] newData = new String[data.length - 1][];
        boolean found = false;
        int index = 0;
        try {
            for (String[] line : data) {
                if (!found && line[0].equals(String.valueOf(id))) {
                    if (managerType == DataTypeEnum.SALE_INVOICES || managerType == DataTypeEnum.PURCHASE_INVOICES) {
                        ContractorInvoicesManager contractorManager = new ContractorInvoicesManager();
                        contractorManager.decrementAmount(Integer.parseInt(line[3]));
                        new TrashManager().addRemovedInvoice(line, managerType, year, month);
                    } else {
                        new TrashManager().addRemovedContractor(line);
                    }
                    found = true;
                    continue;
                }
                newData[index++] = line;
            }
        } catch (IndexOutOfBoundsException ex) {
            log.saveError("Unable to delete contractor - ID doesn't exist in file" + ", " + currentCompanyName);
            return -1;
        }
        DataWriter writer = new DataWriter(filePath);
        writer.saveInfo(newData, false);

        return 0;
    }

    public int editData(int id, TableDataPattern cell) {
        String filePath = createFilePathFromGivenData();
        DataReader reader = new DataReader(filePath);
        String[][] data = reader.getFileData();
        if (data == null) {
            log.saveError("File not found in editing data" + ". " + currentCompanyName);
            return -2;
        }
        boolean found = false;
        try {
            for (int i = 0; i < data.length && !found; i++) {
                if (data[i].length != 0 && data[i][0].equals(String.valueOf(id))) {
                    if (managerType == DataTypeEnum.SALE_INVOICES || managerType == DataTypeEnum.PURCHASE_INVOICES) {
                        Invoice invoice = (Invoice) cell;
                        try {
                            int currentContractorID = invoice.getContractorNumber();
                            int previousContractorID = Integer.parseInt(data[i][3]);
                            if (currentContractorID != previousContractorID) {
                                ContractorInvoicesManager contractorManager = new ContractorInvoicesManager();
                                contractorManager.decrementAmount(previousContractorID);
                                contractorManager.incrementAmount(currentContractorID);
                            }
                        } catch (NumberFormatException ex) {
                            log.saveError("Wrong contractor ID for invoice in editing: " + data[i][3] + ", " + currentCompanyName);
                        }
                    }
                    data[i] = cell.getData();
                    data[i][0] = String.valueOf(id);
                    found = true;
                }
            }
        } catch (NullPointerException ex) {
            log.saveError("Null in editing data" + ", " + currentCompanyName);
            return -1;
        }
        DataWriter writer = new DataWriter(filePath);
        writer.saveInfo(data, false);

        return 0;
    }

    ////

    public void changeMonthState(int currentState) {
        String path = createPathToMonthConfigFile(managerType);
        if (managerType != null && managerType != DataTypeEnum.CONTRACTORS && path != null) {
            if (year != -1 && month >= 1 && month <= 12) {
                createDirectoryWithFiles(path);

                DataReader reader = new DataReader(path);
                String[][] data = reader.getFileData();
                DataWriter writer = new DataWriter(path);

                if (data != null) {
                    boolean found = false;
                    for (int i = 0; i < data.length && !found; i++) {
                        if (i == month - 1) {
                            data[i][1] = String.valueOf(currentState);
                            found = true;
                        }
                    }
                    writer.saveInfo(data, false);
                } else {
                    createMonthsConfig(currentState);
                }
            }
        }
    }

    public void createMonthsConfig(int currentState) {
        if (year != -1 && month != -1) {
            String[][] monthsData = new String[months.size()][];
            for (int i = 0; i < monthsData.length; i++) {
                monthsData[i] = new String[2];
                monthsData[i][0] = months.get(i);
                if (i == month - 1) {
                    monthsData[i][1] = String.valueOf(currentState);
                } else {
                    monthsData[i][1] = "-1";
                }
            }
            DataWriter writer = new DataWriter(createPathToMonthConfigFile(managerType));
            writer.saveInfo(monthsData, false);
        }
    }

    public boolean monthsConfigFileExist() {
        return fileExist(createPathToMonthConfigFile(managerType));
    }

    public int getHighestID() {
        int id = 0;
        String dataPathname = createFilePathFromGivenData();
        if (dataPathname != null) {
            DataReader reader = new DataReader(dataPathname);
            id = reader.readHighestID();
        }

        return id;
    }

    /**
     * @return -2 if file not found, -3 when path was wrong
     */
    public int getMonthState() {
        String path = createPathToMonthConfigFile(managerType);
        if (path == null) {
            return -2;
        }

        DataReader reader = new DataReader(path);
        String[][] fileData = reader.getFileData();
        try {
            if (fileData != null) {
                if (month != 0) {
                    String monthName = getMonthName(month);
                    if (monthName != null) {
                        for (String[] singleData : fileData) {
                            if (singleData[0].equalsIgnoreCase(monthName)) {
                                return Integer.parseInt(singleData[1]);
                            }
                        }
                    }
                }
            } else {
                if (path.contains("-1")) {
                    return -3;
                } else {
                    createMonthsConfig(1);
                }
            }
        } catch (NumberFormatException ex) {
            log.saveError("Error while parsing month state in " + year + ", " + currentCompanyName);
            return -2;
        }

        return -2;
    }

    private String createPathToMonthConfigFile(DataTypeEnum type) {
        if (type != null) {
            String path = basePath + "/" + CompanyDataManager.getEnumTypeName(type) + "/";
            path += this.year + "/monthsConfig.csv";
            return path;
        }

        return null;
    }

    /**
     * Path to csv files
     */
    public String createFilePathFromGivenData() {
        String filepath = basePath + "/";
        if (managerType == DataTypeEnum.CONTRACTORS) {
            filepath += "contractors/contractors.csv";
        } else {
            String typeName;
            if (managerType == DataTypeEnum.SALE_INVOICES) {
                typeName = "saleInvoices";
            } else {
                typeName = "purchaseInvoices";
            }

            if (year != 0 && month > 0 && month <= 12) {
                filepath += typeName + "/" + year + "/" + getMonthName(month) + "/" + typeName + ".csv";
            } else {
                filepath = null;
            }
        }

        return filepath;
    }

    public static String getEnumTypeName(DataTypeEnum typeEnum) {
        String type = null;
        if (typeEnum != null) {
            switch (typeEnum) {
                case PURCHASE_INVOICES:
                    type = "purchaseInvoices";
                    break;
                case SALE_INVOICES:
                    type = "saleInvoices";
                    break;
                case CONTRACTORS:
                    type = "contractors";
            }
        }

        return type;
    }

    public static String getNewestSaleInvoiceFilePath() {
        return getInvoiceDataFilePath(DataTypeEnum.SALE_INVOICES);
    }

    public static String getNewestPurchaseInvoiceFilePath() {
        return getInvoiceDataFilePath(DataTypeEnum.PURCHASE_INVOICES);
    }

    private static String getInvoiceDataFilePath(DataTypeEnum type) {
        if (type != null) {
            String typeName = CompanyDataManager.getEnumTypeName(type);
            String basePath = getBasePath() + "/" + typeName + "/";
            String yearAndMonth = getYearAndMonthPath(basePath);
            if (yearAndMonth != null) {
                basePath += yearAndMonth;
                basePath += "/" + typeName + ".csv";
                return basePath;
            }
        }

        return null;
    }

    private static String getYearAndMonthPath(String basePath) {
        if (basePath != null) {
            LocalDateTime dateTime = LocalDateTime.now();
            int year = dateTime.getYear();
            int month = dateTime.getMonthValue();

            boolean fileExist = false;
            for (; year >= 1990 && !fileExist; month--) {
                if (month == 0) {
                    month = 12;
                    year--;
                }
                fileExist = fileExist(basePath + year + "/" + months.get(month - 1));
            }

            if (fileExist) {
                return year + "/" + months.get(month - 1);
            }
        }

        return null;
    }

    private static String getMonthName(int monthNumber) {
        String month;
        try {
            month = months.get(monthNumber - 1);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
        return month;
    }

}
