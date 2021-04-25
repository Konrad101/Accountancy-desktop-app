package models;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContractorTest {

    @Test
    void zipCodeIsIncorrect() {
        Contractor contractor = new Contractor();
        String[] postalCodes = {"53-201", "", "53201", "53 201", "53_201", "552301"};
        for (int i = 0; i < postalCodes.length; i++) {
            contractor.setCompanyZipCode(postalCodes[i]);
            if (i == 0) {
                assertFalse(contractor.zipCodeIsIncorrect());
            } else {
                assertTrue(contractor.zipCodeIsIncorrect());
            }
        }
    }
}