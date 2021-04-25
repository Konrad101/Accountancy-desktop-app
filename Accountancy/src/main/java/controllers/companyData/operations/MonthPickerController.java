package controllers.companyData.operations;

import controllers.companyData.AbstractCellController;
import controllers.companyData.invoiceControllers.InvoiceCellController;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.database.dataManagers.CompanyDataManager;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicLong;

public class MonthPickerController extends OperationController implements Initializable {
    public HBox yearsBox;
    public GridPane monthsPane;
    public Button cancelButton;
    public Button continueButton;
    public ScrollPane yearsScrollPane;

    private List<Button> visibleYears = new ArrayList<>(9);
    private final List<Button> months = new ArrayList<>(12);

    private static final int MIN_YEAR = 1990;
    private static final int MAX_YEAR = Integer.MAX_VALUE;
    private int pickedYear = 0;
    private int pickedMonth = 0;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initializeYears();
        this.initializeMonths();
        this.lockButtons();
        this.setScrollPane();
    }

    private void initializeYears() {
        int currentYear = LocalDateTime.now().getYear();
        int initialYearsAmount = 11;

        int year = currentYear - 5;
        for (int i = 0; i < initialYearsAmount; i++) {
            Button yearButton = new Button(String.valueOf(year));
            setYearButton(yearButton, year);
            visibleYears.add(yearButton);
            year++;
        }

        yearsBox.getChildren().addAll(visibleYears);
    }

    private void initializeMonths() {
        double width = 100;
        double height = 50;
        ArrayList<String> polishMonths = CompanyDataManager.getPolishMonths();
        for (int i = 0; i < 12; i++) {
            Button monthButton = new Button(polishMonths.get(i));
            monthButton.setPrefWidth(width);
            monthButton.setPrefHeight(height);
            changeButtonStyle(monthButton, false);
            final int monthNumber = i + 1;
            monthButton.setOnAction(e -> changeCurrentMonth(monthNumber));
            months.add(monthButton);
        }

        int buttonIndex = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 6; j++) {
                monthsPane.add(months.get(buttonIndex++), j, i);
            }
        }
    }

    private void lockButtons() {
        for (Button monthButton : months) {
            monthButton.setDisable(true);
        }
        continueButton.setDisable(true);
    }

    private void setScrollPane() {
        yearsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        int yearsToShow = 5;
        yearsScrollPane.fitToHeightProperty();
        yearsScrollPane.setHvalue(2.985);
        long initializeTime = System.currentTimeMillis();
        AtomicLong lastYearsChangeTime = new AtomicLong();
        yearsScrollPane.hvalueProperty().addListener((observableValue, number, t1) -> {
            if (System.currentTimeMillis() - initializeTime > 555 && System.currentTimeMillis() - lastYearsChangeTime.get() > 300) {
                if (yearsScrollPane.getHvalue() == 0) {
                    lastYearsChangeTime.set(System.currentTimeMillis());
                    showPreviousYears(yearsToShow);
                } else if (yearsScrollPane.getHvalue() == yearsScrollPane.getHmax()) {
                    lastYearsChangeTime.set(System.currentTimeMillis());
                    showNextYears(yearsToShow);
                }
            }
        });
    }

    private void showPreviousYears(int amount) {
        int currentMinYear = Integer.parseInt(visibleYears.get(0).getText());
        int freePlaces = currentMinYear - MIN_YEAR;
        if (freePlaces < amount) {
            visibleYears = new ArrayList<>(visibleYears.subList(0, (visibleYears.size() / 2) + 1 + (amount - freePlaces)));
        } else {
            visibleYears = new ArrayList<>(visibleYears.subList(0, (visibleYears.size() / 2) + 1));
        }

        if (currentMinYear > MIN_YEAR) {
            int minimalVisibleYear = currentMinYear - 1;
            boolean minYearReached = false;
            for (int i = 0; i < amount && !minYearReached; i++) {
                Button previousYearButton = new Button(String.valueOf(minimalVisibleYear));
                setYearButton(previousYearButton, minimalVisibleYear);
                visibleYears.add(0, previousYearButton);
                if (minimalVisibleYear != MIN_YEAR) {
                    minimalVisibleYear--;
                } else {
                    minYearReached = true;
                }
            }

            yearsBox.getChildren().clear();
            yearsBox.getChildren().addAll(visibleYears);
            yearsScrollPane.setHvalue(0.8);
        }
    }

    private void showNextYears(int amount) {
        int currentMaxYear = Integer.parseInt(visibleYears.get(visibleYears.size() - 1).getText());
        if (currentMaxYear < MAX_YEAR) {
            int freePlaces = MAX_YEAR - currentMaxYear;
            if (freePlaces < amount) {
                visibleYears = new ArrayList<>(visibleYears.subList((visibleYears.size() - amount - 1) - (amount - freePlaces), visibleYears.size()));
            } else {
                visibleYears = new ArrayList<>(visibleYears.subList((visibleYears.size() - amount - 1), visibleYears.size()));
            }

            int maximalVisibleYear = currentMaxYear + 1;
            boolean maxYearReached = false;
            for (int i = 0; i < amount && !maxYearReached; i++) {
                Button nextYearButton = new Button(String.valueOf(maximalVisibleYear));
                setYearButton(nextYearButton, maximalVisibleYear);
                visibleYears.add(nextYearButton);
                if (maximalVisibleYear != MAX_YEAR) {
                    maximalVisibleYear++;
                } else {
                    maxYearReached = true;
                }
            }

            yearsBox.getChildren().clear();
            yearsBox.getChildren().addAll(visibleYears);
            yearsScrollPane.setHvalue(0.2);
        }
    }

    private void setYearButton(Button yearButton, int year) {
        if (yearButton != null) {
            double width = 85;
            double height = 99;
            yearButton.setPrefWidth(width);
            yearButton.setPrefHeight(height);

            boolean isYearPicked = year == pickedYear;
            changeButtonStyle(yearButton, isYearPicked);
            yearButton.setOnAction(e -> changeCurrentYear(year));
        }
    }

    private void changeCurrentMonth(int month) {
        if (month >= 0 && month <= 12) {
            if (this.pickedMonth != 0) {
                changeButtonStyle(months.get(pickedMonth - 1), false);
            } else {
                continueButton.setDisable(false);
            }
            changeButtonStyle(months.get(month - 1), true);
            pickedMonth = month;
        }
    }

    private void changeCurrentYear(int year) {
        Button pickedYearButton = getButtonForYear(year);
        if (pickedYearButton != null) {
            if (pickedYear != 0) {
                changeButtonStyle(getButtonForYear(pickedYear), false);
            } else {
                unlockMonthsButtons();
            }
            changeButtonStyle(pickedYearButton, true);
            pickedYear = year;
        }
    }

    private Button getButtonForYear(int year) {
        Button button = null;
        for (Button yearButton : visibleYears) {
            if (yearButton.getText().equals(String.valueOf(year))) {
                button = yearButton;
                break;
            }
        }

        return button;
    }

    private void changeButtonStyle(Button button, boolean isPicked) {
        if (button != null) {
            String stylePath;
            if (isPicked) {
                stylePath = "css/monthPickerStyles/pressedMonthButtonStyle.css";
            } else {
                stylePath = "css/monthPickerStyles/monthButtonStyle.css";
            }
            button.getStylesheets().clear();
            button.getStylesheets().addAll(stylePath);
        }
    }

    private void unlockMonthsButtons() {
        for (Button monthButton : months) {
            monthButton.setDisable(false);
        }
    }

    public void okButtonAction() {
        this.controller.changeInvoiceData(pickedMonth, pickedYear);
        this.refreshControllerList();
        ((InvoiceCellController) controller).setCurrentMonthState(AbstractCellController.getCurrentType());
        this.cancel();
    }

    public void cancel() {
        turnOffStage((Stage) yearsBox.getScene().getWindow());
    }

}
