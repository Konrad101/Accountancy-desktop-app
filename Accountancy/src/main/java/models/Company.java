package models;

public class Company extends TableData implements TableDataPattern {
    protected String companyName;
    protected String NIP;


    public void setNIP(String NIP) {
        this.NIP = NIP;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getNIP() {
        return NIP;
    }

    public boolean nipIsIncorrect() {
        return !fieldContainsOnlyNumbers(NIP, 10);
    }

    public boolean dataIsIncorrect(String data) {
        return stringIsEmpty(data);
    }

    @Override
    public String[] getData() {
        String[] data = new String[2];
        data[0] = companyName;
        data[1] = NIP;

        return data;
    }
}
