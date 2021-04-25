package controllers.companyData;

import controllers.TableViewInitializer;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import models.Contractor;
import models.DataTypeEnum;
import models.database.dataManagers.ContractorInvoicesManager;
import models.database.DataReader;
import models.database.dataManagers.CompanyDataManager;
import views.DialogMessage;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ContractorsController extends AbstractCellController implements Initializable, TableViewInitializer {
    public TableView<Identifier<Contractor>> table;
    public TableColumn<Identifier<Contractor>, Number> ordinalNumber;
    public TableColumn<Identifier<Contractor>, String> companyName;
    public TableColumn<Identifier<Contractor>, String> companyNIP;
    public TableColumn<Identifier<Contractor>, String> address;
    public TableColumn<Identifier<Contractor>, String> companyPlace;
    public TableColumn<Identifier<Contractor>, String> phoneNumber;

    private ObservableList<Identifier<Contractor>> visibleData = FXCollections.observableArrayList();
    private ArrayList<Identifier<Contractor>> allData;

    private static final int MAX_VISIBLE_CONTRACTORS_AMOUNT = 1000;

    public ContractorsController() {
        super();
        int initialCapacity = new CompanyDataManager(DataTypeEnum.CONTRACTORS).getHighestID();
        if (initialCapacity <= 0) {
            initialCapacity = 10;
        }
        allData = new ArrayList<>(initialCapacity);

        this.loadData(dataPathname);
        this.sortData();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        this.initializeVisibleData();
        this.initializeArrayData();
        this.initializeColumns();
        this.initializeTableScrollBar();
        this.setEmptyTableText(table, "Brak kontrahentów");
    }

    private void initializeVisibleData() {
        for (int i = 0; i < allData.size() && i < MAX_VISIBLE_CONTRACTORS_AMOUNT; i++) {
            visibleData.add(allData.get(i));
        }
    }

    private void initializeTableScrollBar() {
        int addedContractorsAmount = 20;
        if (allData.size() > 1000 && allData.size() <= 2000) {
            addedContractorsAmount = 100;
        } else if (allData.size() > 2000 && allData.size() <= 5000) {
            addedContractorsAmount = 200;
        } else if (allData.size() > 5000 && allData.size() <= 10000) {
            addedContractorsAmount = 300;
        } else if (allData.size() > 10000 && allData.size() <= 15000) {
            addedContractorsAmount = 500;
        } else if (allData.size() > 15000) {
            addedContractorsAmount = 1000;
        }

        AtomicLong lastBorderScrollTime = new AtomicLong(System.currentTimeMillis());
        int ultimateAddedContractorsAmount = addedContractorsAmount;
        Platform.runLater(() -> {
            ScrollBar verticalBar = (ScrollBar) table.lookup(".scroll-bar:vertical");
            table.addEventFilter(ScrollEvent.SCROLL, event -> this.checkScrollAction(verticalBar, lastBorderScrollTime, ultimateAddedContractorsAmount));
            table.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> this.checkScrollAction(verticalBar, lastBorderScrollTime, ultimateAddedContractorsAmount));
        });
    }

    private void checkScrollAction(ScrollBar verticalBar, AtomicLong lastBorderScrollTime, int addedContractorsAmount) {
        if (verticalBar != null) {
            long scrollBreakTime = 373;

            if (visibleData.size() == MAX_VISIBLE_CONTRACTORS_AMOUNT) {
                if (System.currentTimeMillis() - lastBorderScrollTime.get() > scrollBreakTime) {
                    if (verticalBar.getValue() == verticalBar.getMax()) {
                        lastBorderScrollTime.set(System.currentTimeMillis());
                        showNextContractors(addedContractorsAmount);
                    } else if (verticalBar.getValue() == verticalBar.getMin()) {
                        lastBorderScrollTime.set(System.currentTimeMillis());
                        showPreviousContractors(addedContractorsAmount);
                    }
                }
            }
        }
    }

    private void showNextContractors(int amount) {
        if (amount <= MAX_VISIBLE_CONTRACTORS_AMOUNT) {
            if (visibleData.get(visibleData.size() - 1) != allData.get(allData.size() - 1)) {
                int freePlaces = allData.size() - (allData.indexOf(visibleData.get(visibleData.size() - 1)) + 1);
                List<Identifier<Contractor>> visibleContractorsList;
                if (freePlaces < amount) {
                    visibleContractorsList = visibleData.subList(freePlaces, visibleData.size());
                } else {
                    visibleContractorsList = visibleData.subList(amount, visibleData.size());
                }

                int lastVisibleID = visibleContractorsList.get(visibleContractorsList.size() - 1).getId();
                int addedContractors = 0;
                boolean addNewContractors = false;
                for (Identifier<Contractor> contractor : allData) {
                    if (addedContractors == amount) {
                        break;
                    }
                    if (addNewContractors) {
                        visibleContractorsList.add(contractor);
                        addedContractors++;
                    } else if (contractor.getId() == lastVisibleID) {
                        addNewContractors = true;
                    }
                }

                visibleData = FXCollections.observableArrayList(visibleContractorsList);
                table.setItems(visibleData);
            }
        }
    }

    private void showPreviousContractors(int amount) {
        if (amount <= MAX_VISIBLE_CONTRACTORS_AMOUNT) {
            if (visibleData.get(0) != allData.get(0)) {
                int freePlaces = allData.indexOf(visibleData.get(0));
                List<Identifier<Contractor>> visibleContractorsList;
                if (freePlaces < amount) {
                    visibleContractorsList = visibleData.subList(0, visibleData.size() - freePlaces);
                } else {
                    visibleContractorsList = visibleData.subList(0, visibleData.size() - amount);
                }

                int firstVisibleID = visibleContractorsList.get(0).getId();
                int addedContractors = 0;
                boolean addNewContractors = false;
                for (int i = allData.size() - 1; i >= 0 && addedContractors != amount; i--) {
                    if (addNewContractors) {
                        visibleContractorsList.add(0, allData.get(i));
                        addedContractors++;
                    } else if (allData.get(i).getId() == firstVisibleID) {
                        addNewContractors = true;
                    }
                }

                visibleData = FXCollections.observableArrayList(visibleContractorsList);
                table.setItems(visibleData);
            }
        }
    }

    @Override
    public void initializeArrayData() {
        if (!visibleData.isEmpty() && table != null) {
            table.setItems(this.visibleData);
        }
    }

    @Override
    public void initializeColumns() {
        ordinalNumber.setCellValueFactory(cellData -> {
            int index = 0;
            for (Identifier<Contractor> contractor : allData) {
                if (cellData.getValue().getId() == contractor.getId()) {
                    break;
                }
                index++;
            }
            return new SimpleIntegerProperty(index + 1);
        });
        columnTextToCenter(ordinalNumber);

        companyName.setCellValueFactory(cellData -> cellData.getValue().getData().getCellCompanyName());
        companyNIP.setCellValueFactory(cellData -> cellData.getValue().getData().getCellNIP());
        columnTextToCenter(companyNIP);
        address.setCellValueFactory(cellData -> cellData.getValue().getData().getCellCompanyAddress());
        companyPlace.setCellValueFactory(cellData -> cellData.getValue().getData().getCellCompanyCity());
        phoneNumber.setCellValueFactory(cellData -> cellData.getValue().getData().getCellPhoneNumber());
        columnTextToCenter(phoneNumber);

        for (TableColumn column : table.getColumns()) {
            column.setSortable(false);
        }
    }

    @Override
    public void deleteData() {
        Identifier<Contractor> selectedItem = table.getSelectionModel().getSelectedItem();

        if (selectedItem != null && !additionalWindowOpened) {
            DialogMessage message = new DialogMessage();
            String messageText = "";
            ContractorInvoicesManager contractorManager = new ContractorInvoicesManager();
            int invoicesAmount = contractorManager.getInvoicesAmount(selectedItem.getId());
            if (invoicesAmount > 0) {
                messageText += "Ostrzeżenie! " + invoicesAmount;
                int lastNumber = invoicesAmount % 10;
                if (invoicesAmount == 1) {
                    messageText += " faktura ma";
                } else if (lastNumber >= 2 && lastNumber <= 4) {
                    messageText += " faktury mają";
                } else {
                    messageText += " faktur ma";
                }
                messageText += " wpisanego wybranego kontrahenta.\n";
            }
            messageText += "Czy na pewno usunąć kontrahenta?";
            boolean confirmation = message.showConfirmationBeforeDelete(messageText);
            if (confirmation) {
                table.getItems().remove(selectedItem);
                this.allData.remove(selectedItem);
                this.deleteCell(selectedItem.getId());
                this.refreshTable();
            }
        }
    }

    @Override
    public void editData() {
        Identifier<Contractor> selectedItem = table.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            this.showEditWindow(selectedItem.getId(), selectedItem.getData());
        }
    }

    @Override
    public boolean loadData(String pathname) {
        DataReader reader = new DataReader(pathname);

        String[][] fileData = reader.getFileData();
        if (fileData == null) {
            return false;
        }

        if (fileData.length > 0 && fileData[0].length > 0) {
            int index = 0;
            for (String[] fileDatum : fileData) {
                ArrayList<String> contractorData = new ArrayList<>(Arrays.asList(fileDatum).subList(0, fileData[index].length));
                Contractor contractor = new Contractor();
                contractor.setNIP(contractorData.get(1));
                contractor.setCompanyName(contractorData.get(2));
                contractor.setCompanyAddress(contractorData.get(3));
                contractor.setCompanyZipCode(contractorData.get(4));
                contractor.setCompanyCity(contractorData.get(5));
                if (contractorData.size() > 6) {
                    contractor.setPersonData(contractorData.get(6));
                    if (contractorData.size() > 7) {
                        contractor.setPhoneNumber(contractorData.get(7));
                        if (contractorData.size() > 8) {
                            contractor.setEmail(contractorData.get(8));
                        }
                    }
                }

                Identifier<Contractor> contractorIdentifier = new Identifier<>(Integer.parseInt(contractorData.get(0)), contractor);
                this.allData.add(contractorIdentifier);
                index++;
            }
            return true;
        }

        return false;
    }

    @Override
    public void sortData() {
        Collections.sort(allData);
    }

    @Override
    public void refreshTable() {
        this.allData.clear();
        this.loadData(dataPathname);
        this.sortData();
        this.refreshVisibleData();
        this.initializeArrayData();
        this.table.refresh();
    }

    @Override
    public void showDetails() {
        Identifier<Contractor> selectedItem = table.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            this.showDetailsWindow(selectedItem.getData());
        }
    }

    private void refreshVisibleData() {
        if (allData.size() <= MAX_VISIBLE_CONTRACTORS_AMOUNT) {
            visibleData.clear();
            initializeVisibleData();
        } else if (visibleData.size() > MAX_VISIBLE_CONTRACTORS_AMOUNT) {
            // after adding
            int firstVisibleIndex = getIndexInAllData(visibleData.get(0).getId());
            visibleData.clear();
            fillVisibleContractors(firstVisibleIndex);
        } else {
            // after deleting
            if (visibleData.size() > 0) {
                int firstVisibleIndex = getIndexInAllData(visibleData.get(0).getId());
                int freePlaces = (allData.size() - 1) - getIndexInAllData(visibleData.get(visibleData.size() - 1).getId());
                if (firstVisibleIndex == -1) {
                    if (visibleData.size() > 1) {
                        firstVisibleIndex = getIndexInAllData(visibleData.get(1).getId());
                    }
                }
                int startIndex;
                if (freePlaces > 0) {
                    startIndex = firstVisibleIndex;
                } else {
                    startIndex = firstVisibleIndex - 1;
                }
                this.visibleData.clear();
                fillVisibleContractors(startIndex);
            }
        }
    }

    private int getIndexInAllData(int id) {
        int index = 0;
        boolean found = false;
        for (Identifier<Contractor> contractorIdentifier : allData) {
            if (contractorIdentifier.getId() == id) {
                found = true;
                break;
            }
            index++;
        }
        if (!found) {
            index = -1;
        }

        return index;
    }

    private void fillVisibleContractors(int startIndex) {
        for (int i = 0; i < MAX_VISIBLE_CONTRACTORS_AMOUNT && startIndex < allData.size(); i++, startIndex++) {
            visibleData.add(allData.get(startIndex));
        }
    }

    protected void cleanMemory() {
        allData = null;
        table.setItems(null);
        visibleData = null;
    }
}
