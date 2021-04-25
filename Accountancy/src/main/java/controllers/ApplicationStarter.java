package controllers;

import javafx.application.Application;
import javafx.stage.Stage;
import models.database.dataManagers.BackupManager;
import models.database.dataManagers.DatabaseManager;


public class ApplicationStarter extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        DatabaseManager.createProgramData();
        BackupManager backupManager = new BackupManager();
        if (backupManager.backupIsNecessary()) {
            backupManager.makeBackup();
        }
        MenuStarter starter = new MenuStarter(primaryStage);
        starter.runMenu();
    }
}
