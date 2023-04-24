package com.teampingui.controllers;

import com.teampingui.Main;
import com.teampingui.dao.HabitDAO;
import com.teampingui.dao.JournalDAO;
import com.teampingui.exceptions.NotInDatabaseException;
import com.teampingui.interfaces.IDao;
import com.teampingui.models.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    private static final Logger log = LogManager.getLogger(MainController.class);
    // DAO
    private final IDao<JournalEntry> mJournalDAO = new JournalDAO();
    private final IDao<Habit> mHabitDAO = new HabitDAO();
    @FXML
    AnchorPane apBackground;
    //General Layout
    @FXML
    Label lWelcome;
    @FXML
    Label lMiniJournal;
    //Journal
    @FXML
    TextArea taNewJournal; // Hier auslesen
    @FXML
    Button btnAddJournal;
    @FXML
    Label wordCount;
    @FXML
    ListView<JournalEntry> lvJournal;
    //Habits
    @FXML
    Button btnAddHabit;
    @FXML
    Button btnRemoveHabit;
    @FXML
    Button btnHabits, btnSettings;
    @FXML
    Label lDate;
    @FXML
    private ProgressBar habitsProgress;
    @FXML
    private Label progressDisplay;
    @FXML
    private TableView<Habit> tvHabits = new TableView<>();


    private int haveTodoCounter = 0;
    private int doneCounter = 0;
    private boolean mRemoveButtonsVisible = false;
    private LocalDate mDate = LocalDate.now();

    private ErrorDialog mErrorDialog;

    public MainController() {
    }

    private static int countLines(String str) {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }

    /**
     * Needed, because this class implements Initializable
     * Called to initialize a controller after its root element has been completely processed.
     *
     * @param url            The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     *                       --> Both params are not used in this method.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ErrorDialog
        mErrorDialog = DialogFactory.createDialog(DialogType.ERROR, apBackground);

        // Welcome Message
        lWelcome.setText(Settings.getUsername().isEmpty() ? "Welcome!" : "Welcome " + Settings.getUsername() + "!");

        // Journal
        lvJournal.setItems(mJournalDAO.getAll());
        lvJournal.setCellFactory(studentListView -> new JournalEntryListCell());
        // journal entry max length
        final int MAX_CHARS = 200;
        //journal entry max rows
        final int MAX_LINES = 7;
        taNewJournal.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= MAX_CHARS && countLines(change.getControlNewText()) <= MAX_LINES ? change : null));


        //journal wordCount
        wordCount.textProperty().bind(taNewJournal.textProperty().length().asString("%d/" + MAX_CHARS));

        // Date for Habit Table
        displayTableDate();

        // Habits
        dynamicallyAddTableCols();
        tvHabits.setItems(mHabitDAO.getAll());
        tvHabits.setEditable(true);

        updateProgressBar();
        mHabitDAO.getAll().addListener((ListChangeListener<Habit>) change -> updateProgressBar());
    }

    private void updateProgressBar() {
        if (progressDisplay == null)
            return;

        // Set ProgressBar
        doneCounter = 0;
        haveTodoCounter = 0;
        for (Habit habit : mHabitDAO.getAll()) {
            haveTodoCounter += habit.repsProperty().getValue();
            doneCounter += Arrays.stream(Day.values()).filter(day -> habit.checkedDays(day).getValue() && habit.hasToBeDone(day)).count();
        }
        double percentage = (double) doneCounter / haveTodoCounter;
        habitsProgress.setProgress(percentage);
        progressDisplay.setText((int) (percentage * 100) + "% achieved");
    }

    @FXML
    public void switchScenes(ActionEvent e) {
        Main.getInstance().sceneSwitch(e, btnHabits, btnSettings);
    }

    @FXML
    private void addNewEntry() {
        String sEntry = taNewJournal.getText().trim();

        // Text empty
        if (sEntry.length() <= 0) {
            mErrorDialog.setMsg("You have to select at least one day!");
            mErrorDialog.show();
            log.warn("Input field can not be empty!");
            return;
        }

        // Set current date for journal entry
        String sCurrentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        JournalEntry newJournalEntry = new JournalEntry(sCurrentDate, sEntry);

        try {
            mJournalDAO.insert(newJournalEntry);
            taNewJournal.clear();
            log.info("New entry added: " + newJournalEntry);
        } catch (Exception exception) {
            log.error("Failed to add new entry.", exception);
        }

    }

    @FXML
    void openHabitDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AddHabitDialog.fxml"));
        Parent parent = fxmlLoader.load();
        AddHabitDialogController dialogController = fxmlLoader.getController();
        dialogController.setHabitDAO(mHabitDAO);

        Scene scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getResource("/css/stylesheet.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * Adds a new Column to the tableview (tcDelete) with boolean type
     * When selecting the Habit you want to remove, an alert window pops up
     * When pressing "yes" -> Habit gets deleted from Database. "No" -> Action is cancelled
     */
    @FXML
    public void removeHabit() {
        mRemoveButtonsVisible = !mRemoveButtonsVisible;
        if (mRemoveButtonsVisible) {
            TableColumn<Habit, Boolean> tcDelete = new TableColumn<>("");
            tcDelete.setPrefWidth(28);
            tcDelete.setSortable(false);

            double newWidth = tvHabits.getColumns().get(8).getWidth() - 28;
            tvHabits.getColumns().get(8).setPrefWidth(newWidth);
            tvHabits.getColumns().add(0, tcDelete);

            tcDelete.setCellValueFactory(habitStringCellDataFeatures -> new SimpleBooleanProperty(habitStringCellDataFeatures.getValue() != null));
            tcDelete.setCellFactory(habitStringTableColumn -> new ButtonCell(index -> {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Delete Habit");
                alert.setContentText("Do you want to delete the Habit '" + tvHabits.getItems().get(index).nameProperty().getValue() + "'?");
                ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(btnYes, btnNo);
                alert.showAndWait().ifPresent(type -> {
                    if (type == btnYes) {
                        mHabitDAO.delete(tvHabits.getItems().get(index));
                    } else {
                        alert.close();
                    }
                });
            }));
        } else {
            tvHabits.getColumns().remove(0);
            double newWidth = tvHabits.getColumns().get(8).getWidth() + 28;
            tvHabits.getColumns().get(8).setPrefWidth(newWidth);
        }
    }

    /**
     * Dynamically adds the Columns to the tableview during runtime
     * Columns: Name if Habit, Weeksdays (Mon - Sun), Repetitions
     */
    private void dynamicallyAddTableCols() {
        // Column: habit name
        TableColumn<Habit, String> tcName = new TableColumn<>("Habit");
        tcName.setPrefWidth(158);
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Columns: days checkboxes
        ArrayList<TableColumn<Habit, Boolean>> alCheckboxes = new ArrayList<>();
        for (final Day day : Day.values()) {
            TableColumn<Habit, Boolean> tc = new TableColumn<>(day.getShortDay());
            tc.setPrefWidth(60);
            tc.setSortable(false);
            tc.setCellValueFactory(habitBooleanCellDataFeatures -> habitBooleanCellDataFeatures.getValue().checkedDays(day));
            tc.setCellFactory(checkbox -> new DayCell((isChecked, habit) -> checkboxClicked(isChecked, habit, day), day));
            alCheckboxes.add(tc);
        }

        // Columns: repetitions
        TableColumn<Habit, Integer> tcReps = new TableColumn<>("Reps");
        tcReps.setCellValueFactory(new PropertyValueFactory<>("reps"));
        tcReps.setPrefWidth(85);

        // Cols
        ObservableList<TableColumn<Habit, ?>> cols = tvHabits.getColumns();

        // add cols to table
        cols.add(tcName);
        cols.addAll(alCheckboxes);
        cols.add(tcReps);

    }

    /**
     * @param isChecked if the checkbox is checked its true
     * @param habit     the habit which belongs to the clicked checkbox (same row)
     * @param day       shows wich checkbox is clicked
     */
    private void checkboxClicked(boolean isChecked, Habit habit, Day day) {
        ((HabitDAO) mHabitDAO).setIsChecked(habit, mDate.with(day.getDayOfWeek()), isChecked);

        if (habit.hasToBeDone(day)) {
            doneCounter += isChecked ? 1 : -1;
            double percentage = (double) doneCounter / haveTodoCounter;
            habitsProgress.setProgress(percentage);
            progressDisplay.setText((int) (percentage * 100) + "% achieved");

            // Observable
            int habitIndex = mHabitDAO.indexOf(habit);
            Optional<Habit> updateHabit = mHabitDAO.get(habitIndex);
            updateHabit.ifPresent(value -> value.setChecked(day, isChecked));
        }
    }

    public void onClickWeekBefore() {
        changeWeek(false);
    }

    public void onClickWeekNext() {
        changeWeek(true);
    }

    private void changeWeek(boolean nextWeek) {
        try {
            if (nextWeek) {
                mDate = mDate.plusDays(7);
                ((HabitDAO) mHabitDAO).loadCheckedData(mDate);
            } else {
                mDate = mDate.minusDays(7);
                ((HabitDAO) mHabitDAO).loadCheckedData(mDate);
            }
            updateProgressBar();
            displayTableDate();
            log.info("Changed week successfully.");

        } catch (SQLException e) {
            log.error("Failed to change week!", e);
        } catch (NotInDatabaseException e) {
            e.printStackTrace();
        }
    }

    private void displayTableDate() {
        String fromDate = mDate.with(DayOfWeek.MONDAY).format(DateTimeFormatter.ofPattern("dd. MMMM"));
        String toDate = mDate.with(DayOfWeek.SUNDAY).format(DateTimeFormatter.ofPattern("dd. MMMM yyyy"));
        lDate.setText(fromDate + " - " + toDate);
    }
}