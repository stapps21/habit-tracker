package com.teampingui.dao;

import com.teampingui.exceptions.JournalDaoException;
import com.teampingui.exceptions.NotInDatabaseException;
import com.teampingui.interfaces.IDao;
import com.teampingui.models.JournalEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JournalDAO implements IDao<JournalEntry> {
    private static final String DB_TABLE_NAME = "journal";
    private static final String DB_COLUMN_ID = "id";
    private static final String DB_COLUMN_DATE = "journal_date";
    private static final String DB_COLUMN_ENTRY = "entry";

    private static final Logger log = LogManager.getLogger(JournalDAO.class);

    private final ObservableList<JournalEntry> mosJournalEntries;

    public JournalDAO() {
        mosJournalEntries = FXCollections.observableArrayList();
        try {
            mosJournalEntries.addAll(read());
            log.info("Successfully load journal entries from database.");
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Could not load journal entries from database." + e.getMessage());

        }
    }

    private List<JournalEntry> read() throws SQLException {
        PreparedStatement statement = null;
        List<JournalEntry> journalEntries = new ArrayList<>();

        try (Connection connection = Database.connect()) {
            connection.setAutoCommit(false);
            String query = "SELECT * FROM " + DB_TABLE_NAME;
            statement = connection.prepareStatement(query);
            connection.commit();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                JournalEntry journalEntry = new JournalEntry(
                        resultSet.getInt(DB_COLUMN_ID),
                        resultSet.getString(DB_COLUMN_DATE),
                        resultSet.getString(DB_COLUMN_ENTRY)
                );
                log.debug("Loaded JournalEntry: " + journalEntry);
                journalEntries.add(journalEntry);
            }
        } catch (SQLException exception) {
            log.error("An error occurred while reading journal entries from database.", exception);
        } finally {
            if (null != statement) {
                statement.close();
            }
        }

        // Sort before return (the latest entry on top of the list)
        // Could also be done directly with SQL, but we need streams... :)
        return journalEntries.stream().sorted((o2, o1) -> o1.getDate().compareTo(o2.getDate())).
                collect(Collectors.toList());
    }

    @Override
    public Optional<JournalEntry> get(long id) {
        return Optional.ofNullable(mosJournalEntries.get((int) id));
    }

    @Override
    public ObservableList<JournalEntry> getAll() {
        return mosJournalEntries;
    }

    public int insert(JournalEntry journalEntry) throws SQLException, JournalDaoException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int id = 0;

        try (Connection connection = Database.connect()) {
            connection.setAutoCommit(false);
            String query = "INSERT INTO " + DB_TABLE_NAME + "(" + DB_COLUMN_DATE + ", " + DB_COLUMN_ENTRY + ") VALUES(?, ?)";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            int counter = 0;
            statement.setString(++counter, journalEntry.getDate());
            statement.setString(++counter, journalEntry.getContent());
            statement.executeUpdate();
            connection.commit();
            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
                journalEntry.setID(id);
                mosJournalEntries.add(0, journalEntry);
            }
            log.info("Successfully insert journal entry '" + journalEntry + "' into database.");
        } catch (SQLException exception) {
            log.error("An error occurred while inserting journal entry into database.", exception);
            throw new JournalDaoException(exception);
        } finally {
            if (null != resultSet) {
                resultSet.close();
            }

            if (null != statement) {
                statement.close();
            }
        }

        return id;
    }

    @Override
    public void update(int index, JournalEntry journalEntry) {
    }

    @Override
    public void delete(JournalEntry journalEntry) {
        try (Connection connection = Database.connect()) {
            connection.setAutoCommit(false);

            // Delete Habit from Database
            String query = "DELETE FROM " + DB_TABLE_NAME + " WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, journalEntry.getID());
            statement.executeUpdate();
            connection.commit();
            statement.close();
            log.info("Journal entry '" + journalEntry + "' was successfully deleted from the database.");
        } catch (SQLException exception) {
            log.error("An error occurred while deleting a journal entry  from the database." + exception.getMessage());
        } catch (NotInDatabaseException notInDatabaseException) {
            log.warn("Journal entry is not linked to a database entry", notInDatabaseException);
        }

        // Delete habit from List
        mosJournalEntries.remove(journalEntry);
    }

    @Override
    public int indexOf(JournalEntry journalEntry) {
        return mosJournalEntries.indexOf(journalEntry);
    }

}
