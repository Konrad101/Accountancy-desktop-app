package dataGenerator;

import models.DataTypeEnum;
import models.database.dataManagers.CompanyDataManager;
import models.invoices.PurchaseInvoice;

import java.util.GregorianCalendar;
import java.util.Random;

public class Generator {
    private void addInvoices(int amount) {
        // before launch
        /*DatabaseManager.setBasePath("programData/9131621948");
        DatabaseManager.setCurrentCompanyName("HOME DEVELOPMENT B.D.HAJDUGA SP.J.");
        addInvoices(400);*/

        CompanyDataManager manager = new CompanyDataManager(DataTypeEnum.PURCHASE_INVOICES, 2020, 7);
        PurchaseInvoice[] invoices = new PurchaseInvoice[amount];
        final int highestContractorID = new CompanyDataManager(DataTypeEnum.CONTRACTORS).getHighestID();

        String[] types = {"Faktura VAT", "Faktura zaliczkowa", "Faktura końcowa", "Faktura walutowa"};
        Random random = new Random();

        for (int i = 0; i < invoices.length; i++) {
            GregorianCalendar fstDate = new GregorianCalendar(2000 + random.nextInt(21), random.nextInt(12), 1 + random.nextInt(29));
            GregorianCalendar sndDate = new GregorianCalendar(2000 + random.nextInt(21), random.nextInt(12), 1 + random.nextInt(29));
            GregorianCalendar trdDate = new GregorianCalendar(2000 + random.nextInt(21), random.nextInt(12), 1 + random.nextInt(29));

            invoices[i] = new PurchaseInvoice();
            invoices[i].setRegisterType(types[random.nextInt(types.length)]);
            invoices[i].setNumber(String.valueOf(random.nextInt(10000)));
            invoices[i].setContractorNumber(1 + random.nextInt(highestContractorID));
            invoices[i].setPostingDate(fstDate);
            invoices[i].setReceiveDate(sndDate);
            invoices[i].setIssueDate(trdDate);
            invoices[i].setHighestTaxAmount(20 + random.nextInt(10000));
            invoices[i].setMediumTaxAmount(20 + random.nextInt(10000));
            invoices[i].setLowestTaxAmount(20 + random.nextInt(10000));
            invoices[i].setRate0percent(20 + random.nextInt(10000));
            invoices[i].setDeadline(sndDate);
            invoices[i].setPaid(random.nextBoolean());
            invoices[i] = new PurchaseInvoice(invoices[i]);

            manager.addData(invoices[i]);
        }
    }
}
