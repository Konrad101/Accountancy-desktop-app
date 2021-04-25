package controllers.menu;

import controllers.companyData.operations.OperationController;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Company;
import models.database.Configurator;
import models.database.dataManagers.DatabaseManager;
import views.DialogMessage;

public class AddCompanyController extends OperationController {
    public TextField companyNameLabel;
    public TextField companyNIPLabel;

    private MenuController menuController;

    public void setController(MenuController menuController) {
        this.menuController = menuController;
    }

    public void addCompany() {
        Company company = new Company();
        setCompanyData(company);
        if (checkConditions(company)) {
            DatabaseManager manager = new DatabaseManager();
            manager.addCompany(company, createPathForCompany(company));
            menuController.setAddWindowOpened(false);
            menuController.refreshCompanies();
            new Configurator().setCompanyConfigData(company, "programData/" + company.getCompanyName());
            this.cancel();
        } else {
            DialogMessage message = new DialogMessage(Alert.AlertType.ERROR);
            message.showDialog("Dane firmy są nieprawidłowe.");
        }
    }

    private void setCompanyData(Company company) {
        if (company != null) {
            company.setCompanyName(company.removeEmptyCharsFromEnd(this.companyNameLabel.getText()));
            company.setNIP(this.companyNIPLabel.getText());
        }
    }

    private String createPathForCompany(Company company) {
        if (company != null) {
            return "programData/" + company.getNIP();
        }

        return null;
    }

    private boolean checkConditions(Company company) {
        if (company == null) {
            return false;
        }

        boolean dataIsCorrect = true;
        if (company.dataIsIncorrect(company.getCompanyName())) {
            setDarkWrongDataFieldEffect(companyNameLabel);
            dataIsCorrect = false;
        }
        if (company.nipIsIncorrect()) {
            setDarkWrongDataFieldEffect(companyNIPLabel);
            dataIsCorrect = false;
        }

        return dataIsCorrect;
    }

    public void cancel() {
        if (this.menuController != null) {
            menuController.setAddWindowOpened(false);
        }

        Stage currentStage = (Stage) this.companyNameLabel.getScene().getWindow();
        currentStage.close();
    }
}
