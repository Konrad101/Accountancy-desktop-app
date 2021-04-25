package models;

public abstract class TableData {
    protected int id;

    protected boolean stringIsEmpty(String data) {
        return data == null || data.isEmpty() || data.equalsIgnoreCase("NULL");
    }

    protected boolean fieldContainsOnlyNumbers(String fieldText, int digitsAmount) {
        if (fieldText == null) {
            return false;
        }

        boolean condition = true;
        String text = fieldText.replace("-", "");
        text = text.replace(" ", "");
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) < '0' || text.charAt(i) > '9') {
                condition = false;
                break;
            }
        }
        if (text.length() != digitsAmount) {
            condition = false;
        }
        return condition;
    }

    public String removeEmptyCharsFromEnd(String data) {
        if (data == null) {
            return null;
        }

        if (data.length() > 1) {
            while (data.length() != 0 && data.charAt(data.length() - 1) == ' ') {
                data = data.substring(0, data.length() - 1);
            }
        }
        return data;
    }
}
