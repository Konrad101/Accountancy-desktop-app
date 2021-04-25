package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import models.database.DataReader;
import models.database.dataManagers.CompanyDataManager;

public class Contractor extends Company implements Comparable<Contractor>, TableDataPattern {
    private int id;
    private static int highestID;

    /// COMPANY DATA
    private String companyAddress;
    private String companyZipCode;
    private String companyCity;

    // CONTACT DATA
    private String personData = "";
    private String phoneNumber = "";
    private String email = "";

    public Contractor() {
        this.id = 0;
    }

    public Contractor(Contractor contractor) {
        if (contractor != null) {
            this.NIP = contractor.NIP;
            this.companyName = contractor.companyName;
            this.companyAddress = contractor.companyAddress;
            this.companyZipCode = contractor.companyZipCode;
            this.companyCity = contractor.companyCity;
            this.personData = contractor.personData;
            this.phoneNumber = contractor.phoneNumber;
            this.email = contractor.email;

            this.id = ++highestID;
        }
    }

    //// SETTERS

    public static void setHighestID(int highestID) {
        Contractor.highestID = highestID;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    public void setCompanyZipCode(String companyZipCode) {
        this.companyZipCode = companyZipCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPersonData(String personData) {
        this.personData = personData;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public String getCompanyZipCode() {
        return companyZipCode;
    }

    public String getEmail() {
        return email;
    }

    public String getPersonData() {
        return personData;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public StringProperty getCellCompanyName() {
        return new SimpleStringProperty(this.companyName);
    }

    public StringProperty getCellNIP() {
        return new SimpleStringProperty(this.getPrintFormatNIP());
    }

    public StringProperty getCellCompanyAddress() {
        return new SimpleStringProperty(this.companyAddress);
    }

    public StringProperty getCellCompanyCity() {
        return new SimpleStringProperty(this.companyCity);
    }

    public StringProperty getCellPhoneNumber() {
        return new SimpleStringProperty(this.getPrintFormatPhoneNumber());
    }

    public String getPrintFormatNIP() {
        if (this.NIP != null && this.NIP.length() == 10) {
            return this.NIP.substring(0, 3) + "-" +
                    this.NIP.substring(3, 6) + "-" +
                    this.NIP.substring(6, 8) + "-" +
                    this.NIP.substring(8, 10);
        }

        return this.NIP;
    }

    public String getPrintFormatPhoneNumber() {
        if (this.phoneNumber != null && this.phoneNumber.length() == 9) {
            return this.phoneNumber.substring(0, 3) + " " +
                    this.phoneNumber.substring(3, 6) + " " +
                    this.phoneNumber.substring(6, 9);
        }

        return this.phoneNumber;
    }

    ////

    public static Contractor getContractorByID(int id) {
        DataReader reader = new DataReader(new CompanyDataManager(DataTypeEnum.CONTRACTORS).createFilePathFromGivenData());
        String[][] contractorsData = reader.getFileData();
        Contractor searchedContractor = null;

        if (contractorsData != null) {
            for (String[] data : contractorsData) {
                if (data.length > 0) {
                    if (data[0].equals(String.valueOf(id))) {
                        if (data.length > 5) {
                            searchedContractor = new Contractor();
                            searchedContractor.id = id;
                            searchedContractor.setNIP(data[1]);
                            searchedContractor.setCompanyName(data[2]);
                            searchedContractor.setCompanyAddress(data[3]);
                            searchedContractor.setCompanyZipCode(data[4]);
                            searchedContractor.setCompanyCity(data[5]);
                            if (data.length > 6) {
                                searchedContractor.setPersonData(data[6]);
                            }
                            if (data.length > 7) {
                                searchedContractor.setPhoneNumber(data[7]);
                                if (data.length > 8) {
                                    searchedContractor.setEmail(data[8]);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }

        return searchedContractor;
    }

    public static String[][] getContractorsNamesWithID() {
        DataReader reader = new DataReader(new CompanyDataManager(DataTypeEnum.CONTRACTORS).createFilePathFromGivenData());
        String[][] contractorsData = reader.getFileData();
        if (contractorsData == null) {
            return null;
        }

        String[][] namesWithIDs = new String[contractorsData.length][];
        int index = 0;
        for (String[] data : contractorsData) {
            if (data != null && data.length >= 3) {
                namesWithIDs[index] = new String[2];
                namesWithIDs[index][0] = data[0];
                namesWithIDs[index][1] = data[2];
            }
            index++;
        }
        return namesWithIDs;
    }

    ////

    @Override
    public String[] getData() {
        String[] data = new String[9];
        data[0] = String.valueOf(this.id);
        data[1] = this.NIP;
        data[2] = this.companyName;
        data[3] = this.companyAddress;
        data[4] = this.companyZipCode;
        data[5] = this.companyCity;

        data[6] = getOptionalString(this.personData);
        data[7] = getOptionalString(this.phoneNumber);
        data[8] = getOptionalString(this.email);

        return data;
    }

    private String getOptionalString(String text) {
        if (stringIsEmpty(text)) {
            text = "";
        }
        return text;
    }

    public boolean zipCodeIsIncorrect() {
        boolean codeIsIncorrect;
        codeIsIncorrect = !fieldContainsOnlyNumbers(companyZipCode, 5);
        if (!codeIsIncorrect) {
            if (companyZipCode.length() == 6) {
                if (companyZipCode.charAt(2) != '-') {
                    codeIsIncorrect = true;
                }
            } else {
                codeIsIncorrect = true;
            }
        }
        return codeIsIncorrect;
    }

    @Override
    public int compareTo(Contractor contractor) {
        return this.companyName.toLowerCase().compareTo(contractor.companyName.toLowerCase());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Contractor) {
            Contractor contractor = (Contractor) obj;
            boolean isEqual = true;
            if (!this.NIP.equals(contractor.NIP) || !this.companyName.equals(contractor.companyName)) {
                isEqual = false;
            } else if (!this.companyAddress.equals(contractor.companyAddress) || !this.companyZipCode.equals(contractor.companyZipCode) || !this.companyCity.equals(contractor.companyCity)) {
                isEqual = false;
            } else if (stringsAreDifferent(this.personData, contractor.personData) || stringsAreDifferent(this.phoneNumber, contractor.phoneNumber) || stringsAreDifferent(this.email, contractor.email)) {
                isEqual = false;
            }

            return isEqual;
        }
        return super.equals(obj);
    }

    private boolean stringsAreDifferent(String s1, String s2) {
        if (s1 == null) {
            return s2 != null;
        } else if (s2 == null) {
            return true;
        }

        return !s1.equals(s2);
    }
}
