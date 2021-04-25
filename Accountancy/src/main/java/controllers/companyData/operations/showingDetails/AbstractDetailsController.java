package controllers.companyData.operations.showingDetails;

import controllers.companyData.operations.OperationController;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public abstract class AbstractDetailsController extends OperationController {
    private static final double FONT_SIZE = 13.5;

    protected void centerLabelText(Label label) {
        if (label != null) {
            label.setAlignment(Pos.CENTER);
        }
    }

    protected void changeLabelFontSize(Label label) {
        if (label != null) {
            label.setFont(new Font(FONT_SIZE));
        }
    }
}
