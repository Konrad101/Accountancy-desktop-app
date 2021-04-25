package models;

public class PriceFormatter {
    public static String changePriceToPrintFormat(String price) {
        if (price != null) {
            price = price.replace(",", ".");
            double number;
            try {
                number = Double.parseDouble(price);
            } catch (NumberFormatException ex) {
                return null;
            }

            number = number * 100;
            double decimalPart = number - (int) number;
            decimalPart = decimalPart * 1000;
            decimalPart = (int) Math.round(decimalPart);
            decimalPart /= 1000;
            if (decimalPart >= 0.5) {
                number += 1;
            }
            number = (int) number;
            number /= 100;
            price = String.valueOf(number).replace(".", ",");

            int placesAfterSemicolon = 0;
            boolean semicolonWasRead = false;
            for (int i = 0; i < price.length(); i++) {
                if (semicolonWasRead) {
                    placesAfterSemicolon++;
                } else if (price.charAt(i) == ',') {
                    semicolonWasRead = true;
                }
            }

            if (placesAfterSemicolon < 2) {
                if (placesAfterSemicolon == 0) {
                    price += ",00";
                } else {
                    price += "0";
                }
            }
            return price;
        }

        return null;
    }
}
