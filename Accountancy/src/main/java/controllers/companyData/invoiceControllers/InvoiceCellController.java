package controllers.companyData.invoiceControllers;

import controllers.companyData.AbstractCellController;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import models.DataTypeEnum;
import models.database.dataManagers.CompanyDataManager;
import views.DialogMessage;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class InvoiceCellController extends AbstractCellController implements Initializable {
    protected static int month;
    protected static int year;

    public Button monthButton;
    public Button searchMonthButton;
    public Text monthInfoText;

    private static final String OPEN_MONTH = "Otwórz miesiąc";
    private static final String CLOSE_MONTH = "Zamknij miesiąc";

    private static final String MONTH_TEXT_COLOR = "#eeeeee";
    private static boolean monthOpened = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        InvoiceCellController.year = getDateFromDataPath(true);
        InvoiceCellController.month = getDateFromDataPath(false);
        initializeOpenMonthButton();
        initializeMonthText();
    }

    private void initializeMonthText() {
        monthInfoText.setTextAlignment(TextAlignment.RIGHT);
        changeMonthText();
        monthInfoText.setFill(Paint.valueOf(MONTH_TEXT_COLOR));
    }

    private void initializeOpenMonthButton() {
        CompanyDataManager companyManager = new CompanyDataManager(currentOption, year, month);
        if (!companyManager.monthsConfigFileExist()) {
            companyManager.createMonthsConfig(1);
        }
        if (companyManager.getMonthState() == 0) {
            closeMonth();
        } else {
            openMonth();
        }
    }

    public void changeMonthText() {
        if (month != -1 && year != -1) {
            String monthName = CompanyDataManager.getPolishMonths().get(month - 1);
            monthInfoText.setText(monthName + " " + year);
        } else {
            monthInfoText.setText("Nie wybrano miesiąca");
        }
    }

    public void setCurrentMonthState(DataTypeEnum invoiceType) {
        if (year != -1 && month != -1) {
            int state = new CompanyDataManager(invoiceType, year, month).getMonthState();
            if (state == 0) {
                closeMonth();
            } else if (state == 1) {
                openMonth();
            }
        }
    }

    protected void closeMonth() {
        CompanyDataManager manager = new CompanyDataManager(currentOption, year, month);
        manager.changeMonthState(0);
        monthOpened = false;
        monthButton.setText(OPEN_MONTH);
        monthButton.setStyle("-fx-border-color: #d9291c;" +
                "-fx-border-width: 0 0 2 0;");
    }

    protected void openMonth() {
        CompanyDataManager manager = new CompanyDataManager(currentOption, year, month);
        manager.changeMonthState(1);
        monthOpened = true;
        monthButton.setText(CLOSE_MONTH);
        monthButton.setStyle("-fx-border-color: #1ac41c;" +
                "-fx-border-width: 0 0 2 0;");
    }

    protected boolean showDeleteConfirmation() {
        if (monthOpened && !additionalWindowOpened) {
            DialogMessage message = new DialogMessage();
            return message.showConfirmationBeforeDelete("Czy na pewno usunąć fakturę?");
        } else if (!monthOpened) {
            DialogMessage message = new DialogMessage(Alert.AlertType.ERROR);
            message.showDialog("Miesiąc musi być otwarty, aby usunąć fakturę.");
        }

        return false;
    }

    public void changeMonthState() {
        if (!additionalWindowOpened) {
            if (year != -1 && month != -1 && currentOption != null) {
                CompanyDataManager manager = new CompanyDataManager(currentOption, year, month);

                int monthState = manager.getMonthState();
                if (monthState == 0 || monthState == -2) {
                    openMonth();
                } else if (monthState == 1) {
                    closeMonth();
                }
            }
        }
    }

    public static int getMonth() {
        return InvoiceCellController.month;
    }

    public static int getYear() {
        return InvoiceCellController.year;
    }

    public static void setMonth(int month) {
        InvoiceCellController.month = month;
    }

    public static void setYear(int year) {
        InvoiceCellController.year = year;
    }

    public static boolean isMonthOpened() {
        return monthOpened;
    }

    public void showMonthSearchingWindow() {
        if (!additionalWindowOpened) {
            openOperationStage("fxmlStages/company/monthPicker.fxml", "", OperationType.MONTH_CHOOSING);
        }
    }

    /**
     * @return -1 if something went wrong
     */
    protected static int getDateFromDataPath(boolean year) {
        if (dataPathname != null) {
            String[] data = dataPathname.split("/");
            int index = data.length - 3;  // year index
            if (!year) {
                index += 1;
                int monthNumber = 1;
                for (String month : CompanyDataManager.getMonths()) {
                    if (data[index].equalsIgnoreCase(month)) {
                        return monthNumber;
                    }
                    monthNumber++;
                }
            } else {
                try {
                    return Integer.parseInt(data[index]);
                } catch (NumberFormatException ex) {
                    return -1;
                }
            }
        }

        return -1;
    }

}
