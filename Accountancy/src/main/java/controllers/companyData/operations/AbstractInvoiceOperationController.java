package controllers.companyData.operations;

import controllers.companyData.invoiceControllers.InvoiceCellController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import models.DataTypeEnum;
import models.DateParser;
import models.PriceFormatter;
import models.database.DataReader;
import models.database.dataManagers.CompanyDataManager;
import models.database.Log;
import models.database.dataManagers.TaxManager;
import models.invoices.PurchaseInvoice;
import models.invoices.Invoice;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractInvoiceOperationController extends OperationController implements Initializable {
    private enum TaxesTypes {
        BRUTTO, NETTO, VAT
    }

    protected Log log;

    protected List<CompanyContractor> allCompanies = new ArrayList<>();
    private final ObservableList<String> resultCompanies = FXCollections.observableArrayList();

    protected TextField[] pricesHighestTax = new TextField[3];
    protected TextField[] pricesMediumTax = new TextField[3];
    protected TextField[] pricesLowestTax = new TextField[3];

    public TextField invoiceNumberField;
    public DatePicker issueDateField;
    public DatePicker receiveDateField;
    public DatePicker postingDateField;
    public ChoiceBox<String> registerBox;
    public ComboBox<String> contractorBox;
    public TextField nameField;
    public TextField addressField;
    public TextField wholeGrossPrice;
    public TextField netPriceHighestTax;
    public TextField netPriceMediumTax;
    public TextField netPriceLowestTax;
    public TextField zeroPercentTax;
    public TextField grossPriceHighestTax;
    public TextField grossPriceMediumTax;
    public TextField grossPriceLowestTax;
    public TextField vatHighestTax;
    public TextField vatMediumTax;
    public TextField vatLowestTax;

    public Label highestTaxLabel;
    public Label mediumTaxLabel;
    public Label lowestTaxLabel;

    // Only for PurchaseInvoicesController
    public CheckBox payCheckBox;
    public DatePicker paybackDate;

    protected Number currentContractorIndex = -1;
    private String currentSearchingContractorPhrase = "";
    private long lastEnterPressTime = 0;

    protected DataTypeEnum invoiceType;

    private double highestTax;
    private double mediumTax;
    private double lowestTax;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log = new Log();
        loadAllContractors();
        resultCompanies.addAll(getAllNames());

        initializeTaxes();
        initializeTaxLabels();

        initializeDateActions();
        initializeWholePriceEffectOnChange();
        setRegisters();
        setPriceFields();
        setContractorBox();
    }

    private void initializeTaxes() {
        TaxManager taxManager = new TaxManager(InvoiceCellController.getMonth(), InvoiceCellController.getYear());
        this.highestTax = taxManager.getHighestTax();
        this.mediumTax = taxManager.getMediumTax();
        this.lowestTax = taxManager.getLowestTax();
    }

    private void initializeTaxLabels() {
        setLabelPercentageInfo(highestTaxLabel, highestTax);
        setLabelPercentageInfo(mediumTaxLabel, mediumTax);
        setLabelPercentageInfo(lowestTaxLabel, lowestTax);
    }

    private void initializeWholePriceEffectOnChange() {
        wholeGrossPrice.textProperty().addListener((observableValue, s, t1) -> setNoneEffect(wholeGrossPrice, true));
    }

    private void initializeDateActions() {
        long timeBetweenChanges = 200;
        AtomicLong lastChangeTime = new AtomicLong();
        issueDateField.setOnAction(e -> {
            if (System.currentTimeMillis() - lastChangeTime.get() > timeBetweenChanges) {
                lastChangeTime.set(System.currentTimeMillis());
                receiveDateField.setValue(issueDateField.getValue());
                setNoneEffect(receiveDateField, true);
            }
        });
        receiveDateField.setOnAction(e -> {
            if (System.currentTimeMillis() - lastChangeTime.get() > timeBetweenChanges) {
                lastChangeTime.set(System.currentTimeMillis());
                issueDateField.setValue(receiveDateField.getValue());
                setNoneEffect(issueDateField, true);
            }
        });
    }

    public void cancel() {
        turnOffStage((Stage) invoiceNumberField.getScene().getWindow());
    }

    protected String[] getComponentsData() {
        List<String> data = new ArrayList<>();

        data.add(invoiceNumberField.getText());
        data.add(DateParser.parseLocalDate(issueDateField.getValue()));
        data.add(DateParser.parseLocalDate(receiveDateField.getValue()));
        data.add(DateParser.parseLocalDate(postingDateField.getValue()));
        data.add(registerBox.getSelectionModel().getSelectedItem());
        data.add(String.valueOf(getContractorID(this.currentSearchingContractorPhrase, this.currentContractorIndex.intValue())));
        data.add(netPriceHighestTax.getText().replace(",", "."));
        data.add(netPriceMediumTax.getText().replace(",", "."));
        data.add(netPriceLowestTax.getText().replace(",", "."));
        data.add(zeroPercentTax.getText().replace(",", "."));
        if (invoiceType == DataTypeEnum.PURCHASE_INVOICES) {
            data.add(String.valueOf(payCheckBox.isSelected()));
            data.add(DateParser.parseLocalDate(paybackDate.getValue()));
        }

        String[] arrData = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            arrData[i] = data.get(i);
        }
        return arrData;
    }

    private void setPriceFields() {
        fieldTextToRight(wholeGrossPrice);
        fillPricesArrays();
        for (TextField field : pricesHighestTax) {
            fieldTextToRight(field);
        }
        for (TextField field : pricesMediumTax) {
            fieldTextToRight(field);
        }
        for (TextField field : pricesLowestTax) {
            fieldTextToRight(field);
        }
        fieldTextToRight(zeroPercentTax);

        setPriceTextField(zeroPercentTax, TaxesTypes.NETTO, 0);
        setPriceTextField(netPriceHighestTax, TaxesTypes.NETTO, highestTax);
        setPriceTextField(netPriceMediumTax, TaxesTypes.NETTO, mediumTax);
        setPriceTextField(netPriceLowestTax, TaxesTypes.NETTO, lowestTax);
        setPriceTextField(vatHighestTax, TaxesTypes.VAT, highestTax);
        setPriceTextField(vatMediumTax, TaxesTypes.VAT, mediumTax);
        setPriceTextField(vatLowestTax, TaxesTypes.VAT, lowestTax);
        setPriceTextField(grossPriceHighestTax, TaxesTypes.BRUTTO, highestTax);
        setPriceTextField(grossPriceMediumTax, TaxesTypes.BRUTTO, mediumTax);
        setPriceTextField(grossPriceLowestTax, TaxesTypes.BRUTTO, lowestTax);
    }

    private void setRegisters() {
        registerBox.getItems().addAll("Faktura VAT", "Faktura zaliczkowa", "Faktura końcowa", "Faktura walutowa");
    }

    private void fillPricesArrays() {
        pricesHighestTax[0] = netPriceHighestTax;
        pricesHighestTax[1] = vatHighestTax;
        pricesHighestTax[2] = grossPriceHighestTax;

        pricesMediumTax[0] = netPriceMediumTax;
        pricesMediumTax[1] = vatMediumTax;
        pricesMediumTax[2] = grossPriceMediumTax;

        pricesLowestTax[0] = netPriceLowestTax;
        pricesLowestTax[1] = vatLowestTax;
        pricesLowestTax[2] = grossPriceLowestTax;
    }

    private void setPriceTextField(TextField field, TaxesTypes taxType, double percent) {
        field.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                String number = field.getText().replace(",", ".");
                number = number.replace("zł", "");
                number = number.replace("pln", "");
                double price = 0;
                boolean isNumber = true;
                try {
                    price = Double.parseDouble(number);
                } catch (NumberFormatException ex) {
                    isNumber = false;
                }

                if (isNumber) {
                    TextField[] fields = null;
                    if (percent == this.highestTax) {
                        fields = pricesHighestTax;
                    } else if (percent == this.mediumTax) {
                        fields = pricesMediumTax;
                    } else if (percent == this.lowestTax) {
                        fields = pricesLowestTax;
                    }

                    switch (taxType) {
                        case NETTO:
                            if (fields != null && fields.length > 2) {
                                double grossPrice = price * (1 + (percent / 100));
                                fields[0].setText(changeNumberToCurrencyFormat(price));
                                fields[1].setText(changeNumberToCurrencyFormat(grossPrice - price));
                                fields[2].setText(changeNumberToCurrencyFormat(grossPrice));
                            } else {
                                field.setText(changeNumberToCurrencyFormat(price));
                            }
                            break;
                        case VAT:
                            if (fields != null && fields.length > 2) {
                                double netPrice = price / (percent / 100);
                                fields[0].setText(changeNumberToCurrencyFormat(netPrice));
                                fields[1].setText(changeNumberToCurrencyFormat(price));
                                fields[2].setText(changeNumberToCurrencyFormat(netPrice + price));
                            }
                            break;
                        case BRUTTO:
                            if (fields != null && fields.length > 2) {
                                double netPrice = price / (1 + (percent / 100));
                                fields[0].setText(changeNumberToCurrencyFormat(netPrice));
                                fields[1].setText(changeNumberToCurrencyFormat(price - netPrice));
                                fields[2].setText(changeNumberToCurrencyFormat(price));
                            }
                    }

                    double wholePrice;
                    double[] prices = new double[4];
                    for (int i = 0; i < prices.length; i++) {
                        TextField priceField;
                        if (i == 0) {
                            priceField = grossPriceHighestTax;
                        } else if (i == 1) {
                            priceField = grossPriceMediumTax;
                        } else if (i == 2) {
                            priceField = grossPriceLowestTax;
                        } else {
                            priceField = zeroPercentTax;
                        }

                        try {
                            prices[i] = Double.parseDouble(priceField.getText().replace(",", "."));
                        } catch (NumberFormatException ex) {
                            prices[i] = 0;
                        }
                    }

                    wholePrice = prices[0] + prices[1] + prices[2] + prices[3];
                    wholeGrossPrice.setText(changeNumberToCurrencyFormat(wholePrice));
                }
            }
        });
    }

    private String changeNumberToCurrencyFormat(double number) {
        return PriceFormatter.changePriceToPrintFormat(String.valueOf(number));
    }

    private void setContractorBox() {
        if (resultCompanies.size() > 0) {
            contractorBox.setItems(resultCompanies);
            contractorBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
                if (t1.intValue() == -1) {
                    if (contractorBox.getEditor().getText().isEmpty()) {
                        indexChanged(t1);
                    }
                } else {
                    indexChanged(t1);
                }

                if (System.currentTimeMillis() - lastEnterPressTime > 350) {
                    boolean contractorIsSelected = false;

                    for (CompanyContractor company : allCompanies) {
                        if (company.getName().equals(contractorBox.getSelectionModel().getSelectedItem())) {
                            contractorIsSelected = true;
                            break;
                        }
                    }

                    if (contractorIsSelected) {
                        int id = getContractorID(currentSearchingContractorPhrase, currentContractorIndex.intValue());
                        setContractorData(id);
                    }
                }
            });

            contractorBox.setOnKeyPressed(event -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    lastEnterPressTime = System.currentTimeMillis();
                    String searchingText = contractorBox.getEditor().getText();
                    currentSearchingContractorPhrase = searchingText;
                    ObservableList<String> list = FXCollections.observableArrayList(getResultList(searchingText));
                    if (searchingText.equals("")) {
                        changeContractorAddressAndPersonalData("", "");
                        indexChanged(-1);
                    }
                    contractorBox.setItems(list);
                    contractorBox.getSelectionModel().clearSelection();
                    contractorBox.show();
                }
            });
        }
    }

    protected void setContractorData(int contractorID) {
        DataReader reader = new DataReader(new CompanyDataManager(DataTypeEnum.CONTRACTORS).createFilePathFromGivenData());
        String[][] contractorData = reader.getFileData();
        if (contractorData != null && contractorID != -1) {
            try {
                for (String[] data : contractorData) {
                    if (data.length > 0) {
                        if (Integer.parseInt(data[0]) == contractorID) {
                            if (data.length > 5) {
                                String personalData = "";
                                String address = data[3] + ", " + data[5];
                                addressField.setText(address);
                                if (data.length > 6) {
                                    personalData = data[6];
                                    nameField.setText(personalData);
                                }

                                changeContractorAddressAndPersonalData(address, personalData);
                            }
                            break;
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                log.saveError("Wrong ID number while setting contractor's data");
            }
        }
    }

    protected void setInvoiceData(Invoice invoice, String[] data) {
        if (invoice != null && data != null) {
            if (data.length >= 9) {
                invoice.setNumber(invoice.removeEmptyCharsFromEnd(data[0]));
                invoice.setIssueDate(DateParser.parseStringToDate(data[1]));
                invoice.setReceiveDate(DateParser.parseStringToDate(data[2]));
                invoice.setPostingDate(DateParser.parseStringToDate(data[3]));
                invoice.setRegisterType(data[4]);
                try {
                    invoice.setContractorNumber(Integer.parseInt(data[5]));
                } catch (NumberFormatException ex) {
                    log.saveError("Wrong contractor ID in setting invoice data");
                }
                try {
                    invoice.setHighestTaxAmount(Double.parseDouble(data[6]));
                } catch (NumberFormatException ignored) {
                }
                try {
                    invoice.setMediumTaxAmount(Double.parseDouble(data[7]));
                } catch (NumberFormatException ignored) {
                }
                try {
                    invoice.setLowestTaxAmount(Double.parseDouble(data[8]));
                } catch (NumberFormatException ignored) {
                }
                try {
                    invoice.setRate0percent(Double.parseDouble(data[9]));
                } catch (NumberFormatException ignored) {
                }

                if (invoiceType == DataTypeEnum.PURCHASE_INVOICES) {
                    PurchaseInvoice purchaseInvoice = (PurchaseInvoice) invoice;
                    purchaseInvoice.setPaid(Boolean.parseBoolean(data[10]));
                    purchaseInvoice.setDeadline(DateParser.parseStringToDate(data[11]));
                }
            }
        }
    }

    protected boolean checkInvoiceConditions(Invoice invoice) {
        boolean dataIsCorrect = true;
        if (invoice != null) {
            if (invoice.dataIsIncorrect(invoice.getRegisterType())) {
                setLightWrongDataFieldEffect(registerBox);
                dataIsCorrect = false;
            }
            if (invoice.dataIsIncorrect(invoice.getNumber())) {
                setLightWrongDataFieldEffect(invoiceNumberField);
                dataIsCorrect = false;
            }
            if (invoice.contractorNumberIsIncorrect()) {
                setLightWrongDataFieldEffect(contractorBox);
                dataIsCorrect = false;
            }
            if (invoice.dateIsIncorrect(invoice.getPostingDate())) {
                setLightWrongDataFieldEffect(postingDateField);
                dataIsCorrect = false;
            }
            if (invoice.dateIsIncorrect(invoice.getReceiveDate())) {
                setLightWrongDataFieldEffect(receiveDateField);
                dataIsCorrect = false;
            }
            if (invoice.dateIsIncorrect(invoice.getIssueDate())) {
                setLightWrongDataFieldEffect(issueDateField);
                dataIsCorrect = false;
            }
            if (invoice.ratesAreIncorrect()) {
                setLightWrongDataFieldEffect(wholeGrossPrice);
                dataIsCorrect = false;
            }
            if (invoice.postingDateIsIncorrect()) {
                setLightWrongDataFieldEffect(postingDateField);
                dataIsCorrect = false;
            }
            if (invoiceType == DataTypeEnum.PURCHASE_INVOICES) {
                if (invoice instanceof PurchaseInvoice && paybackDate != null) {
                    PurchaseInvoice purchaseInvoice = (PurchaseInvoice) invoice;
                    if (purchaseInvoice.dateIsIncorrect(purchaseInvoice.getDeadline())) {
                        setLightWrongDataFieldEffect(paybackDate);
                        dataIsCorrect = false;
                    }
                } else {
                    log.saveError("Wrong invoice type in checking conditions");
                }
            }
        }
        return dataIsCorrect;
    }

    protected boolean pricesAreCorrect() {
        int[] results = new int[4];
        results[0] = priceFieldsAreCorrect(pricesHighestTax);
        results[1] = priceFieldsAreCorrect(pricesMediumTax);
        results[2] = priceFieldsAreCorrect(pricesLowestTax);
        results[3] = priceFieldIsCorrect(zeroPercentTax);

        for (Integer result : results) {
            if (result == -1) {
                return false;
            }
        }

        for (Integer result : results) {
            if (result == 1) {
                return true;
            }
        }

        return false;
    }

    private int priceFieldsAreCorrect(TextField[] fields) {
        if (fields == null) {
            return -1;
        }

        boolean containsNumber = false;
        int numbersAmount = 0;
        for (TextField field : fields) {
            int result = priceFieldIsCorrect(field);
            if (result == 1) {
                containsNumber = true;
                numbersAmount++;
            } else if (result == -1) {
                return result;
            }
        }

        if (containsNumber) {
            if (numbersAmount == 3) {
                return 1;
            } else {
                return -1;
            }
        }
        return 0;
    }

    /**
     * @return 1 if there is a number, 0 if null, if wrong input -1
     */
    private int priceFieldIsCorrect(TextField priceField) {
        if (priceField == null) {
            return -1;
        }

        String price = priceField.getText().replace(",", ".");
        try {
            Double.parseDouble(price);
            return 1;
        } catch (NumberFormatException ex) {
            if (price.isEmpty()) {
                return 0;
            }
            return -1;
        }
    }

    private void changeContractorAddressAndPersonalData(String address, String personalData) {
        this.addressField.setText(address);
        this.nameField.setText(personalData);
    }

    private void indexChanged(Number newValue) {
        this.currentContractorIndex = newValue;
    }

    private ArrayList<String> getResultList(String phrase) {
        if (phrase == null || phrase.length() == 0) {
            return getAllNames();
        }

        ArrayList<String> filteredList = new ArrayList<>();
        ArrayList<Integer> indexes = new ArrayList<>();
        if (phrase.length() > 3) {
            for (int i = 0; i < resultCompanies.size(); i++) {
                String name = resultCompanies.get(i);
                if (name.toUpperCase().startsWith(phrase.toUpperCase())) {
                    filteredList.add(resultCompanies.get(i));
                    indexes.add(i);
                }
            }
        } else {
            for (String contractorName : resultCompanies) {
                if (contractorName.toUpperCase().startsWith(phrase.toUpperCase())) {
                    filteredList.add(contractorName);
                }
            }
        }

        if (phrase.length() > 3) {
            for (int i = 0; i < resultCompanies.size(); i++) {
                if (!indexes.contains(i)) {
                    String name = resultCompanies.get(i);
                    if (name.toUpperCase().contains(phrase.toUpperCase())) {
                        filteredList.add(resultCompanies.get(i));
                    }
                }
            }
        }

        if (filteredList.size() == 0) {
            return getAllNames();
        }

        return filteredList;
    }

    private ArrayList<String> getAllNames() {
        ArrayList<String> names = new ArrayList<>();
        if (allCompanies != null) {
            for (CompanyContractor company : allCompanies) {
                names.add(company.getName());
            }
        }
        return names;
    }

    private int getContractorID(String phrase, int index) {
        ArrayList<String> resultsForPhrase = getResultList(phrase);
        int phraseIndex = 0;
        for (CompanyContractor company : allCompanies) {
            if (resultsForPhrase.size() > phraseIndex) {
                if (resultsForPhrase.get(phraseIndex).equals(company.getName())) {
                    if (phraseIndex == index) {
                        return company.getId();
                    }
                    phraseIndex++;
                }
            }
        }

        return -1;
    }

    private void loadAllContractors() {
        DataReader reader = new DataReader(new CompanyDataManager(DataTypeEnum.CONTRACTORS).createFilePathFromGivenData());
        String[][] data = reader.getFileData();
        if (data != null) {
            try {
                for (String[] singleData : data) {
                    if (singleData.length >= 3) {
                        allCompanies.add(new CompanyContractor(Integer.parseInt(singleData[0]), singleData[2]));
                    }
                }
                Collections.sort(allCompanies);
            } catch (NumberFormatException ex) {
                log.saveError("Wrong contractor ID in loading contractors");
            }
        }
    }

    private void fieldTextToRight(TextField field) {
        if (field != null)
            field.setStyle("-fx-alignment: CENTER-RIGHT;");
    }

    protected static class CompanyContractor implements Comparable<CompanyContractor> {
        private final int id;
        private final String name;

        public CompanyContractor(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }


        @Override
        public int compareTo(CompanyContractor company) {
            if (company != null) {
                return this.name.toLowerCase().compareTo(company.name.toLowerCase());
            }
            return 0;
        }
    }
}