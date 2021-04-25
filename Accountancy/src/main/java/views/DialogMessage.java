package views;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class DialogMessage {
    private Alert.AlertType type;

    public DialogMessage() {
        type = Alert.AlertType.INFORMATION;
    }

    public DialogMessage(Alert.AlertType type) {
        this.type = type;
    }

    public boolean showConfirmationBeforeDelete(String message) {
        this.type = Alert.AlertType.CONFIRMATION;
        return this.askUser(message, "Uwaga");
    }

    public boolean askUser(String message, String header) {
        Alert alert = new Alert(this.type);
        alert.setTitle("");
        alert.setHeaderText(header);
        alert.setContentText(message);

        ButtonType yesButton = new ButtonType("Tak");
        ButtonType noButton = new ButtonType("Nie");

        IconSetter.setDialogIcon(alert);
        alert.getButtonTypes().setAll(yesButton, noButton);
        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == yesButton;
    }

    public void showDialog(String message) {
        Alert alert = new Alert(this.type);
        IconSetter.setDialogIcon(alert);

        alert.setTitle("");
        if (type == Alert.AlertType.ERROR) {
            alert.setHeaderText("Błąd");
        } else if (type == Alert.AlertType.WARNING) {
            alert.setHeaderText("Uwaga");
        } else {
            alert.setHeaderText("Info");
        }

        alert.setContentText(message);
        alert.showAndWait();
    }
}
