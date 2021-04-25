package controllers.companyData.invoiceControllers;


import controllers.TableViewInitializer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.Contractor;
import models.DateParser;
import models.database.DataReader;
import models.invoices.Invoice;
import models.invoices.SaleInvoice;

import java.net.URL;
import java.util.*;


public class SaleInvoicesController extends InvoiceCellController implements Initializable, TableViewInitializer {
    public TableView<Identifier<Invoice>> table;
    public TableColumn<Identifier<Invoice>, Number> ordinalNumber;
    public TableColumn<Identifier<Invoice>, String> invoiceNumber;
    public TableColumn<Identifier<Invoice>, String> contractor;
    public TableColumn<Identifier<Invoice>, String> wholeGrossPrice;
    public TableColumn<Identifier<Invoice>, String> receiveDate;
    public TableColumn<Identifier<Invoice>, String> netPrice;
    public TableColumn<Identifier<Invoice>, String> vatTaxPrice;

    private final ObservableList<Identifier<Invoice>> data = FXCollections.observableArrayList();


    public SaleInvoicesController() {
        super();
        this.loadData(dataPathname);
        this.sortData();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        this.initializeArrayData();
        this.initializeColumns();
        this.setEmptyTableText(table, "Brak faktur sprzedażowych");
    }

    @Override
    public void initializeColumns() {
        String[][] namesWithIDs = Contractor.getContractorsNamesWithID();
        List<Identifier<String>> contractorsNames = new ArrayList<>();

        if (namesWithIDs != null) {
            for (String[] contractors : namesWithIDs) {
                contractorsNames.add(new Identifier<>(Integer.parseInt(contractors[0]), contractors[1]));
            }
        }
        Collections.sort(contractorsNames);

        ordinalNumber.setCellValueFactory(column -> new SimpleIntegerProperty(data.indexOf(column.getValue()) + 1));
        columnTextToCenter(ordinalNumber);

        receiveDate.setCellValueFactory(cellData -> cellData.getValue().getData().getCellReceiveDate());
        columnTextToRight(receiveDate);
        invoiceNumber.setCellValueFactory(cellData -> cellData.getValue().getData().getCellNumber());
        columnTextToCenter(invoiceNumber);

        contractor.setCellValueFactory(cellData -> {
            String contractorName = "";
            for (Identifier<String> contractor : contractorsNames) {
                if (contractor.getId() == cellData.getValue().getData().getContractorNumber()) {
                    contractorName = contractor.getData();
                    break;
                }
            }
            return new SimpleStringProperty(contractorName);
        });

        netPrice.setCellValueFactory(cellData -> cellData.getValue().getData().getNetPrice());
        columnTextToRight(netPrice);
        vatTaxPrice.setCellValueFactory(cellData -> cellData.getValue().getData().getVATPrice());
        columnTextToRight(vatTaxPrice);
        wholeGrossPrice.setCellValueFactory(cellData -> cellData.getValue().getData().getGrossPrice());
        columnTextToRight(wholeGrossPrice);

        for (TableColumn column : table.getColumns()) {
            column.setSortable(false);
        }
    }

    @Override
    public void sortData() {
        FXCollections.sort(data);
    }

    @Override
    public boolean loadData(String pathname) {
        DataReader reader = new DataReader(pathname);
        String[][] fileData = reader.getFileData();
        if (fileData == null) {
            return false;
        }

        if (fileData.length > 0 && fileData[0].length > 0) {
            for (String[] fileDatum : fileData) {
                ArrayList<String> invoiceData = new ArrayList<>(Arrays.asList(fileDatum).subList(0, fileData[0].length));
                Invoice invoice = new SaleInvoice();
                invoice.setRegisterType(invoiceData.get(1));
                invoice.setNumber(invoiceData.get(2));
                invoice.setContractorNumber(Integer.parseInt(invoiceData.get(3)));
                invoice.setPostingDate(DateParser.parseStringToDate(invoiceData.get(4)));
                invoice.setReceiveDate(DateParser.parseStringToDate(invoiceData.get(5)));
                invoice.setIssueDate(DateParser.parseStringToDate(invoiceData.get(6)));
                invoice.setHighestTaxAmount(Double.parseDouble(invoiceData.get(7)));
                invoice.setMediumTaxAmount(Double.parseDouble(invoiceData.get(8)));
                invoice.setLowestTaxAmount(Double.parseDouble(invoiceData.get(9)));
                invoice.setRate0percent(Double.parseDouble(invoiceData.get(10)));
                this.data.add(new Identifier<>(Integer.parseInt(invoiceData.get(0)), invoice));
            }
            return true;
        }

        return false;
    }

    @Override
    public void initializeArrayData() {
        if (!data.isEmpty() && table != null) {
            table.setItems(this.data);
        }
    }

    @Override
    public void refreshTable() {
        this.data.clear();
        this.loadData(dataPathname);
        this.sortData();
        this.initializeArrayData();
    }

    @Override
    public void showDetails() {
        Identifier<Invoice> selectedItem = table.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            this.showDetailsWindow(selectedItem.getData());
        }
    }

    @Override
    public void deleteData() {
        Identifier<Invoice> selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem != null && showDeleteConfirmation()) {
            table.getItems().remove(selectedItem);
            this.deleteCell(selectedItem.getId());
        }
    }

    @Override
    public void editData() {
        Identifier<Invoice> selectedItem = table.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            this.showEditWindow(selectedItem.getId(), selectedItem.getData());
        }
    }

}
