package com.teampingui.dao;

import com.teampingui.exceptions.JournalDaoException;
import com.teampingui.exceptions.NotInDatabaseException;
import com.teampingui.models.JournalEntry;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.fail;

public class NegativeJournalTest {
    JournalDAO journalDAO = new JournalDAO();
    JournalEntry journalEntry = new JournalEntry("05.05.1999", null);

    @Test
    public void testInsertJournalEntry() {
        try {
            journalDAO.insert(journalEntry);
        } catch (JournalDaoException e) {
            System.out.println("Right exception thrown (JournalDaoException)");
        } catch (Exception e) {
            fail("Unexpected exception call in testInsertJournalEntry. Expected JournalDaoException");
        }
    }

    //This test is for checking that an exception gets thrown, when the setID method in JournalEntryItem gets passed
    //a negative value
    @Test(expected = IllegalArgumentException.class)
    public void testSetIdOfJournalEntry() {
        try {
            JournalEntry journalEntry = new JournalEntry("20.04.2020", "This text came from testSetIdOfJournalEntry");
            journalEntry.setID(-1);
        } catch (IllegalArgumentException i) {
            fail("ad", i);
            throw new IllegalArgumentException(i);
        }
    }
}
