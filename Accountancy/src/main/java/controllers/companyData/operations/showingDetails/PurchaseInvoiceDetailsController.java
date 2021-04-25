package controllers.companyData.operations.showingDetails;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import models.DateParser;
import models.invoices.PurchaseInvoice;

import java.net.URL;
import java.util.ResourceBundle;

public class PurchaseInvoiceDetailsController extends SaleInvoiceDetailsController {
    public Label paydayDateLabel;
    public CheckBox paidBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        this.updateLabelList();
        centerLabelText(paydayDateLabel);
        changeLabelFontSize(paydayDateLabel);
    }

    private void updateLabelList() {
        this.labels.add(pricesStartIndex, paydayDateLabel);
    }

    @Override
    public void loadDataToComponents() {
        super.loadDataToComponents();
        if (this.invoice != null && this.invoice instanceof PurchaseInvoice) {
            PurchaseInvoice purchaseInvoice = (PurchaseInvoice) this.invoice;
            paidBox.setSelected(purchaseInvoice.isPaid());
            paydayDateLabel.setText(purchaseInvoice.getDatePrintFormat(DateParser.parseDate(purchaseInvoice.getDeadline())));
        }
    }
}
