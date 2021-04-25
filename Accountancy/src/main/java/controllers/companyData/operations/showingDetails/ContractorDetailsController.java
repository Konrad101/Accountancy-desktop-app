package controllers.companyData.operations.showingDetails;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Contractor;
import models.TableData;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ContractorDetailsController extends AbstractDetailsController implements Initializable, DetailsOperation {
    public Label NIPLabel;
    public Label companyNameLabel;
    public Label addressLabel;
    public Label zipCodeLabel;
    public Label companyCityLabel;

    public Label personDataLabel;
    public Label phoneNumberLabel;
    public Label emailLabel;

    protected List<Label> labels = new ArrayList<>();
    protected Contractor contractor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createLabelsList();
        for (Label label : labels) {
            centerLabelText(label);
            changeLabelFontSize(label);
        }
    }

    private void createLabelsList() {
        labels.add(NIPLabel);
        labels.add(companyNameLabel);
        labels.add(addressLabel);
        labels.add(zipCodeLabel);
        labels.add(companyCityLabel);
        labels.add(personDataLabel);
        labels.add(phoneNumberLabel);
        labels.add(emailLabel);
    }

    @Override
    public void setTableData(TableData data) {
        if (data instanceof Contractor) {
            this.contractor = (Contractor) data;
        }
    }

    @Override
    public void loadDataToComponents() {
        if (this.contractor != null) {
            this.NIPLabel.setText(contractor.getPrintFormatNIP());
            this.companyNameLabel.setText(contractor.getCompanyName());
            this.addressLabel.setText(contractor.getCompanyAddress());
            this.zipCodeLabel.setText(contractor.getCompanyZipCode());
            this.companyCityLabel.setText(contractor.getCompanyCity());
            this.personDataLabel.setText(contractor.getPersonData());
            this.phoneNumberLabel.setText(contractor.getPrintFormatPhoneNumber());
            this.emailLabel.setText(contractor.getEmail());
        }
    }

    public void cancel() {
        this.turnOffStage((Stage) companyNameLabel.getScene().getWindow());
    }

}
