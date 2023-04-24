package com.teampingui;

import com.teampingui.dao.Database;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

public class Main extends Application {

    //Initializing the logger
    private static final Logger log = LogManager.getLogger(Main.class);
    // Singleton
    private static Main instance;
    private Stage mPrimaryStage;

    public Main() {
        instance = this;
    }

    public static Main getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        if (Database.isOK()) {
            try {
                mPrimaryStage = primaryStage;
                gotoMain();
                primaryStage.show();
                log.info(LocalDateTime.now() + ": Application started");
            } catch (Exception ex) {
                log.fatal("Failed to show the primary stage (" + ex.getMessage() + "). Shutting down the application.");
                System.exit(1);
            }
        } else {
            log.error(LocalDateTime.now() + ": Database could not be loaded");
        }
    }

    public void gotoMain() {
        try {
            replaceSceneContent("Main.fxml");
        } catch (Exception ex) {
            log.error("Failed to replace the scene content with the main page (" + ex.getMessage() + ").");
        }
    }

    public void gotoSettings() {
        try {
            replaceSceneContent("Settings.fxml");
        } catch (Exception ex) {
            log.error("Failed to show the scene content with the settings (" + ex.getMessage() + ").");
        }
    }

    private void replaceSceneContent(String fxml) throws Exception {
        FXMLLoader lloader = new FXMLLoader();
        lloader.setLocation(getClass().getResource("/fxml/" + fxml));
        Parent page = lloader.load();
        Scene scene = mPrimaryStage.getScene();
        if (scene == null) {
            scene = new Scene(page);
            //Importing our own css sheet
            scene.getStylesheets().add(getClass().getResource("/css/stylesheet.css").toExternalForm());
            mPrimaryStage.setScene(scene);
            mPrimaryStage.setResizable(false);
        } else {
            mPrimaryStage.getScene().setRoot(page);
        }
        mPrimaryStage.sizeToScene();
        log.info(LocalDateTime.now() + ": Replaced current scene content with " + fxml);
    }

    public void sceneSwitch(ActionEvent e, Button btnHabits, Button btnSettings) {
        Object source = e.getSource();
        if (btnHabits.equals(source)) {
            gotoMain();
        } else if (btnSettings.equals(source)) {
            gotoSettings();
        } else {
            throw new IllegalStateException("Unexpected value: " + e.getSource());
        }
    }

    public Stage getPrimaryStage() {
        return mPrimaryStage;
    }
}
