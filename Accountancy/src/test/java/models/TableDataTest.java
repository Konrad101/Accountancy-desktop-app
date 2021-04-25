package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableDataTest {

    @Test
    void removeEmptyCharsFromEnd() {
        TableData tableData = new Contractor();
        String[] inputData = {"sth ", "", " company name ", "  ", null};
        String[] correctOutput = {"sth", "", " company name", "", null};

        for (int i = 0; i < inputData.length; i++) {
            assertEquals(tableData.removeEmptyCharsFromEnd(inputData[i]), correctOutput[i]);
        }
    }
}