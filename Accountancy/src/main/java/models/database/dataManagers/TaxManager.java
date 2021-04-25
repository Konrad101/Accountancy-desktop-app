package models.database.dataManagers;

import models.DateParser;
import models.database.DataReader;
import models.database.DataWriter;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TaxManager {
    private final static double DEFAULT_HIGHEST_TAX = 23.;
    private final static double DEFAULT_MEDIUM_TAX = 8.;
    private final static double DEFAULT_LOWEST_TAX = 5.;

    private final static String TAXES_FILEPATH = "programData/defaultTax.csv";

    private double highestTax = -1;
    private double mediumTax = -1;
    private double lowestTax = -1;

    public TaxManager(int month, int year) {
        initializeFile();
        loadTaxes(month, year);
    }

    private void initializeFile() {
        DataReader reader = new DataReader(TAXES_FILEPATH);
        if (reader.getFileData() == null) {
            String[][] taxesData = new String[2][];
            taxesData[0] = new String[4];
            taxesData[0][0] = "DATE";
            taxesData[0][1] = "HIGHEST_TAX";
            taxesData[0][2] = "MEDIUM_TAX";
            taxesData[0][3] = "SMALLEST_TAX";
            taxesData[1] = new String[4];
            taxesData[1][0] = DateParser.parseLocalDate(LocalDate.of(1990, 1, 1));
            taxesData[1][1] = String.valueOf(DEFAULT_HIGHEST_TAX);
            taxesData[1][2] = String.valueOf(DEFAULT_MEDIUM_TAX);
            taxesData[1][3] = String.valueOf(DEFAULT_LOWEST_TAX);
            DataWriter writer = new DataWriter(TAXES_FILEPATH);
            writer.saveInfo(taxesData, false);
        }
    }

    private void loadTaxes(int month, int year) {
        DataReader reader = new DataReader(TAXES_FILEPATH);
        String[][] taxesData = reader.getFileData();
        if (taxesData != null) {
            Calendar invoiceDate = new GregorianCalendar(year, month, 1);
            for (int i = taxesData.length - 1; i > 0; i--) {
                Calendar taxDate = DateParser.parseStringToDate(taxesData[i][0].replace(".", "-"));
                if (taxDate != null && taxesData[i].length > 3) {
                    if (invoiceDate.compareTo(taxDate) > 0) {
                        highestTax = Double.parseDouble(taxesData[i][1].replace(",", "."));
                        mediumTax = Double.parseDouble(taxesData[i][2].replace(",", "."));
                        lowestTax = Double.parseDouble(taxesData[i][3].replace(",", "."));
                        break;
                    }
                }
            }
        }
    }

    public double getHighestTax() {
        if (this.highestTax != -1) {
            return highestTax;
        }

        return DEFAULT_HIGHEST_TAX;
    }

    public double getMediumTax() {
        if (this.mediumTax != -1) {
            return mediumTax;
        }

        return DEFAULT_MEDIUM_TAX;
    }

    public double getLowestTax() {
        if (this.lowestTax != -1) {
            return lowestTax;
        }

        return DEFAULT_LOWEST_TAX;
    }

}
