package controllers.companyData.operations.add;

import controllers.companyData.invoiceControllers.InvoiceCellController;
import controllers.companyData.operations.AbstractInvoiceOperationController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import models.DataTypeEnum;
import models.database.dataManagers.CompanyDataManager;
import models.invoices.PurchaseInvoice;
import models.invoices.Invoice;
import models.invoices.SaleInvoice;
import views.DialogMessage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddSaleInvoiceController extends AbstractInvoiceOperationController implements Initializable {

    public AddSaleInvoiceController() {
        invoiceType = DataTypeEnum.SALE_INVOICES;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }

    @FXML
    public void addInvoice() {
        Invoice invoice;
        if (invoiceType == DataTypeEnum.SALE_INVOICES) {
            invoice = new SaleInvoice();
        } else {
            invoice = new PurchaseInvoice();
        }

        setInvoiceData(invoice, getComponentsData());
        boolean correctData = checkInvoiceConditions(invoice);

        if (correctData && pricesAreCorrect()) {
            CompanyDataManager manager = new CompanyDataManager(invoiceType, InvoiceCellController.getYear(), InvoiceCellController.getMonth());
            int result = -1;
            if (invoiceType == DataTypeEnum.SALE_INVOICES && invoice instanceof SaleInvoice) {
                result = manager.addData(new SaleInvoice((SaleInvoice) invoice));
            } else if (invoiceType == DataTypeEnum.PURCHASE_INVOICES && invoice instanceof PurchaseInvoice) {
                result = manager.addData(new PurchaseInvoice((PurchaseInvoice) invoice));
            }

            if (result < 0) {
                DialogMessage message = new DialogMessage(Alert.AlertType.ERROR);
                message.showDialog("Dodawanie faktury nie powiodło się.");
            } else {
                refreshControllerList();
            }
            this.cancel();
        } else {
            DialogMessage message = new DialogMessage(Alert.AlertType.ERROR);
            message.showDialog("Podane dane są nieprawidłowe.");
        }
    }
}
