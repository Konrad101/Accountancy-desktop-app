package controllers.companyData.operations.add;

import controllers.companyData.operations.AbstractContractorOperationController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import models.Contractor;
import models.DataTypeEnum;
import models.database.dataManagers.CompanyDataManager;
import views.DialogMessage;

public class AddContractorController extends AbstractContractorOperationController {

    @FXML
    public void addContractor() {
        Contractor contractor = new Contractor();
        setContractorData(contractor, getComponentsData());
        boolean correctData = checkContractorConditions(contractor);

        if (correctData) {
            CompanyDataManager manager = new CompanyDataManager(DataTypeEnum.CONTRACTORS);
            int result = manager.addData(new Contractor(contractor));

            if (result < 0) {
                DialogMessage message = new DialogMessage(Alert.AlertType.ERROR);
                message.showDialog("Dodawanie kontrahenta nie powiodło się.");
            } else {
                refreshControllerList();
            }
            this.cancel();
        } else {
            DialogMessage message = new DialogMessage(Alert.AlertType.ERROR);
            message.showDialog("Podane dane są nieprawidłowe.");
        }
    }
}
