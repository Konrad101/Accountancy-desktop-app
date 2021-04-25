package controllers.companyData.operations.add;

import models.DataTypeEnum;
import models.DateParser;

public class AddPurchaseInvoiceController extends AddSaleInvoiceController {

    public AddPurchaseInvoiceController() {
        invoiceType = DataTypeEnum.PURCHASE_INVOICES;
    }

    @Override
    protected String[] getComponentsData() {
        String[] data = super.getComponentsData();
        String[] purchaseInvoiceData = new String[data.length + 2];
        System.arraycopy(data, 0, purchaseInvoiceData, 0, data.length);
        purchaseInvoiceData[purchaseInvoiceData.length - 2] = String.valueOf(payCheckBox.isSelected());
        purchaseInvoiceData[purchaseInvoiceData.length - 1] = DateParser.parseLocalDate(paybackDate.getValue());

        return purchaseInvoiceData;
    }

}
