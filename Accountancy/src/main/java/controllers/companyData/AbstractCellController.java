package controllers.companyData;

import controllers.MenuStarter;
import controllers.companyData.invoiceControllers.InvoiceCellController;
import controllers.companyData.operations.OperationController;
import controllers.companyData.operations.edit.EditOperation;
import controllers.companyData.operations.showingDetails.DetailsOperation;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.DataTypeEnum;
import models.TableData;
import models.database.Configurator;
import models.database.DataReader;
import models.database.dataManagers.CompanyDataManager;
import models.database.dataManagers.DatabaseManager;
import models.invoices.PurchaseInvoice;
import models.invoices.SaleInvoice;
import views.DialogMessage;
import views.IconSetter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static models.DataTypeEnum.*;

public abstract class AbstractCellController implements Initializable {
    public HBox toolbarBox;
    public HBox optionsBox;
    public Button saleInvoices;
    public Button purchaseInvoices;
    public Button contractors;
    public Button addButton;
    public Button editButton;
    public Button deleteButton;
    public Button detailsButton;
    public Text companyNameText;

    private final static String OPTION_BACKGROUND_COLOR = "#373737";    // dark
    private final static String TOOLBAR_BACKGROUND_COLOR = "#636363";   // bright

    protected static DataTypeEnum currentOption;
    protected static String dataPathname = null;
    protected static AbstractCellController currentController;

