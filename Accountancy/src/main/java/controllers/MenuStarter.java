package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.database.Log;

import java.io.IOException;

public class MenuStarter {
    private final Stage menuStage;

    public MenuStarter(Stage menuStage) {
        this.menuStage = menuStage;
    }

    public void runMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxmlStages/menu/menu.fxml"));
            Parent root = loader.load();

            menuStage.setResizable(false);
            menuStage.getIcons().add(new Image("images/icon.png"));
            menuStage.setTitle("Menu");

            menuStage.setScene(new Scene(root));
            menuStage.centerOnScreen();
            menuStage.show();
            menuStage.setOnCloseRequest(e -> System.exit(0));
        } catch (IOException ex) {
            new Log().saveError("Error occurred during loading menu stage.");
        }
    }
}
