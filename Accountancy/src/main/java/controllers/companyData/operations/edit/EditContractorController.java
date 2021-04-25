package controllers.companyData.operations.edit;

import controllers.companyData.operations.AbstractContractorOperationController;
import javafx.scene.control.Alert;
import models.Contractor;
import models.DataTypeEnum;
import models.TableData;
import models.database.dataManagers.CompanyDataManager;
import views.DialogMessage;

public class EditContractorController extends AbstractContractorOperationController implements EditOperation {
    private int id;
    private Contractor contractor;

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public void setTableData(TableData data) {
        contractor = (Contractor) data;
    }

    @Override
    public void loadDataToComponents() {
        if (contractor != null) {
            this.NIPField.setText(contractor.getNIP());
            this.nameField.setText(contractor.getCompanyName());
            this.addressField.setText(contractor.getCompanyAddress());
            this.zipCodeField.setText(contractor.getCompanyZipCode());
            this.cityField.setText(contractor.getCompanyCity());
            this.personDataField.setText(contractor.getPersonData());
            this.phoneNumberField.setText(contractor.getPhoneNumber());
            this.emailField.setText(contractor.getEmail());
        }
    }

    public void editContractor() {
        Contractor contractor = new Contractor();
        setContractorData(contractor, getComponentsData());

        if (checkContractorConditions(contractor) && id != -1) {
            if (!this.contractor.equals(contractor)) {
                CompanyDataManager manager = new CompanyDataManager(DataTypeEnum.CONTRACTORS);
                int result = manager.editData(this.id, contractor);
                if (result < 0) {
                    DialogMessage message = new DialogMessage(Alert.AlertType.ERROR);
                    message.showDialog("Edytowanie kontrahenta nie powiodło się.");
                } else {
                    refreshControllerList();
                }
            }
            this.cancel();
        } else {
            DialogMessage message = new DialogMessage(Alert.AlertType.ERROR);
            message.showDialog("Podane dane są nieprawidłowe");
        }
    }
}
