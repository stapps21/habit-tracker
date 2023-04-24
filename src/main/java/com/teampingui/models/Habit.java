package com.teampingui.models;

import com.teampingui.exceptions.NotInDatabaseException;
import javafx.beans.property.*;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Habit {

    private final BooleanProperty[] checkedDays = new BooleanProperty[7];
    private final boolean[] haveTodoDays = new boolean[7];
    private StringProperty name;
    private IntegerProperty reps;
    //For database
    private int mDB_ID = 0;

    public Habit(String name, boolean[] haveTodoDays) {
        init(name, haveTodoDays);
    }

    public Habit(int dbID, String name, boolean[] haveTodoDays) {
        this.mDB_ID = dbID;
        init(name, haveTodoDays);
    }

    private void init(String name, boolean[] haveTodoDays) {
        this.name = new SimpleStringProperty(name);
        int reps = 0;
        for (int i = 0; i < checkedDays.length; i++) {
                reps += haveTodoDays[i] ? 1 : 0;
            this.haveTodoDays[i] = haveTodoDays[i];
            this.checkedDays[i] = new SimpleBooleanProperty(false);
        }
        this.reps = new SimpleIntegerProperty(reps);
    }

    public int getDBID() throws NotInDatabaseException {
        if (mDB_ID == 0) {
            throw new NotInDatabaseException("Habit is not connected to database");
        }
        return mDB_ID;
    }

    public void setDBID(final int ID) {
        if (ID < 1) {
            throw new IllegalArgumentException();
        } else {
            mDB_ID = ID;
        }
    }

    public void setCheckedDays(boolean[] checkedDays) {
        IntStream.range(0, checkedDays.length).forEach(i -> this.checkedDays[i].set(checkedDays[i]));
    }

    public void setChecked(Day day, boolean checked) {
        checkedDays[day.ordinal()].set(checked);
    }

    public void setChecked(int indexDay, boolean checked) {
        checkedDays[indexDay].set(checked);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public BooleanProperty checkedDays(Day day) {
        return checkedDays[day.ordinal()];
    }

    public IntegerProperty repsProperty() {
        return reps;
    }

    public boolean hasToBeDone(Day day) {
        return haveTodoDays[day.ordinal()];
    }

    public boolean[] hasToBeDoneList() {
        return haveTodoDays;
    }

    @Override
    public String toString() {
        return "Habit: " +
                "name=" + name.getValue() +
                ", reps=" + reps.getValue() +
                ", checkedDays=" + Arrays.toString(checkedDays) +
                ", haveTodoDays=" + Arrays.toString(haveTodoDays) +
                ", id=" + mDB_ID;
    }
}
