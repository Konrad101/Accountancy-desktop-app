package views;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class IconSetter {
    private final static String APP_ICON_PATH = "/images/icon.png";

    public static void setDialogIcon(Alert dialog) {
        if (dialog != null) {
            DialogPane pane = dialog.getDialogPane();
            setStageIcon((Stage) pane.getScene().getWindow());
        }
    }

    public static void setStageIcon(Stage stage) {
        if (stage != null)
            stage.getIcons().add(new Image(APP_ICON_PATH));
    }
}
