package com.teampingui.dao;

import com.teampingui.exceptions.JournalDaoException;
import com.teampingui.models.JournalEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;

class JournalTest {
    JournalDAO journalDAO = new JournalDAO();
    JournalEntry journalEntry = new JournalEntry("01.01.2020", "This text came from JournalTest.java");

    @Test
    public void testInsertJournalEntry() {
        try {
            journalDAO.insert(journalEntry);
        } catch (JournalDaoException | SQLException e) {
            fail("Insert journal was not successfully in testInsertJournalEntry", e);
        }
    }

    @AfterEach
    public void tearDown() {

        // Does the list of entries contain the new entry?
        Assumptions.assumeTrue(journalDAO.getAll().contains(journalEntry));
        System.out.println(journalDAO.getAll());

        // Remove last entry from list and database
        journalDAO.delete(journalEntry);
        System.out.println(journalDAO.getAll());
    }
}