package controllers;

public interface TableViewInitializer {
    void initializeArrayData();

    void deleteData();

    void editData();

    boolean loadData(String pathname);

    void initializeColumns();

    void sortData();

    void refreshTable();

    void showDetails();
}
