package models.invoices;

import controllers.companyData.invoiceControllers.InvoiceCellController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import models.DateParser;
import models.PriceFormatter;
import models.TableData;
import models.TableDataPattern;
import models.database.dataManagers.TaxManager;

import java.util.Calendar;

public abstract class Invoice extends TableData implements Comparable<Invoice>, TableDataPattern {
    // Invoice details
    protected String registerType;
    protected String number;
    protected Calendar postingDate;
    protected Calendar receiveDate;
    protected Calendar issueDate;

    // Contractor's id
    protected int contractorNumber;

    private static final String CURRENCY = "zł";

    // everything is net amount - without tax
    protected double highestTaxAmount = 0;
    protected double mediumTaxAmount = 0;
    protected double lowestTaxAmount = 0;
    protected double amountWithoutTax = 0;

    public Invoice() {
        this.id = 0;
    }

    public Invoice(Invoice invoice) {
        if (invoice != null) {
            this.registerType = invoice.registerType;
            this.number = invoice.number;
            this.postingDate = invoice.postingDate;
            this.receiveDate = invoice.receiveDate;
            this.issueDate = invoice.issueDate;

            this.contractorNumber = invoice.contractorNumber;

            this.highestTaxAmount = invoice.highestTaxAmount;
            this.mediumTaxAmount = invoice.mediumTaxAmount;
            this.lowestTaxAmount = invoice.lowestTaxAmount;
            this.amountWithoutTax = invoice.amountWithoutTax;
        }
    }

    //// SETTERS

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public void setPostingDate(Calendar postingDate) {
        this.postingDate = postingDate;
    }

    public void setReceiveDate(Calendar receiveDate) {
        this.receiveDate = receiveDate;
    }

