package controllers.companyData.operations.edit;

import models.DataTypeEnum;
import models.DateParser;
import models.invoices.PurchaseInvoice;

import java.net.URL;
import java.util.ResourceBundle;

public class EditPurchaseSaleInvoiceController extends EditSaleInvoiceController {

    public EditPurchaseSaleInvoiceController() {
        invoiceType = DataTypeEnum.PURCHASE_INVOICES;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }

    @Override
    public void loadDataToComponents() {
        if (invoice != null) {
            super.loadDataToComponents();
            PurchaseInvoice purchaseInvoice = (PurchaseInvoice) invoice;
            this.payCheckBox.setSelected(purchaseInvoice.isPaid());
            this.paybackDate.setValue(DateParser.parseCalendarToLocalDate(purchaseInvoice.getDeadline()));
        }
    }
}
