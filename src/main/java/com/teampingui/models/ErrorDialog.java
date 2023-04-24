package com.teampingui.models;

import com.teampingui.Main;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ErrorDialog {

    private static final Logger log = LogManager.getLogger(ErrorDialog.class);
    // Error Message
    private static final Integer ERROR_DIALOG_TIME = 3;
    private final IntegerProperty mDialogTime = new SimpleIntegerProperty(ERROR_DIALOG_TIME * 100);
    private final Parent mParent;
    private final String mHexBG;
    private final String mHexBorderColor;
    private VBox mVBox;
    private Label mLabel;
    private ProgressBar mProgressBar;
    private Timeline mTimeline;
    private Thread mThreadErrorMsg;
    private String mMsg;

    public ErrorDialog(Parent parent, String bgColor, String borderColor) {
        mHexBG = bgColor;
        mHexBorderColor = borderColor;
        mParent = parent;
        init();
    }

    private void init() {
        ReadOnlyDoubleProperty width = Main.getInstance().getPrimaryStage().widthProperty();

        mVBox = new VBox();
        mVBox.setVisible(false);
        mVBox.setId("vbErrorContainer");
        mVBox.prefHeight(43);
        mVBox.prefWidthProperty().bind(width);
        //mVBox.setBackground();
        mVBox.setStyle("" +
                "-fx-background-color: " + mHexBG + ";" +
                "-fx-border-color: " + mHexBorderColor + ";");

        mLabel = new Label();
        mLabel.setId("lErrorMsg");
        mLabel.setAlignment(Pos.CENTER);
        mLabel.prefWidthProperty().bind(width);
        mVBox.getChildren().add(mLabel);

        mProgressBar = new ProgressBar();
        mProgressBar.setId("pbErrorDuration");
        mProgressBar.prefWidthProperty().bind(width);
        mProgressBar.setProgress(0.5);
        mProgressBar.progressProperty().bind(mDialogTime.divide(ERROR_DIALOG_TIME * 100.0));
        mVBox.getChildren().add(mProgressBar);

        if (mParent instanceof AnchorPane) {
            mVBox.setLayoutY(51);
            ((AnchorPane) mParent).getChildren().add(mVBox);
        } else if (mParent instanceof GridPane) {
            ((GridPane) mParent).getChildren().add(0, mVBox);
        } else {
            log.info("Class-type not supported yet");
        }
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }

    public void show() {
        mVBox.setVisible(true);
        mLabel.setText(mMsg);

        if (mTimeline != null) {
            mTimeline.stop();
        }

        mDialogTime.set(ERROR_DIALOG_TIME * 100);
        mTimeline = new Timeline();
        mTimeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(ERROR_DIALOG_TIME),
                        new KeyValue(mDialogTime, 0))
        );
        mTimeline.playFromStart();

        Runnable runnable = () -> {
            log.debug("error message threat started");
            try {
                Thread.sleep(ERROR_DIALOG_TIME * 1000L);
                Platform.runLater(() -> mVBox.setVisible(false));
                log.debug("error message is now invisible");
            } catch (InterruptedException e) {
                log.debug("error message thread interrupted");
            }
        };

        if (mThreadErrorMsg != null && mThreadErrorMsg.isAlive()) {
            mThreadErrorMsg.interrupt();
        }

        mThreadErrorMsg = new Thread(runnable);
        mThreadErrorMsg.start();
    }

}