    public void setIssueDate(Calendar issueDate) {
        this.issueDate = issueDate;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setRate0percent(double freeTaxAmount) {
        this.amountWithoutTax = freeTaxAmount;
    }

    public void setLowestTaxAmount(double lowestTaxAmount) {
        this.lowestTaxAmount = lowestTaxAmount;
    }

    public void setMediumTaxAmount(double mediumTaxAmount) {
        this.mediumTaxAmount = mediumTaxAmount;
    }

    public void setHighestTaxAmount(double highestTaxAmount) {
        this.highestTaxAmount = highestTaxAmount;
    }

    public void setContractorNumber(int contractorNumber) {
        this.contractorNumber = contractorNumber;
    }

    //// GETTERS

    public Calendar getIssueDate() {
        return issueDate;
    }

    public Calendar getPostingDate() {
        return postingDate;
    }

    public Calendar getReceiveDate() {
        return receiveDate;
    }

    public double getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public double getLowestTaxAmount() {
        return lowestTaxAmount;
    }

    public double getMediumTaxAmount() {
        return mediumTaxAmount;
    }

    public double getHighestTaxAmount() {
        return highestTaxAmount;
    }

    public int getContractorNumber() {
        return contractorNumber;
    }

    public String getNumber() {
        return number;
    }

    public String getRegisterType() {
        return registerType;
    }

    ////

    @Override
    public String[] getData() {
        String[] data = new String[11];
        int id = 0;
        data[0] = String.valueOf(id);
        data[1] = this.registerType;
        data[2] = this.number;
        data[3] = String.valueOf(this.contractorNumber);
        data[4] = DateParser.parseDate(this.postingDate);
        data[5] = DateParser.parseDate(this.receiveDate);
        data[6] = DateParser.parseDate(this.issueDate);
        data[7] = String.valueOf(this.highestTaxAmount);
        data[8] = String.valueOf(this.mediumTaxAmount);
        data[9] = String.valueOf(this.lowestTaxAmount);
        data[10] = String.valueOf(this.amountWithoutTax);
        return data;
    }

    public boolean dataIsIncorrect(String data) {
        return stringIsEmpty(data);
    }

    public boolean dateIsIncorrect(Calendar date) {
        return date == null;
    }

    public boolean contractorNumberIsIncorrect() {
        return contractorNumber <= 0;
    }

    public boolean ratesAreIncorrect() {
        return highestTaxAmount + mediumTaxAmount + lowestTaxAmount + amountWithoutTax == 0;
    }

    public boolean postingDateIsIncorrect() {
        if (dateIsIncorrect(postingDate) || dateIsIncorrect(receiveDate)) {
            return false;
        }

        return postingDate.compareTo(receiveDate) > 0;
    }

    public double getWholePrice() {
        TaxManager taxManager = new TaxManager(InvoiceCellController.getMonth(), InvoiceCellController.getYear());
        double highestTax = 1 + (taxManager.getHighestTax() / 100.);
        double mediumTax = 1 + (taxManager.getMediumTax() / 100.);
        double lowestTax = 1 + (taxManager.getLowestTax() / 100.);

        double price = highestTaxAmount * highestTax + mediumTaxAmount * mediumTax + lowestTaxAmount * lowestTax + amountWithoutTax;
        price *= 100;
        price = Math.round(price);

        return price / 100;
    }

    public StringProperty getGrossPrice() {
        String price = String.valueOf(this.getWholePrice());
        price = PriceFormatter.changePriceToPrintFormat(price) + " " + CURRENCY;
        return new SimpleStringProperty(price);
    }

    public StringProperty getVATPrice() {
        String price = String.valueOf((this.getWholePrice() - amountWithoutTax) - (this.lowestTaxAmount + this.mediumTaxAmount + this.highestTaxAmount));
        price = PriceFormatter.changePriceToPrintFormat(price) + " " + CURRENCY;
        return new SimpleStringProperty(price);
    }

    public StringProperty getNetPrice() {
        String price = String.valueOf(this.highestTaxAmount + this.mediumTaxAmount + this.lowestTaxAmount + this.amountWithoutTax);
        price = PriceFormatter.changePriceToPrintFormat(price) + " " + CURRENCY;
        return new SimpleStringProperty(price);
    }

    public StringProperty getCellNumber() {
        return new SimpleStringProperty(number);
    }

    public StringProperty getCellReceiveDate() {
        return new SimpleStringProperty(this.getDatePrintFormat(DateParser.parseDate(this.receiveDate)));
    }

    public String getDatePrintFormat(String date) {
        if (date == null) {
            return null;
        }

        String[] arrDate = date.split("-");
        if (arrDate.length == 3) {
            String day = arrDate[0];
            String month = arrDate[1];
            String year = arrDate[2];

            if (day.length() < 2) {
                day = "0" + day;
            }
            if (month.length() < 2) {
                month = "0" + month;
            }

            date = day + "." + month + "." + year;
        }

        return date;
    }

    @Override
    public int compareTo(Invoice o) {
        Calendar firstDate = this.receiveDate;
        Calendar secondDate = o.receiveDate;

        if (firstDate.get(Calendar.YEAR) >= secondDate.get(Calendar.YEAR)) {
            if (firstDate.get(Calendar.YEAR) == secondDate.get(Calendar.YEAR)) {
                if (firstDate.get(Calendar.MONTH) >= secondDate.get(Calendar.MONTH)) {
                    if (firstDate.get(Calendar.MONTH) == secondDate.get(Calendar.MONTH)) {
                        if (firstDate.get(Calendar.DAY_OF_MONTH) > secondDate.get(Calendar.DAY_OF_MONTH)) {
                            return 1;
                        } else if (firstDate.get(Calendar.DAY_OF_MONTH) == secondDate.get(Calendar.DAY_OF_MONTH)) {
                            return 0;
                        }
                    } else {
                        return 1;
                    }
                }
            } else {
                return 1;
            }
        }

        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Invoice) {
            Invoice invoice = (Invoice) obj;
            boolean isEqual = true;
            if (!this.registerType.equals(invoice.registerType)
                    || !this.number.equals(invoice.number)) {
                isEqual = false;
            } else if (!this.postingDate.equals(invoice.postingDate)
                    || !this.receiveDate.equals(invoice.receiveDate)
                    || !this.issueDate.equals(invoice.issueDate)) {
                isEqual = false;
            } else if (this.contractorNumber != invoice.contractorNumber) {
                isEqual = false;
            } else if (this.highestTaxAmount != invoice.highestTaxAmount
                    || this.mediumTaxAmount != invoice.mediumTaxAmount
                    || this.lowestTaxAmount != invoice.lowestTaxAmount
                    || this.amountWithoutTax != invoice.amountWithoutTax) {
                isEqual = false;
            }

            return isEqual;
        }

        return super.equals(obj);
    }
}