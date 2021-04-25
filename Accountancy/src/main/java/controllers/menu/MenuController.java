package controllers.menu;

import controllers.CompanyStarter;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Company;
import models.database.Log;
import models.database.dataManagers.CompanyDataManager;
import models.database.dataManagers.DatabaseManager;
import views.DialogMessage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    public VBox companiesBox;
    public Button addCompanyButton;
    public Button importCompanyButton;
    public Button openCompanyButton;

    private static final String COMPANIES_BUTTON_STYLE_PATH = "css/menuStyles/companyButtonStyle.css";

    private final List<Pair<Button, String>> companiesData = new ArrayList<>();

    private boolean addWindowOpened = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createCompaniesButtons();
        addCompaniesButtons();
    }

    private void createCompaniesButtons() {
        String[][] companies = CompanyDataManager.getSavedCompanies();
        if (companies != null) {
            for (String[] company : companies) {
                if (company.length > 1) {
                    Company companyData = DatabaseManager.getCompanyFromFile(company[1]);
                    String buttonText = "Error";
                    if (companyData != null) {
                        buttonText = companyData.getCompanyName() + "\n";
                        buttonText += "NIP: " + companyData.getNIP();
                    }
                    Button companyButton = new Button(buttonText);
                    setCompanyButtonAction(companyButton);
                    companyButton.getStylesheets().add(COMPANIES_BUTTON_STYLE_PATH);
                    companiesData.add(new Pair<>(companyButton, company[1], company[0]));
                }
            }
        }
    }

    private void setCompanyButtonAction(Button companyButton) {
        if (companyButton != null) {
            companyButton.setOnAction(e -> {
                if (e.getSource() instanceof Button) {
                    Button companyBtn = (Button) e.getSource();
                    String path = null;
                    String companyName = null;
                    for (Pair<Button, String> pair : companiesData) {
                        if (pair.getElement() == companyBtn) {
                            path = pair.getElementValue();
                            companyName = pair.getCompanyName();
                            break;
                        }
                    }
                    if (path != null) {
                        CompanyStarter starter = new CompanyStarter(path, companyName);
                        starter.runCompanyStage();
                        closeStage();
                    } else {
                        DialogMessage message = new DialogMessage(Alert.AlertType.ERROR);
                        message.showDialog("Brak ścieżki do danych dla firmy " + companyName);
                    }
                }
            });
        }
    }

    private void addCompaniesButtons() {
        if (companiesData.size() != 0) {
            for (Pair<Button, String> pair : companiesData) {
                companiesBox.getChildren().addAll(pair.getElement());
            }
        }
    }

    public void refreshCompanies() {
        for (Pair<Button, String> pair : companiesData) {
            companiesBox.getChildren().remove(pair.getElement());
        }
        companiesData.clear();
        this.createCompaniesButtons();
        this.addCompaniesButtons();
    }

    public void setAddWindowOpened(boolean addWindowOpened) {
        this.addWindowOpened = addWindowOpened;
    }

    private void closeStage() {
        ((Stage) companiesBox.getScene().getWindow()).close();
    }

    public void openAddCompanyWindow() {
        if (!this.addWindowOpened) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxmlStages/menu/addCompanyPanel.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                AddCompanyController controller = loader.getController();
                controller.setController(this);
                stage.setResizable(false);
                stage.getIcons().add(new Image("images/icon.png"));
                stage.setTitle("Dodawanie firmy");

                stage.setScene(new Scene(root));
                stage.centerOnScreen();
                stage.show();
                this.addWindowOpened = true;
                stage.setOnCloseRequest(e -> this.addWindowOpened = false);
            } catch (IOException ex) {
                new Log().saveError("Error while loading add company stage.");
            }
        }
    }

    private static class Pair<E, V> {
        private final E element;
        private final V elementValue;
        private final String companyName;

        public Pair(E element, V elementValue, String companyName) {
            this.element = element;
            this.elementValue = elementValue;
            this.companyName = companyName;
        }

        public E getElement() {
            return element;
        }

        public V getElementValue() {
            return elementValue;
        }

        public String getCompanyName() {
            return companyName;
        }
    }
}
