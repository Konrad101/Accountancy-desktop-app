package controllers;

import controllers.companyData.AbstractCellController;
import controllers.companyData.invoiceControllers.InvoiceCellController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Company;
import models.Contractor;
import models.DataTypeEnum;
import models.database.Configurator;
import models.database.Log;
import models.database.dataManagers.CompanyDataManager;
import models.database.dataManagers.DatabaseManager;
import models.database.dataManagers.TrashManager;
import models.invoices.PurchaseInvoice;
import models.invoices.SaleInvoice;

import java.io.IOException;

public class CompanyStarter {
    private final String companyName;

    public CompanyStarter(String pathToCompany, String companyName) {
        this.companyName = companyName;
        DatabaseManager.setBasePath(pathToCompany);
    }

    public void runCompanyStage() {
        setID();
        new TrashManager().inspectRemovedData();
        DatabaseManager.createCompanyData(this.getCompanyData());

        String[] data = new Configurator().getStartConfigData();
        DataTypeEnum option = DataTypeEnum.SALE_INVOICES;
        String stagePath = "fxmlStages/company/";

        if (data != null && data.length == 3) {
            String type = data[0].toUpperCase();
            if (type.contains("PURCHASE")) {
                stagePath += "purchaseInvoices.fxml";
                option = DataTypeEnum.PURCHASE_INVOICES;
            } else if (type.contains("CONTRACTOR")) {
                stagePath += "contractors.fxml";
                option = DataTypeEnum.CONTRACTORS;
            } else {
                stagePath += "saleInvoices.fxml";
            }
        } else {
            stagePath += "saleInvoices.fxml";
        }

        AbstractCellController.setCurrentOption(option);
        AbstractCellController.setDataPathname(this.getPathToLastFile());

        try {
            DatabaseManager.setCurrentCompanyName(this.companyName);
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(stagePath));
            Parent root = loader.load();
            stage.setScene(new Scene(root, 1027, 550));
            AbstractCellController.setCurrentController(loader.getController());

            stage.setResizable(true);
            stage.getIcons().add(new Image("images/icon.png"));
            stage.setTitle("Księgowość - " + this.companyName);

            stage.centerOnScreen();
            stage.show();
            stage.setOnCloseRequest(e -> {
                new Configurator().setStartConfiguration(AbstractCellController.getCurrentType(), InvoiceCellController.getMonth(), InvoiceCellController.getYear());
                System.exit(0);
            });
            new Log().saveInfo(companyName + " has been opened.");
        } catch (IOException ex) {
            new Log().saveError("I/O error while opening company - " + this.companyName);
        }
    }

    private Company getCompanyData() {
        Configurator configurator = new Configurator();
        return configurator.getCompanyFromConfigData();
    }

    /**
     * creating path to the file using array of String
     */
    private String getPathToLastFile() {
        String path = null;
        String[] configData = new Configurator().getStartConfigData();
        if (configData != null && configData.length == 3) {
            CompanyDataManager manager;
            DataTypeEnum type;
            int year = -1;
            int month;
            if (configData[0].toUpperCase().contains("INVOICE")) {
                if (configData[0].toUpperCase().contains("SALE")) {
                    type = DataTypeEnum.SALE_INVOICES;
                } else {
                    type = DataTypeEnum.PURCHASE_INVOICES;
                }
                month = CompanyDataManager.getMonths().indexOf(configData[1]) + 1;
                try {
                    year = Integer.parseInt(configData[2]);
                } catch (NumberFormatException ignored) {
                }
                manager = new CompanyDataManager(type, year, month);
            } else {
                type = DataTypeEnum.CONTRACTORS;
                manager = new CompanyDataManager(type);
            }

            path = manager.createFilePathFromGivenData();
        } else {
            String resultPath = CompanyDataManager.getNewestSaleInvoiceFilePath();
            if (resultPath != null) {
                path = resultPath;
            }
        }

        if (path != null) {
            if (path.length() > 0) {
                return path;
            }
        }

        return null;
    }

    /**
     * setting highest ID by reading the files
     */
    private void setID() {
        CompanyDataManager manager = new CompanyDataManager(DataTypeEnum.CONTRACTORS);
        Contractor.setHighestID(manager.getHighestID());
        String[] startConfig = new Configurator().getStartConfigData();
        if (startConfig != null) {
            if (startConfig.length == 3) {
                if (!startConfig[0].toUpperCase().contains("CONTRACTOR")) {
                    int year;
                    int month = 7;
                    try {
                        year = Integer.parseInt(startConfig[2]);
                        int number = 1;
                        for (String monthName : CompanyDataManager.getMonths()) {
                            if (monthName.equalsIgnoreCase(startConfig[1])) {
                                month = number;
                                break;
                            }
                            number++;
                        }
                    } catch (NumberFormatException ex) {
                        year = 2020;
                    }

                    if (startConfig[0].toUpperCase().contains("PURCHASE")) {
                        manager = new CompanyDataManager(DataTypeEnum.PURCHASE_INVOICES, year, month);
                        PurchaseInvoice.setHighestID(manager.getHighestID());
                    } else {
                        manager = new CompanyDataManager(DataTypeEnum.SALE_INVOICES, year, month);
                        SaleInvoice.setHighestID(manager.getHighestID());
                    }
                }
            }
        }
    }
}
