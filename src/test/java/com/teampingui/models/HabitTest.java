package com.teampingui.models;

import com.teampingui.exceptions.NotInDatabaseException;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HabitTest {

    Habit mHabit;

    @BeforeEach
    public void createHabit() {
        mHabit = new Habit("Test", new boolean[7]);
    }

    @Test
    void setDBID() throws NotInDatabaseException {
        mHabit.setDBID(999999);
        assertEquals(999999, mHabit.getDBID());
    }

    @Test
    void setCheckedDays() {
        final boolean[] checkedDaysBoolean = {true, true, false, true, true, false, false};
        mHabit.setCheckedDays(checkedDaysBoolean);
        assertTrue(mHabit.checkedDays(Day.MONDAY).getValue());
        assertTrue(mHabit.checkedDays(Day.TUESDAY).getValue());
        assertFalse(mHabit.checkedDays(Day.WEDNESDAY).getValue());
        assertTrue(mHabit.checkedDays(Day.THURSDAY).getValue());
        assertTrue( mHabit.checkedDays(Day.FRIDAY).getValue());
        assertFalse(mHabit.checkedDays(Day.SATURDAY).getValue());
        assertFalse(mHabit.checkedDays(Day.SUNDAY).getValue());
    }

    @Test
    void testSetChecked() {
        mHabit.setChecked(2,true);
        assertTrue(mHabit.checkedDays(Day.WEDNESDAY).getValue());
    }

    @Test
    void nameProperty() {
        assertEquals("Test", mHabit.nameProperty().getValue());
    }

    @Test
    void checkedDays() {
        mHabit.setChecked(Day.WEDNESDAY, true);
        assertTrue(mHabit.checkedDays(Day.WEDNESDAY).getValue());
    }

    @Test
    void repsProperty(){
        assertEquals(0,mHabit.repsProperty().getValue());
    }

    @Test
    void hasToBeDone() {
        assertFalse(mHabit.hasToBeDone(Day.MONDAY));
    }

    @Test
    void hasToBeDoneList() {
        boolean[] check = {false, false, false, false, false, false, false};
        assertArrayEquals(check, mHabit.hasToBeDoneList());
    }
}