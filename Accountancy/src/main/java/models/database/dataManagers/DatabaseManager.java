package models.database.dataManagers;

import javafx.scene.control.Alert;
import models.Company;
import models.database.Configurator;
import models.database.DataReader;
import models.database.DataWriter;
import models.database.Log;
import views.DialogMessage;

import java.io.File;
import java.io.IOException;

public class DatabaseManager {
    protected Log log;
    protected static String currentCompanyName;
    protected static String basePath;

    public static void setBasePath(String basePath) {
        DatabaseManager.basePath = basePath;
    }

    // D:/.../.../programData/Company Name/
    public static String getBasePath() {
        return basePath;
    }

    public static String getCurrentCompanyName() {
        return currentCompanyName;
    }

    public static void setCurrentCompanyName(String currentCompanyName) {
        DatabaseManager.currentCompanyName = currentCompanyName;
    }

    public static void createProgramData(){
        String baseDirectory = "programData";
        if(!fileExist(baseDirectory)){
            DialogMessage message = new DialogMessage(Alert.AlertType.CONFIRMATION);
            if(!message.askUser("Czy folder ma zostać utworzony automatycznie?", "Brak folderu z danymi")){
                System.exit(-1);
                return;
            }
        }
        createDirectoryWithFiles(BackupManager.getConfigFilePath());
        createDirectory(baseDirectory);
        createFile(baseDirectory + "/defaultTax.csv");
        createFile(baseDirectory + "/savedCompanies.csv");
    }

    public void addCompany(Company company, String dataPath){
        String companiesNamesPath = "programData/savedCompanies.csv";
        DataReader reader = new DataReader(companiesNamesPath);
        DataWriter writer = new DataWriter(companiesNamesPath);
        String[][] fileData = reader.getFileData();
        boolean nipExist = false;
        if(fileData == null){
            String[][] companiesData = new String[2][];
            companiesData[0] = new String[2];
            companiesData[0][0] = "COMPANY_NAME";
            companiesData[0][1] = "COMPANY_DATA_PATH";
            companiesData[1] = new String[2];
            companiesData[1][0] = company.getCompanyName();
            companiesData[1][1] = dataPath;
            writer.saveInfo(companiesData, false);
        } else {
            for(int i = 1; i < fileData.length && !nipExist; i++){
                if(fileData[i].length == 2) {
                    DataReader nipReader = new DataReader(fileData[i][1] + "/config/companyConfig.csv");
                    String[][] companyData = nipReader.getFileData();
                    if(companyData != null && companyData.length == 2){
                        if(companyData[1].length == 2){
                            if(company.getNIP().equals(companyData[1][1])){
                                nipExist = true;
                            }
                        }
                    }
                }
            }
            if(!nipExist) {
                String[][] companyData = new String[1][];
                companyData[0] = new String[2];
                companyData[0][0] = company.getCompanyName();
                companyData[0][1] = dataPath;
                writer.saveInfo(companyData, true);
            } else {
                DialogMessage message = new DialogMessage(Alert.AlertType.ERROR);
                message.showDialog("Firma o podanym numerze NIP już istnieje.");
            }
        }

        if(!nipExist) {
            createCompanyData(company);
        }
    }

    public static void createCompanyData(Company company){
        if(company != null) {
            String baseDirectory = "programData/" + company.getNIP();
            String[] programData = {"config", "trash", "purchaseInvoices", "saleInvoices", "contractors"};
            String[] configFilenames = {"runningConfiguration.csv", "startConfiguration.csv", "companyConfig.csv"};

            createDirectory(baseDirectory);
            for (String dir : programData) {
                createDirectory(baseDirectory + "/" + dir);
            }
            createFile(baseDirectory + "/" + programData[1] + "/" + "removedData.csv");
            createFile(baseDirectory + "/" + programData[4] + "/" + "contractors.csv");
            createFile(baseDirectory + "/" + programData[4] + "/" + "contractorsWithInvoicesAmount.csv");
            for (String filename : configFilenames) {
                createFile(baseDirectory + "/" + programData[0] + "/" + filename);
            }
            new Configurator().setCompanyConfigData(company, baseDirectory);
        }
    }

    public static String[][] getSavedCompanies(){
        String[][] companiesWithPaths = null;
        String companiesNamesPath = "programData/savedCompanies.csv";
        DataReader reader = new DataReader(companiesNamesPath);
        String[][] fileData = reader.getFileData();
        if(fileData != null){
            companiesWithPaths = new String[fileData.length - 1][];
            for(int i = 1; i < fileData.length; i++){
                companiesWithPaths[i - 1] = new String[2];
                if(fileData[i].length > 0) {
                    companiesWithPaths[i - 1][0] = fileData[i][0];
                    if(fileData[i].length > 1){
                        companiesWithPaths[i - 1][1] = fileData[i][1];
                    }
                }
            }
        }

        return companiesWithPaths;
    }

    public static Company getCompanyFromFile(String basePath){
        if(basePath != null) {
            String[][] companyConfigData = getCompanyConfigData(basePath);
            if (companyConfigData != null) {
                if (companyConfigData.length == 2 && companyConfigData[1].length == 2) {
                    Company company = new Company();
                    company.setCompanyName(companyConfigData[1][0]);
                    company.setNIP(companyConfigData[1][1]);

                    return company;
                }
            }
        }
        return null;
    }

    private static String[][] getCompanyConfigData(String basePath){
        String path = basePath + "/config/companyConfig.csv";
        DataReader reader = new DataReader(path);
        return reader.getFileData();
    }

    public static void createDirectoryWithFiles(String pathname){
        if(!fileExist(pathname) && pathname != null){
            String[] arrPath = pathname.split("/");
            int index = 0;
            boolean pathIsCorrect = true;
            StringBuilder path = new StringBuilder();

            while(index < arrPath.length - 1 && pathIsCorrect) {
                if (index != 0) {
                    path.append("/").append(arrPath[index]);
                } else {
                    path.append(arrPath[index]);
                }

                if (path.toString().toUpperCase().contains("NULL")) {
                    new Log().saveError("Null in creating directory: " + pathname);
                    pathIsCorrect = false;
                } else {
                    createDirectory(path.toString());
                    index++;
                }
            }
            if(pathIsCorrect) {
                createFile(pathname);
            }
        }
    }

    protected static boolean createDirectory(String pathname){
        return new File(pathname).mkdir();
    }

    protected static boolean createFile(String pathname){
        try{
            File file = new File(pathname);
            return file.createNewFile();
        }catch (IOException ex){
            return false;
        }
    }

    public static boolean fileExist(String pathname){
        return new File(pathname).exists();
    }

}