    protected boolean additionalWindowOpened = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeComponentsColors();
        companyNameText.setText(DatabaseManager.getCurrentCompanyName());
    }

    private void initializeComponentsColors(){
        setComponentColor(toolbarBox, TOOLBAR_BACKGROUND_COLOR);
        setComponentColor(optionsBox, OPTION_BACKGROUND_COLOR);
    }

    protected void setComponentColor(Node node, String colorNumber){
        if(node != null) {
            node.setStyle("-fx-background-color: " + colorNumber);
        }
    }

    public static void setCurrentController(AbstractCellController controller) {
        AbstractCellController.currentController = controller;
    }

    public static void setDataPathname(String dataPathname) {
        AbstractCellController.dataPathname = dataPathname;
    }

    public static void setCurrentOption(DataTypeEnum currentOption) {
        AbstractCellController.currentOption = currentOption;
    }

    protected void setEmptyTableText(TableView tableview, String text){
        if(tableview != null) {
            Label label = new Label(text);
            label.setStyle("-fx-font-size: 22px;");
            tableview.setPlaceholder(label);
        }
    }

    public void changeInvoiceData(int month, int year){
        InvoiceCellController.setMonth(month);
        InvoiceCellController.setYear(year);
        InvoiceCellController controller = (InvoiceCellController) currentController;
        controller.changeMonthText();
        CompanyDataManager manager = new CompanyDataManager(currentOption, year, month);
        int monthState = manager.getMonthState();
        if(monthState == -1){
            manager.changeMonthState(1);
        }
        Configurator configurator = new Configurator();
        configurator.setRunningConfiguration(currentOption, month, year);
        dataPathname = manager.createFilePathFromGivenData();
        CompanyDataManager.createDirectoryWithFiles(dataPathname);
        if(currentOption == SALE_INVOICES){
            SaleInvoice.setHighestID(manager.getHighestID());
        } else {
            PurchaseInvoice.setHighestID(manager.getHighestID());
        }
    }

    public void setAdditionalWindowOpened(boolean additionalWindowOpened) {
        this.additionalWindowOpened = additionalWindowOpened;
    }

    public void backToMenu(){
        if(!additionalWindowOpened) {
            Stage currentStage = (Stage) deleteButton.getScene().getWindow();
            currentStage.close();
            new Configurator().setStartConfiguration(currentOption, InvoiceCellController.getMonth(), InvoiceCellController.getYear());
            new MenuStarter(new Stage()).runMenu();
        }
    }

    public void saleInvoicesAction() {
        changeScene(SALE_INVOICES);
    }

    public void purchaseInvoicesAction() {
        changeScene(PURCHASE_INVOICES);
    }

    public void contractorsAction() {
        changeScene(CONTRACTORS);
    }

    private void changeScene(DataTypeEnum option) {
        if (!additionalWindowOpened && option != null && option != currentOption) {
            dataPathname = this.getDataFilePath(option);
            if(currentOption == CONTRACTORS){
                ContractorsController contractorsController = (ContractorsController) currentController;
                contractorsController.cleanMemory();
            }

            String path = "fxmlStages/company/";
            switch (option) {
                case SALE_INVOICES:
                    SaleInvoice.setHighestID(new DataReader(dataPathname).readHighestID());
                    path += "saleInvoices.fxml";
                    break;
                case PURCHASE_INVOICES:
                    PurchaseInvoice.setHighestID(new DataReader(dataPathname).readHighestID());
                    path += "purchaseInvoices.fxml";
                    break;
                case CONTRACTORS:
                    path += "contractors.fxml";
                    break;
                default:
                    return;
            }

            try {
                if (currentOption != CONTRACTORS) {
                    this.saveConfigInfo();
                }
                currentOption = option;

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getClassLoader().getResource(path));
                loader.load();

                Stage stage = (Stage) saleInvoices.getScene().getWindow();
                Parent parent = loader.getRoot();
                currentController = loader.getController();

                stage.getScene().setRoot(parent);
                stage.show();
                if(currentOption == CONTRACTORS){
                    InvoiceCellController.setYear(-1);
                    InvoiceCellController.setMonth(-1);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveConfigInfo() {
        if (currentOption != CONTRACTORS) {
            Configurator configurator = new Configurator();
            configurator.setRunningConfiguration(currentOption, InvoiceCellController.getMonth(), InvoiceCellController.getYear());
        }
    }

    /**
     * when couldn't found @return null*/
    private String getDataFilePath(DataTypeEnum type) {
        Configurator configurator = new Configurator();
        String[] runningConfig = configurator.getRunningConfigData(type);
        CompanyDataManager manager;
        if (runningConfig != null && runningConfig.length == 3) {
            int year;
            int month = 0;
            try {
                year = Integer.parseInt(runningConfig[2]);
                int monthNumber = 0;
                for(String monthName: CompanyDataManager.getMonths()){
                    monthNumber++;
                    if(monthName.toUpperCase().equals(runningConfig[1].toUpperCase())){
                        month = monthNumber;
                        break;
                    }
                }
            } catch (NumberFormatException ex) {
                year = 2020;
                month = 7;
            }

            manager = new CompanyDataManager(type, year, month);
        } else {
            manager = new CompanyDataManager(type);
        }

        String filepath = manager.createFilePathFromGivenData();
        if (filepath == null) {
            if (type == PURCHASE_INVOICES) {
                filepath = CompanyDataManager.getNewestPurchaseInvoiceFilePath();
            } else if (type == SALE_INVOICES) {
                filepath = CompanyDataManager.getNewestSaleInvoiceFilePath();
            }
        }
        return filepath;
    }

    public void showAddWindow() {
        if(currentOption != CONTRACTORS) {
            if (!additionalWindowOpened && InvoiceCellController.getMonth() != -1 && InvoiceCellController.getYear() != -1) {
                String pathname = getOperationScenePath(OperationType.ADDING);
                String title = getOperationSceneTitle(true);
                if (pathname != null && title != null) {
                    openOperationStage(pathname, title, OperationType.ADDING);
                }
            }
        } else {
            String pathname = getOperationScenePath(OperationType.ADDING);
            String title = getOperationSceneTitle(true);
            if (pathname != null && title != null) {
                openOperationStage(pathname, title, OperationType.ADDING);
            }
        }
    }

    protected void showEditWindow(int id, TableData data) {
        if (!additionalWindowOpened) {
            String pathname = getOperationScenePath(OperationType.EDITING);
            String title = getOperationSceneTitle(false);
            if (pathname != null && title != null) {
                openOperation(pathname, title, OperationType.EDITING, id, data);
            }
        }
    }

    protected void showDetailsWindow(TableData data){
        if (!additionalWindowOpened) {
            String pathname = getOperationScenePath(OperationType.SHOWING_DETAILS);
            String title = "Szczegóły";
            if (pathname != null) {
                openOperation(pathname, title, OperationType.SHOWING_DETAILS, 0, data);
            }
        }
    }

    private String getOperationScenePath(OperationType operationType) {
        String operationName;
        if (operationType == OperationType.ADDING) {
            operationName = "add";
        } else if (operationType == OperationType.EDITING){
            operationName = "edit";
        } else {
            operationName = "show";
        }

        String pathname = "fxmlStages/company/operations/";
        if(operationType != OperationType.SHOWING_DETAILS) {
            pathname += operationName + "ing/" + operationName;
        } else {
            pathname += "showingDetails/" + operationName;
        }
        switch (currentOption) {
            case SALE_INVOICES:
                pathname += "SaleInvoice.fxml";
                break;
            case PURCHASE_INVOICES:
                pathname += "PurchaseInvoice.fxml";
                break;
            case CONTRACTORS:
                pathname += "Contractor.fxml";
                break;
            default:
                return null;
        }

        return pathname;
    }

    private String getOperationSceneTitle(boolean operationIsAdding) {
        boolean typeIsInvoice = currentOption != CONTRACTORS;
        String title;
        if (operationIsAdding) {
            title = "Dodawanie ";
        } else {
            title = "Edytowanie ";
        }

        if (typeIsInvoice) {
            if (!InvoiceCellController.isMonthOpened()) {
                String operation;
                if (operationIsAdding) {
                    operation = "dodać";
                } else {
                    operation = "edytować";
                }
                DialogMessage message = new DialogMessage(Alert.AlertType.ERROR);
                message.showDialog("Miesiąc musi być otwarty, aby " + operation + " fakturę.");
                return null;
            }
            title += "faktury";
        } else {
            title += "kontrahenta";
        }

        return title;
    }

    protected void openOperationStage(String pathname, String title, OperationType operation){
        this.openOperation(pathname, title, operation, 0, null);
    }

    private void openOperation(String pathname, String title, OperationType operation, int id, TableData data) {
        if (operation != OperationType.EDITING && operation != OperationType.SHOWING_DETAILS && (id != 0 || data != null)) {
            return;
        }

        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(pathname));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            additionalWindowOpened = true;

            OperationController addController = loader.getController();
            addController.setController(this);
            if (operation == OperationType.EDITING) {
                EditOperation editOperationController = loader.getController();
                editOperationController.setTableData(data);
                editOperationController.setID(id);
                editOperationController.loadDataToComponents();
            } else if(operation == OperationType.SHOWING_DETAILS){
                DetailsOperation detailsOperationController = loader.getController();
                detailsOperationController.setTableData(data);
                detailsOperationController.loadDataToComponents();
            }

            stage.setResizable(false);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.setOnCloseRequest(e -> this.additionalWindowOpened = false);
            IconSetter.setStageIcon(stage);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void deleteCell(int id) {
        CompanyDataManager manager;
        if (currentOption == CONTRACTORS) {
            manager = new CompanyDataManager(CONTRACTORS);
        } else {
            manager = new CompanyDataManager(currentOption, InvoiceCellController.getYear(), InvoiceCellController.getMonth());
        }
        int result = manager.removeData(id);
        if (result < 0) {
            DialogMessage message = new DialogMessage(Alert.AlertType.ERROR);
            message.showDialog("Nie udało się usunąć faktury.");
        }
    }

    public static DataTypeEnum getCurrentType() {
        return currentOption;
    }

    protected void columnTextToRight(TableColumn column) {
        if (column != null)
            column.setStyle("-fx-alignment: CENTER-RIGHT;");
    }

    protected void columnTextToCenter(TableColumn column) {
        if (column != null)
            column.setStyle("-fx-alignment: CENTER;");
    }

    protected static class Identifier<T extends Comparable<? super T>> implements Comparable<Identifier<T>> {
        private final int id;
        private final T data;

        public Identifier(int id, T data) {
            this.id = id;
            this.data = data;
        }

        public int getId() {
            return this.id;
        }

        public T getData() {
            return data;
        }

        @Override
        public int compareTo(Identifier<T> o) {
            if (o != null) {
                return data.compareTo(o.data);
            }
            return 0;
        }
    }

    protected enum OperationType{
        ADDING, EDITING, MONTH_CHOOSING, SHOWING_DETAILS
    }
}
