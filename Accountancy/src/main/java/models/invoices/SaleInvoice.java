package models.invoices;

public class SaleInvoice extends Invoice {
    private static int highestID;

    public SaleInvoice() {
        super();
    }

    public SaleInvoice(SaleInvoice invoice) {
        super(invoice);
        this.id = ++highestID;
    }

    public static void setHighestID(int highestID) {
        SaleInvoice.highestID = highestID;
    }

    @Override
    public String[] getData() {
        String[] data = super.getData();
        data[0] = String.valueOf(id);
        return data;
    }
}
