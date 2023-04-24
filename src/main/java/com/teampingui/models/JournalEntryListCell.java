package com.teampingui.models;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.IOException;

public class JournalEntryListCell extends ListCell<JournalEntry> {
    @FXML
    private Label dateLabel;

    @FXML
    private Text entryText;

    @FXML
    private VBox vbox;

    @FXML
    private FXMLLoader mLLoader;


    @Override
    protected void updateItem(JournalEntry jEntry, boolean empty) {
        super.updateItem(jEntry, empty);

        if (empty || jEntry == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader();
                mLLoader.setLocation(getClass().getResource("/fxml/JournalCell.fxml"));

                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            entryText.wrappingWidthProperty().bind(vbox.widthProperty());
            dateLabel.setText(jEntry.getDate());
            entryText.setText(jEntry.getContent());

            // set the width's
            vbox.setMinWidth(255);
            vbox.setMaxWidth(255);
            vbox.setPrefWidth(255);

            dateLabel.setText(jEntry.getDate());
            entryText.setText(jEntry.getContent());

            Text text = (Text) vbox.getChildren().get(1);
            text.setTextAlignment(TextAlignment.LEFT);


            setText(null);
            setGraphic(vbox);
        }
    }
}
