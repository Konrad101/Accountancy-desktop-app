package models.invoices;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import models.DateParser;

import java.util.Calendar;

public class PurchaseInvoice extends Invoice {
    private static int highestID;

    private boolean paid = false;
    private Calendar deadline;

    public PurchaseInvoice() {
        super();
    }

    public PurchaseInvoice(PurchaseInvoice invoice) {
        super(invoice);
        this.paid = invoice.paid;
        this.deadline = invoice.deadline;

        this.id = ++highestID;
    }

    public static void setHighestID(int highestID) {
        PurchaseInvoice.highestID = highestID;
    }

    @Override
    public String[] getData() {
        String[][] data = new String[2][];
        data[0] = super.getData();
        data[0][0] = String.valueOf(id);
        data[1] = new String[data[0].length + 2];
        System.arraycopy(data[0], 0, data[1], 0, data[0].length);
        int lastSuperIndex = data[0].length - 1;
        data[1][lastSuperIndex + 1] = String.valueOf(paid);
        data[1][lastSuperIndex + 2] = DateParser.parseDate(deadline);

        return data[1];
    }

    public Calendar getDeadline() {
        return deadline;
    }

    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public StringProperty getCellDeadline() {
        return new SimpleStringProperty(this.getDatePrintFormat(DateParser.parseDate(this.deadline)));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PurchaseInvoice) {
            PurchaseInvoice invoice = (PurchaseInvoice) obj;
            boolean isEqual = super.equals(obj);
            if (isEqual) {
                if (this.paid != invoice.paid) {
                    isEqual = false;
                } else if (!this.deadline.equals(invoice.deadline)) {
                    isEqual = false;
                }
            }
            return isEqual;
        }

        return super.equals(obj);
    }
}
