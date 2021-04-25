package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriceFormatterTest {

    @Test
    void changePriceToPrintFormat() {
        assertEquals("4,23", PriceFormatter.changePriceToPrintFormat("4.225675"));
        assertEquals("0,00", PriceFormatter.changePriceToPrintFormat(String.valueOf(0.)));
        assertEquals("19,19", PriceFormatter.changePriceToPrintFormat("19.194675"));
        assertEquals("5,00", PriceFormatter.changePriceToPrintFormat("5"));
        assertEquals("6,01", PriceFormatter.changePriceToPrintFormat("6,01"));
        assertEquals("11,50", PriceFormatter.changePriceToPrintFormat("11.5"));
        assertEquals("3,14", PriceFormatter.changePriceToPrintFormat(String.valueOf(Math.PI)));
        assertEquals("30,00", PriceFormatter.changePriceToPrintFormat("29,99563213123532"));
        assertEquals("1238,00", PriceFormatter.changePriceToPrintFormat("1237.995"));
    }
}