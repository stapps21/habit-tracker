package com.teampingui.models;

import com.teampingui.interfaces.ICheckBoxClickListener;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;

public class DayCell extends TableCell<Habit, Boolean> {

    private final CheckBox mCheckBox = new CheckBox();
    private final Day mDay;

    public DayCell(ICheckBoxClickListener clickListener, final Day day) {
        this.mDay = day;

        mCheckBox.setOnAction(evt -> {
            clickListener.onPositionClicked(mCheckBox.isSelected(), getTableView().getItems().get(getIndex()));
            switchStyle();
        });

        this.setGraphic(mCheckBox);
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.setEditable(true);
    }

    /**
     * changes colors for checkboxes in Habit Table
     */
    private void switchStyle() {
        boolean hasTodo = getTableView().getItems().get(getIndex()).hasToBeDone(mDay);
        mCheckBox.getStyleClass().removeAll("cb-haveto", "cb-done", "cb-donthaveto");
        if (hasTodo && !mCheckBox.isSelected()) {
            mCheckBox.getStyleClass().add("cb-haveto");
        } else if (hasTodo && mCheckBox.isSelected()) {
            mCheckBox.getStyleClass().add("cb-done");
        } else {
            mCheckBox.getStyleClass().add("cb-donthaveto");
        }
    }

    @Override
    public void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            if (item != null) {
                mCheckBox.setAlignment(Pos.CENTER);
                mCheckBox.setSelected(item);
            }
            setAlignment(Pos.CENTER);
            setGraphic(mCheckBox);
            switchStyle();
        }
    }

    @Override
    public String toString() {
        return "DayCell: " +
                "mDay=" + mDay +
                ", checked=" + mCheckBox.isSelected();
    }
}
