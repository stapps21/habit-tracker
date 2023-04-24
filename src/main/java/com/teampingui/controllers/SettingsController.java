package com.teampingui.controllers;

import com.teampingui.Main;
import com.teampingui.models.DialogFactory;
import com.teampingui.models.DialogType;
import com.teampingui.models.ErrorDialog;
import com.teampingui.models.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;


public class SettingsController implements Initializable {

    private static final Logger log = LogManager.getLogger(SettingsController.class);

    @FXML
    AnchorPane apBackground;
    @FXML
    Button btnHabits, btnSettings;
    @FXML
    Label lName;
    @FXML
    TextField tfName;

    private ErrorDialog mSuccessDialog;

    @FXML
    public void switchScenes(ActionEvent e) {
        Main.getInstance().sceneSwitch(e, btnHabits, btnSettings);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ErrorDialog
        mSuccessDialog = DialogFactory.createDialog(DialogType.SUCCESS, apBackground);
        tfName.setText(Settings.getUsername());
    }

    @FXML
    protected void saveChanges() {
        Settings.setUsername(tfName.getText().trim());
        log.info("Settings saved");
        mSuccessDialog.setMsg("Settings saved successfully!");
        mSuccessDialog.show();
    }
}
