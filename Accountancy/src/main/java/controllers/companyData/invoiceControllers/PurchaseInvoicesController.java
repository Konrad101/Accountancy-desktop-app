package controllers.companyData.invoiceControllers;

import controllers.TableViewInitializer;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import models.Contractor;
import models.DateParser;
import models.database.DataReader;
import models.invoices.PurchaseInvoice;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static java.time.temporal.ChronoUnit.DAYS;

public class PurchaseInvoicesController extends InvoiceCellController implements Initializable, TableViewInitializer {
    public TableView<Identifier<PurchaseInvoice>> table;
    public TableColumn<Identifier<PurchaseInvoice>, Number> ordinalNumber;
    public TableColumn<Identifier<PurchaseInvoice>, String> invoiceNumber;
    public TableColumn<Identifier<PurchaseInvoice>, String> contractor;
    public TableColumn<Identifier<PurchaseInvoice>, String> wholeGrossPrice;
    public TableColumn<Identifier<PurchaseInvoice>, String> receiveDate;
    public TableColumn<Identifier<PurchaseInvoice>, String> netPrice;
    public TableColumn<Identifier<PurchaseInvoice>, String> vatTaxPrice;
    public TableColumn<Identifier<PurchaseInvoice>, String> deadlineDate;
    public TableColumn<Identifier<PurchaseInvoice>, Boolean> isPaid;

    private final ObservableList<Identifier<PurchaseInvoice>> data = FXCollections.observableArrayList();

    private static final String PAY_IMMEDIATELY_COLOR = "#eb3d3d";
    private static final String PAY_REMINDER_COLOR = "#d48383";


    public PurchaseInvoicesController() {
        super();
        this.loadData(dataPathname);
        this.sortData();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        this.initializeArrayData();
        this.initializeScrollAction();
        this.initializeColumns();
        this.initializeTableRows();
        this.setEmptyTableText(table, "Brak faktur zakupowych");
    }

    private void initializeScrollAction() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Platform.runLater(() -> {
            AtomicLong lastRefreshTime = new AtomicLong(0);
            table.addEventFilter(ScrollEvent.SCROLL, e -> service.execute(() -> scrollRefreshAction(lastRefreshTime)));
            table.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> service.execute(() -> scrollRefreshAction(lastRefreshTime)));
        });
    }

    private void scrollRefreshAction(AtomicLong lastRefreshTime) {
        final long REFRESH_PERIOD = 333;
        if (lastRefreshTime != null) {
            if (System.currentTimeMillis() - lastRefreshTime.get() > REFRESH_PERIOD) {
                try {
                    this.table.refresh();
                    lastRefreshTime.set(System.currentTimeMillis());
                    Thread.sleep(REFRESH_PERIOD - 5);
                } catch (InterruptedException ignored) {
                }
            }
        }
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
        deadlineDate.setCellValueFactory(cellData -> cellData.getValue().getData().getCellDeadline());
        columnTextToRight(deadlineDate);
        isPaid.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getData().isPaid()));
        isPaid.setCellFactory(cellData -> new CheckBoxTableCell<>());

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
                PurchaseInvoice invoice = new PurchaseInvoice();
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
                invoice.setPaid(Boolean.parseBoolean(invoiceData.get(11)));
                invoice.setDeadline(DateParser.parseStringToDate(invoiceData.get(12)));
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

    private void initializeTableRows() {
        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Identifier<PurchaseInvoice> item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && item.getData().getDeadline() != null && !item.getData().isPaid()) {
                    LocalDate currentDate = LocalDate.now();
                    LocalDate deadlineDate = DateParser.parseCalendarToLocalDate(item.getData().getDeadline());
                    long daysBetween = DAYS.between(currentDate, deadlineDate);
                    if (daysBetween > -60 && daysBetween <= 7) {
                        setStyle("-fx-background-color: " + PAY_REMINDER_COLOR + ";");
                    } else if (daysBetween <= -60) {
                        setStyle("-fx-background-color: " + PAY_IMMEDIATELY_COLOR + ";");
                    }
                }

            }
        });
    }

    @Override
    public void refreshTable() {
        this.data.clear();
        this.loadData(dataPathname);
        this.sortData();
        this.initializeArrayData();
        this.table.refresh();
    }

    @Override
    public void showDetails() {
        Identifier<PurchaseInvoice> selectedItem = table.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            this.showDetailsWindow(selectedItem.getData());
        }
    }

    @Override
    public void deleteData() {
        Identifier<PurchaseInvoice> selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem != null && showDeleteConfirmation()) {
            table.getItems().remove(selectedItem);
            this.deleteCell(selectedItem.getId());
        }
    }

    @Override
    public void editData() {
        Identifier<PurchaseInvoice> selectedItem = table.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            this.showEditWindow(selectedItem.getId(), selectedItem.getData());
        }
    }
}
