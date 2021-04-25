package controllers.companyData.operations;

import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Contractor;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public abstract class AbstractContractorOperationController extends OperationController implements Initializable {
    public TextField NIPField;
    public TextArea nameField;
    public TextField addressField;
    public TextField zipCodeField;
    public TextField cityField;

    public TextField personDataField;
    public TextField phoneNumberField;
    public TextField emailField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void cancel() {
        turnOffStage((Stage) addressField.getScene().getWindow());
    }

    protected void setContractorData(Contractor contractor, String[] data) {
        if (contractor != null && data != null) {
            if (data.length > 7) {
                contractor.setNIP(contractor.removeEmptyCharsFromEnd(data[0]));
                contractor.setCompanyName(contractor.removeEmptyCharsFromEnd(data[1]));
                contractor.setCompanyAddress(contractor.removeEmptyCharsFromEnd(data[2]));
                contractor.setCompanyZipCode(contractor.removeEmptyCharsFromEnd(data[3]));
                contractor.setCompanyCity(contractor.removeEmptyCharsFromEnd(data[4]));
                contractor.setPersonData(contractor.removeEmptyCharsFromEnd(data[5]));
                contractor.setPhoneNumber(contractor.removeEmptyCharsFromEnd(data[6]));
                contractor.setEmail(contractor.removeEmptyCharsFromEnd(data[7]));
            }
        }
    }

    protected String[] getComponentsData() {
        List<String> data = new ArrayList<>();

        data.add(NIPField.getText());
        data.add(nameField.getText());
        data.add(addressField.getText());
        data.add(zipCodeField.getText());
        data.add(cityField.getText());
        data.add(personDataField.getText());
        data.add(phoneNumberField.getText());
        data.add(emailField.getText());

        String[] arrData = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            arrData[i] = data.get(i);
        }
        return arrData;
    }

    protected boolean checkContractorConditions(Contractor contractor) {
        boolean dataIsCorrect = true;
        if (contractor != null) {
            if (contractor.nipIsIncorrect()) {
                setLightWrongDataFieldEffect(NIPField);
                dataIsCorrect = false;
            }
            if (contractor.zipCodeIsIncorrect()) {
                setLightWrongDataFieldEffect(zipCodeField);
                dataIsCorrect = false;
            }
            if (contractor.dataIsIncorrect(contractor.getCompanyName())) {
                setLightWrongDataFieldEffect(nameField);
                dataIsCorrect = false;
            }
            if (contractor.dataIsIncorrect(contractor.getCompanyAddress())) {
                setLightWrongDataFieldEffect(addressField);
                dataIsCorrect = false;
            }
            if (contractor.dataIsIncorrect(contractor.getCompanyCity())) {
                setLightWrongDataFieldEffect(cityField);
                dataIsCorrect = false;
            }

        }
        return dataIsCorrect;
    }
}
