package com.teampingui.dao;

import com.teampingui.models.Habit;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class HabitTest {
    HabitDAO habitDAO = new HabitDAO();
    Habit mHabit;

    @BeforeEach
    public void createHabit() {
        mHabit = new Habit("TestHabit", new boolean[]{true, false, true, true, true, false, false});
    }
    
    @After
    public void deleteTestData() {
        habitDAO.delete(mHabit);
    }

    @Test
    public void testRemoveHabitEntry() {
        // First we need to add a new habit
        try {
            habitDAO.insert(mHabit);
        } catch (Exception e) {
            fail("Unexpected insert fail in testRemoveHabitEntry", e);
        }

        // Check if habit was added successfully
        Assumptions.assumeTrue(habitDAO.getAll().contains(mHabit));

        // Check if habit was deleted successfully
        habitDAO.delete(mHabit);
        Assumptions.assumeFalse(habitDAO.getAll().contains(mHabit));
    }

    @Test
    public void testInsertHabitEntry() {
        try {
            habitDAO.insert(mHabit);
        } catch (Exception e) {
            fail("Insert failed in testInsertHabitEntry", e);
        }
        // Does the list of entries contain the new entry?
        Assumptions.assumeTrue(habitDAO.getAll().contains(mHabit));
    }
}