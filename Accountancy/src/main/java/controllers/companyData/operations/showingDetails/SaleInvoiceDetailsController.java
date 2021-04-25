package controllers.companyData.operations.showingDetails;

import controllers.companyData.invoiceControllers.InvoiceCellController;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Contractor;
import models.DateParser;
import models.PriceFormatter;
import models.TableData;
import models.database.dataManagers.TaxManager;
import models.invoices.Invoice;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SaleInvoiceDetailsController extends AbstractDetailsController implements Initializable, DetailsOperation {
    public Label registerTypeLabel;
    public Label numberLabel;
    public Label issueDateLabel;
    public Label receiveDateLabel;
    public Label postingDateLabel;

    public Label companyNameLabel;
    public Label personDataLabel;
    public Label addressLabel;

    public Label wholePriceLabel;

    public Label netPriceHighestTaxAmountLabel;
    public Label vatHighestTaxAmountLabel;
    public Label grossPriceHighestTaxAmountLabel;
    public Label netPriceMediumTaxAmountLabel;
    public Label vatMediumTaxAmountLabel;
    public Label grossPriceMediumTaxAmountLabel;
    public Label netPriceLowestTaxAmountLabel;
    public Label vatLowestTaxAmountLabel;
    public Label grossPriceLowestTaxAmountLabel;
    public Label zeroPercentTaxLabel;

    public Label highestTaxLabel;
    public Label mediumTaxLabel;
    public Label lowestTaxLabel;

    protected List<Label> labels = new ArrayList<>();
    protected int pricesStartIndex = 0;
    protected Invoice invoice;

    private double highestTax;
    private double mediumTax;
    private double lowestTax;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createLabelsList();
        this.initializeTaxLabels();

        for (Label label : labels) {
            centerLabelText(label);
            changeLabelFontSize(label);
        }
    }

    private void initializeTaxLabels() {
        TaxManager taxManager = new TaxManager(InvoiceCellController.getMonth(), InvoiceCellController.getYear());
        double highestTaxPercent = taxManager.getHighestTax();
        double mediumTaxPercent = taxManager.getMediumTax();
        double lowestTaxPercent = taxManager.getLowestTax();

        setLabelPercentageInfo(highestTaxLabel, highestTaxPercent);
        setLabelPercentageInfo(mediumTaxLabel, mediumTaxPercent);
        setLabelPercentageInfo(lowestTaxLabel, lowestTaxPercent);

        this.highestTax = 1 + (taxManager.getHighestTax() / 100.);
        this.mediumTax = 1 + (taxManager.getMediumTax() / 100.);
        this.lowestTax = 1 + (taxManager.getLowestTax() / 100.);
    }

    private void createLabelsList() {
        labels.add(registerTypeLabel);
        labels.add(numberLabel);
        labels.add(issueDateLabel);
        labels.add(receiveDateLabel);
        labels.add(postingDateLabel);
        labels.add(companyNameLabel);
        labels.add(personDataLabel);
        labels.add(addressLabel);
        labels.add(wholePriceLabel);
        labels.add(netPriceHighestTaxAmountLabel);
        labels.add(vatHighestTaxAmountLabel);
        labels.add(grossPriceHighestTaxAmountLabel);
        labels.add(netPriceMediumTaxAmountLabel);
        labels.add(vatMediumTaxAmountLabel);
        labels.add(grossPriceMediumTaxAmountLabel);
        labels.add(netPriceLowestTaxAmountLabel);
        labels.add(vatLowestTaxAmountLabel);
        labels.add(grossPriceLowestTaxAmountLabel);
        labels.add(zeroPercentTaxLabel);
        pricesStartIndex = labels.indexOf(netPriceHighestTaxAmountLabel);
    }

    @Override
    public void setTableData(TableData data) {
        if (data instanceof Invoice) {
            this.invoice = (Invoice) data;
        }
    }

    @Override
    public void loadDataToComponents() {
        if (this.invoice != null) {
            this.registerTypeLabel.setText(invoice.getRegisterType());
            this.numberLabel.setText(invoice.getNumber());
            this.issueDateLabel.setText(invoice.getDatePrintFormat(DateParser.parseDate(invoice.getIssueDate())));
            this.receiveDateLabel.setText(invoice.getDatePrintFormat(DateParser.parseDate(invoice.getReceiveDate())));
            this.postingDateLabel.setText(invoice.getDatePrintFormat(DateParser.parseDate(invoice.getPostingDate())));

            Contractor contractor = Contractor.getContractorByID(invoice.getContractorNumber());
            if (contractor != null) {
                this.companyNameLabel.setText(contractor.getCompanyName());
                this.personDataLabel.setText(contractor.getPersonData());
                this.addressLabel.setText(contractor.getCompanyAddress() + ", " + contractor.getCompanyCity());
            }

            this.wholePriceLabel.setText(PriceFormatter.changePriceToPrintFormat(String.valueOf(invoice.getWholePrice())) + " zł");
            double netPrice = invoice.getHighestTaxAmount();
            this.netPriceHighestTaxAmountLabel.setText(PriceFormatter.changePriceToPrintFormat(String.valueOf(netPrice)) + " zł");
            this.vatHighestTaxAmountLabel.setText(PriceFormatter.changePriceToPrintFormat(String.valueOf((netPrice * highestTax) - netPrice)) + " zł");
            this.grossPriceHighestTaxAmountLabel.setText(PriceFormatter.changePriceToPrintFormat(String.valueOf(netPrice * highestTax)) + " zł");
            netPrice = invoice.getMediumTaxAmount();
            this.netPriceMediumTaxAmountLabel.setText(PriceFormatter.changePriceToPrintFormat(String.valueOf(netPrice)) + " zł");
            this.vatMediumTaxAmountLabel.setText(PriceFormatter.changePriceToPrintFormat(String.valueOf((netPrice * mediumTax) - netPrice)) + " zł");
            this.grossPriceMediumTaxAmountLabel.setText(PriceFormatter.changePriceToPrintFormat(String.valueOf(netPrice * mediumTax)) + " zł");
            netPrice = invoice.getLowestTaxAmount();
            this.netPriceLowestTaxAmountLabel.setText(PriceFormatter.changePriceToPrintFormat(String.valueOf(netPrice)) + " zł");
            this.vatLowestTaxAmountLabel.setText(PriceFormatter.changePriceToPrintFormat(String.valueOf((netPrice * lowestTax) - netPrice)) + " zł");
            this.grossPriceLowestTaxAmountLabel.setText(PriceFormatter.changePriceToPrintFormat(String.valueOf(netPrice * lowestTax)) + " zł");
            this.zeroPercentTaxLabel.setText(PriceFormatter.changePriceToPrintFormat(String.valueOf(invoice.getAmountWithoutTax())) + " zł");
        }
    }

    public void cancel() {
        this.turnOffStage((Stage) grossPriceLowestTaxAmountLabel.getScene().getWindow());
    }

}
