package com.teampingui.dao;

import com.teampingui.exceptions.NotInDatabaseException;
import com.teampingui.models.Habit;
import org.junit.Test;

import static org.junit.Assert.fail;

public class NegativeHabitTest {

    Habit invalidIDHabit = new Habit("TestHabit", new boolean[7]);

    @Test
    public void testSetDbIdInvalid() {
        int negativeID = -99;
        try {
            invalidIDHabit.setDBID(negativeID);
        } catch (IllegalArgumentException e) {
            System.out.println("Successfully caught negative value in setDB_ID(): " + e);
        } catch (Exception e) {
            fail("Wrong exception thrown");
        }
    }

    @Test
    public void testGetDbIdInvalid() {
        try {
            invalidIDHabit.getDBID();
        } catch (NotInDatabaseException e) {
            System.out.println("Successfully caught invalid ID in getDB_ID(): " + e);
        } catch (Exception e) {
            fail("Wrong exception thrown");
        }
    }
}
