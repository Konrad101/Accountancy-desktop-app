package models.database.dataManagers;

import models.DataTypeEnum;
import models.database.DataReader;
import models.database.DataWriter;
import models.database.Log;

public class ContractorInvoicesManager {
    private final Log log;
    private final String FILEPATH = DatabaseManager.getBasePath() + "/contractors/contractorsWithInvoicesAmount.csv";

    public ContractorInvoicesManager() {
        log = new Log();
        this.initializeFileIDs();
    }

    private void initializeFileIDs() {
        DataReader reader = new DataReader(new CompanyDataManager(DataTypeEnum.CONTRACTORS).createFilePathFromGivenData());
        int highestContractorID = reader.readHighestID();
        reader = new DataReader(FILEPATH);
        int highestContractorInvoiceAmountID = reader.readHighestID();
        if (highestContractorID > highestContractorInvoiceAmountID) {
            // create missing contractors
            String[][] fileData = new String[highestContractorID - highestContractorInvoiceAmountID][];
            for (int i = 0; i < fileData.length; i++) {
                fileData[i] = new String[2];
                fileData[i][0] = String.valueOf(++highestContractorInvoiceAmountID);
                fileData[i][1] = "0";
            }

            DataWriter writer = new DataWriter(FILEPATH);
            writer.saveInfo(fileData, true);
        }
    }

    public void incrementAmount(int id) {
        changeAmount(id, true);
    }

    public void decrementAmount(int id) {
        changeAmount(id, false);
    }

    private void changeAmount(int id, boolean incrementation) {
        DataReader reader = new DataReader(FILEPATH);
        String[][] fileData = reader.getFileData();
        try {
            for (String[] fileDatum : fileData) {
                if (fileDatum[0].equals(String.valueOf(id))) {
                    int amount = Integer.parseInt(fileDatum[1]);
                    if (incrementation) {
                        amount++;
                    } else {
                        amount--;
                    }
                    fileDatum[1] = String.valueOf(amount);
                    break;
                }
            }
            DataWriter writer = new DataWriter(FILEPATH);
            writer.saveInfo(fileData, false);
        } catch (NumberFormatException ex) {
            log.saveError("Wrong number format in contractor invoices amount - " + DatabaseManager.getCurrentCompanyName());
        }
    }

    public int getInvoicesAmount(int id) {
        DataReader reader = new DataReader(FILEPATH);
        String[][] fileData = reader.getFileData();
        try {
            for (String[] fileDatum : fileData) {
                if (fileDatum[0].equals(String.valueOf(id))) {
                    return Integer.parseInt(fileDatum[1]);
                }
            }
        } catch (NumberFormatException ex) {
            log.saveError("Wrong number format in contractor invoices amount - " + DatabaseManager.getCurrentCompanyName());
        }

        return -1;
    }
}
