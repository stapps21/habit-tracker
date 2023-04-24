package com.teampingui.dao;

import com.teampingui.exceptions.HabitDaoException;
import com.teampingui.exceptions.NotInDatabaseException;
import com.teampingui.interfaces.IDao;
import com.teampingui.models.Day;
import com.teampingui.models.Habit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class HabitDAO implements IDao<Habit> {
    private static final String DB_TABLE_HABIT = "habit";
    private static final String DB_TABLE_HAVETODODAYS = "haveTodoDays";
    private static final String DB_TABLE_CHECKEDDAYS = "checkedDays";

    private static final String DB_COLUMN_NAME = "name";
    private static final String DB_COLUMN_ID = "id";
    private static final String DB_COLUMN_HABITID = "habit_id";
    private static final String DB_COLUMN_REPS = "reps";


    private static final Logger log = LogManager.getLogger(HabitDAO.class);

    private final ObservableList<Habit> mosHabits;

    public HabitDAO() {
        mosHabits = FXCollections.observableArrayList();
        try {
            mosHabits.addAll(read());
            loadCheckedData(LocalDate.now());
        } catch (SQLException e) {
            log.error(LocalDateTime.now() + ": could not load habits from database." + e.getMessage());
        } catch (NotInDatabaseException e) {
            e.printStackTrace();
        }

    }

    /**
     * @return List of habit Entries from Database
     * @throws SQLException
     */
    private List<Habit> read() throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        List<Habit> habitEntries = new ArrayList<>();

        try (Connection connection = Database.connect()) {

            String getStringQuery =
                    "SELECT " + DB_TABLE_HABIT + "." + DB_COLUMN_ID + ", " + DB_TABLE_HABIT + "." + DB_COLUMN_NAME + ", GROUP_CONCAT(" + DB_TABLE_HAVETODODAYS + ".weekday) AS weekdays " +
                            "FROM " + DB_TABLE_HABIT + ", " + DB_TABLE_HAVETODODAYS +
                            " WHERE " + DB_TABLE_HABIT + "." + DB_COLUMN_ID + " = " + DB_TABLE_HAVETODODAYS + "." + DB_COLUMN_HABITID +
                            " AND " + DB_TABLE_HAVETODODAYS + ".havetodo = 1" +
                            " GROUP BY " + DB_TABLE_HABIT + "." + DB_COLUMN_ID + ";";

            log.debug(getStringQuery);

            statement = connection.prepareStatement(getStringQuery);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // Getting haveTodoDays Boolean Array
                List<String> splitConcat = Arrays.asList(resultSet.getString("weekdays").split(","));
                List<Integer> weekdays = splitConcat.stream().map(Integer::parseInt).toList();
                boolean[] haveTodoDays = new boolean[7];
                weekdays.forEach(i -> haveTodoDays[i] = true);

                habitEntries.add(new Habit(
                        resultSet.getInt(DB_COLUMN_ID),
                        resultSet.getString(DB_COLUMN_NAME),
                        haveTodoDays)
                );
                log.debug("Loaded habit. ID=" + resultSet.getString(DB_COLUMN_ID));
            }
            log.info("Habits were loaded successfully.");
        } catch (SQLException e) {
            log.error(LocalDateTime.now() + ": could not load habits from database." + e.getMessage());
            mosHabits.clear();
        } finally {
            if (null != resultSet) {
                resultSet.close();
            }

            if (null != statement) {
                statement.close();
            }
        }

        return habitEntries;
    }

    public void loadCheckedData(LocalDate date) throws SQLException, NotInDatabaseException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String fromDate = date.with(DayOfWeek.MONDAY).toString();
        String toDate = date.with(DayOfWeek.SUNDAY).toString();

        try (Connection connection = Database.connect()) {

            String getStringQuery = "SELECT " + DB_TABLE_HABIT + ".id, " + DB_TABLE_HABIT + "." + DB_COLUMN_NAME + ", GROUP_CONCAT(" + DB_TABLE_CHECKEDDAYS + ".entry_date) AS done_days " +
                    "FROM " + DB_TABLE_HABIT + ", " + DB_TABLE_CHECKEDDAYS +
                    " WHERE " + DB_TABLE_HABIT + ".id = " + DB_TABLE_CHECKEDDAYS + "." + DB_COLUMN_HABITID +
                    " AND entry_date >= date('" + fromDate + "') AND entry_date <= date('" + toDate + "') " +
                    "GROUP BY habit.id;";

            statement = connection.prepareStatement(getStringQuery);
            resultSet = statement.executeQuery();

            resultSet.next();
            for (Habit rootHabit : mosHabits) {

                boolean sameHabit = false;
                try {
                    sameHabit = !resultSet.isClosed() && (rootHabit.getDBID() == resultSet.getInt(DB_COLUMN_ID));
                } catch (NotInDatabaseException notInDatabaseException) {
                    // ignore
                }

                if (sameHabit) {
                    List<String> splitConcat = Arrays.asList(resultSet.getString("done_days").split(","));

                    boolean[] doneDays = new boolean[7];
                    splitConcat.forEach(sDate -> doneDays[LocalDate.parse(sDate).getDayOfWeek().getValue() - 1] = true);

                    int index = mosHabits.indexOf(rootHabit);
                    mosHabits.get(index).setCheckedDays(doneDays);
                    log.debug("Loaded habit checks. ID=" + rootHabit.getDBID());
                    resultSet.next();
                } else {
                    log.debug("Loaded habit checks (blank). ID=" + rootHabit.getDBID());
                    int index = mosHabits.indexOf(rootHabit);
                    mosHabits.get(index).setCheckedDays(new boolean[7]);
                }
            }
        } finally {
            if (null != resultSet) {
                resultSet.close();
            }

            if (null != statement) {
                statement.close();
            }
        }
    }


    @Override
    public Optional<Habit> get(long id) {
        return Optional.ofNullable(mosHabits.get((int) id));
    }

    @Override
    public ObservableList<Habit> getAll() {
        return mosHabits;
    }

    @Override
    public int insert(Habit habit) throws Exception {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int id = 0;

        try (Connection connection = Database.connect()) {
            connection.setAutoCommit(false);

            String query = "INSERT INTO " + DB_TABLE_HABIT + " (" + DB_COLUMN_NAME + ", " + DB_COLUMN_REPS + ") VALUES (?,?)";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            int counter = 0;
            statement.setString(++counter, habit.nameProperty().getValue());
            statement.setString(++counter, null);
            statement.executeUpdate();
            connection.commit();
            resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                id = resultSet.getInt(1);

                query = "INSERT INTO " + DB_TABLE_HAVETODODAYS + " (" + DB_COLUMN_HABITID + ", weekday, havetodo) VALUES (?,?,?)";
                statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                for (Day day : Day.values()) {
                    if (!habit.hasToBeDone(day))
                        continue;

                    counter = 0;
                    statement.setInt(++counter, id);
                    statement.setInt(++counter, day.ordinal());
                    statement.setInt(++counter, 1);
                    statement.executeUpdate();
                }
                connection.commit();

                habit.setDBID(id);
                mosHabits.add(habit);
                log.info("Habit '" + habit + "' was inserted successfully into the database.");
            }
        } catch (SQLException exception) {
            log.error("An error occurred while inserting a habit into the database." + exception.getMessage());
            throw new HabitDaoException(exception);
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
    public void update(int index, Habit habit) {
    }

    @Override
    public void delete(Habit habit) {
        try (Connection connection = Database.connect()) {

            // Delete Habit from Database
            String query = "DELETE FROM " + DB_TABLE_HABIT + " WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, habit.getDBID());
            statement.executeUpdate();
            statement.close();
            log.info("Habit '" + habit + "' was successfully deleted from the database.");
        } catch (SQLException exception) {
            log.error("An error occurred while deleting a habit from the database." + exception.getMessage());
        } catch (NotInDatabaseException notInDatabaseException) {
            log.warn("Habit is not linked to a database entry", notInDatabaseException);
        }

        // Delete habit from List
        mosHabits.remove(habit);
    }

    public int indexOf(Habit habit) {
        return mosHabits.indexOf(habit);
    }

    public void setIsChecked(Habit habit, LocalDate date, boolean isChecked) {
        String checkDate = date.toString();

        try (Connection connection = Database.connect()) {

            String query;

            if (isChecked) {
                query = "INSERT INTO " + DB_TABLE_CHECKEDDAYS + " (habit_id, entry_date) VALUES (?, date('" + checkDate + "'));";
            } else {
                query = "DELETE FROM " + DB_TABLE_CHECKEDDAYS + " WHERE habit_id =? AND entry_date = date('" + checkDate + "');";
            }

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, habit.getDBID());
            statement.executeUpdate();
            connection.commit();
            statement.close();
            mosHabits.get(indexOf(habit)).setChecked(date.getDayOfWeek().getValue() - 1, isChecked);


            log.info("Habit checks were successfully updated from the database.");
        } catch (SQLException exception) {
            log.error("An error occurred while loading checkedDays from the database." + exception.getMessage());
        } catch (NotInDatabaseException notInDatabaseException) {
            log.warn("Habit is not linked to a database entry", notInDatabaseException);
        }
    }
}

