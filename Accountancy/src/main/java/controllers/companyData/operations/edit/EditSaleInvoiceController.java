package controllers.companyData.operations.edit;


import controllers.companyData.invoiceControllers.InvoiceCellController;
import controllers.companyData.operations.AbstractInvoiceOperationController;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import models.DataTypeEnum;
import models.DateParser;
import models.PriceFormatter;
import models.TableData;
import models.database.DataReader;
import models.database.dataManagers.CompanyDataManager;
import models.invoices.PurchaseInvoice;
import models.invoices.Invoice;
import models.invoices.SaleInvoice;
import views.DialogMessage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditSaleInvoiceController extends AbstractInvoiceOperationController implements EditOperation {
    protected int id;
    protected Invoice invoice;

    public EditSaleInvoiceController(){
        invoiceType = DataTypeEnum.SALE_INVOICES;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }

    @Override
    public void loadDataToComponents() {
        if (invoice != null) {
            this.invoiceNumberField.setText(invoice.getNumber());
            this.issueDateField.setValue(DateParser.parseCalendarToLocalDate(invoice.getIssueDate()));
            this.receiveDateField.setValue(DateParser.parseCalendarToLocalDate(invoice.getReceiveDate()));
            this.postingDateField.setValue(DateParser.parseCalendarToLocalDate(invoice.getPostingDate()));
            this.registerBox.getSelectionModel().select(invoice.getRegisterType());
            this.loadContractorData();
            this.wholeGrossPrice.setText(PriceFormatter.changePriceToPrintFormat(String.valueOf(invoice.getWholePrice())));
            this.zeroPercentTax.setText(PriceFormatter.changePriceToPrintFormat(String.valueOf(invoice.getAmountWithoutTax())));
            loadPrices();
        }
    }

    protected void loadContractorData() {
        DataReader reader = new DataReader(new CompanyDataManager(DataTypeEnum.CONTRACTORS).createFilePathFromGivenData());
        String[][] contractorData = reader.getFileData();
        if (contractorData != null && invoice.getContractorNumber() != -1) {
            try {
                for (String[] data : contractorData) {
                    int currentContractorID = Integer.parseInt(data[0]);
                    if (currentContractorID == invoice.getContractorNumber()) {
                        int selectIndex = 0;
                        for (CompanyContractor company : allCompanies) {
                            if (company.getId() == Integer.parseInt(data[0])) {
                                break;
                            }
                            selectIndex++;
                        }
                        contractorBox.getSelectionModel().select(selectIndex);
                        setContractorData(invoice.getContractorNumber());
                    }
                }
            } catch (NumberFormatException ex) {
                log.saveError("Wrong contractor ID in loading data to edit window.");
            }
        }

    }

    protected void loadPrices() {
        //   0   -  1  -   2
        // Netto - VAT - Brutto
        double[][] prices = new double[3][];
        double[] percents = {0.23, 0.08, 0.05};

        for (int i = 0; i < percents.length; i++) {
            prices[i] = new double[3];
            if (i == 0) {
                prices[i][0] = invoice.getHighestTaxAmount();
            } else if (i == 1) {
                prices[i][0] = invoice.getMediumTaxAmount();
            } else {
                prices[i][0] = invoice.getLowestTaxAmount();
            }
            countPrices(prices[i], percents[i]);
        }

        TextField[][] priceFields = new TextField[3][];
        priceFields[0] = pricesHighestTax;
        priceFields[1] = pricesMediumTax;
        priceFields[2] = pricesLowestTax;
        for (int i = 0; i < prices.length; i++) {
            for (int j = 0; j < priceFields[i].length; j++) {
                priceFields[i][j].setText(PriceFormatter.changePriceToPrintFormat(String.valueOf(prices[i][j])));
            }
        }
    }

    private void countPrices(double[] prices, double percent) {
        if (prices != null) {
            for (int i = 0; i < prices.length; i++) {
                if (prices.length == 3) {
                    prices[2] = prices[0] * (1 + percent);
                    prices[1] = prices[2] - prices[0];
                }
            }
        }
    }

    public void editInvoice() {
        Invoice invoice;
        if(invoiceType == DataTypeEnum.SALE_INVOICES){
            invoice = new SaleInvoice();
        } else {
            invoice = new PurchaseInvoice();
        }

        currentContractorIndex = contractorBox.getSelectionModel().selectedIndexProperty().get();
        setInvoiceData(invoice, getComponentsData());
        boolean correctData = checkInvoiceConditions(invoice);

        if (correctData && pricesAreCorrect() && id != -1) {
            if(!this.invoice.equals(invoice)) {
                CompanyDataManager manager = new CompanyDataManager(invoiceType, InvoiceCellController.getYear(), InvoiceCellController.getMonth());
                int result = manager.editData(this.id, invoice);
                if (result < 0) {
                    DialogMessage message = new DialogMessage(Alert.AlertType.ERROR);
                    message.showDialog("Edytowanie faktury nie powiodło się.");
                } else {
                    refreshControllerList();
                }
            }
            this.cancel();
        } else {
            DialogMessage message = new DialogMessage(Alert.AlertType.ERROR);
            message.showDialog("Podane dane są nieprawidłowe");
        }
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public void setTableData(TableData data) {
        this.invoice = (Invoice) data;
    }
}
