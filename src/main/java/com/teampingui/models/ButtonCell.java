package com.teampingui.models;

import com.teampingui.interfaces.IButtonClickListener;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

public class ButtonCell extends TableCell<Habit, Boolean> {
    final Button mCellButton = new Button("-");

    public ButtonCell(final IButtonClickListener clickListener) {
        mCellButton.setOnAction(t -> clickListener.onClick(getIndex()));
    }

    @Override
    protected void updateItem(Boolean t, boolean empty) {
        super.updateItem(t, empty);
        if (!empty) {
            setGraphic(mCellButton);
        } else {
            setGraphic(null);
        }
    }
}
