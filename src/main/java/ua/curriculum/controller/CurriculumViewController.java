package ua.curriculum.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.curriculum.MainApp;
import ua.curriculum.dao.AttendanceDao;
import ua.curriculum.dao.AttendanceStateDao;
import ua.curriculum.dao.CurriculumDao;
import ua.curriculum.export.ExportData;
import ua.curriculum.export.ExportFileType;
import ua.curriculum.export.ExportServices;
import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.fx.DialogText;
import ua.curriculum.fx.Dialogs;
import ua.curriculum.fx.ImageResources;
import ua.curriculum.model.Attendance;
import ua.curriculum.model.Curriculum;
import ua.curriculum.utils.DateUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CurriculumViewController {
    private Logger logger = LogManager.getLogger(this.getClass());

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tabTable;
    @FXML
    private Tab tabView;
    @FXML
    private Tab tabFilter;


    @FXML
    private HBox hBoxOkCancel;
    @FXML
    private HBox hBoxFilter;

    @FXML
    private HBox hBoxEdit;
    @FXML
    private HBox hBoxNavigation;


    @FXML
    private Button buttonNew;
    @FXML
    private Button buttonEdit;
    @FXML
    private Button buttonDelete;
    @FXML
    private Button buttonFirst;
    @FXML
    private Button buttonPrior;
    @FXML
    private Button buttonNext;
    @FXML
    private Button buttonLast;
    @FXML
    private Button buttonRefresh;
    @FXML
    private Button buttonCopyAllDateCurriculum;


    @FXML
    private Button buttonOk;
    @FXML
    private Button buttonCancel;
    @FXML
    private Button buttonFilter;
    @FXML
    private Button buttonCopy;

    @FXML
    private TableView<Curriculum> tableViewCurriculumData;

    @FXML
    private TableColumn<Curriculum, Integer> tableColumnLessonNo;
    @FXML
    private TableColumn<Curriculum, String> tableColumnLessonTime;
    @FXML
    private TableColumn<Curriculum, String> tableColumnLessonTypeName;
    @FXML
    private TableColumn<Curriculum, String> tableColumnSubjectName;
    @FXML
    private TableColumn<Curriculum, String> tableColumnTeacherPIP;
    @FXML
    private TableColumn<Curriculum, String> tableColumnGroupName;
    @FXML
    private TableColumn<Curriculum, String> tableColumnClassroom;


    @FXML
    private TableView<Attendance> tableViewAttendanceData;
    @FXML
    private TableColumn<Attendance, String> tableColumnStudentID;
    @FXML
    private TableColumn<Attendance, String> tableColumnStudentPIP;
    @FXML
    //private TableColumn<Attendance, String> tableColumnAttendanceState;
    private TableColumn<Attendance, ComboBoxItem> tableColumnAttendanceState;

    @FXML
    private TextField textFViewLesson;
    @FXML
    private TextField textFViewLessonType;
    @FXML
    private TextField textFViewTime;
    @FXML
    private TextField textFViewSubject;
    @FXML
    private TextField textFViewGroup;
    @FXML
    private TextField textFViewClassroom;
    @FXML
    private TextField textFViewTeacher;

    @FXML
    private TextField textFFilterLesson;
    @FXML
    private TextField textFFilterLessonType;
    @FXML
    private TextField textFFilterTime;
    @FXML
    private TextField textFFilterSubject;
    @FXML
    private TextField textFFilterGroup;
    @FXML
    private TextField textFFilterClassroom;
    @FXML
    private TextField textFFilterTeacher;

    @FXML
    private DatePicker datePickerSelectedDate;

    @FXML
    private MenuButton menuButtonReports;
    @FXML
    private MenuItem menuItemExcel;
    @FXML
    private MenuItem menuItemJasper;

    private CurriculumDao curriculumDao;
    private AttendanceDao attendanceDao;
    private AttendanceStateDao attendanceStateDao;

    private ObservableList<Curriculum> curriculumObservableList = FXCollections.observableArrayList();
    private FilteredList<Curriculum> filteredData;
    private ObservableList<Attendance> attendanceObservableList = FXCollections.observableArrayList();
    private ObservableList<ComboBoxItem> comboBoxItemsAttendanceState = FXCollections.observableArrayList();

    private MainApp mainApp;

    private Stage dialogStage;
    private boolean isEdited;
    private boolean isFilter;

    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        initHBoxes(true);

        buttonCopyAllDateCurriculum.setVisible(mainApp.getLoginUser().getUserType().getId() != 3);
        tableViewAttendanceData.setEditable(mainApp.getLoginUser().getUserType().getId() != 3);

        curriculumDao = new CurriculumDao(mainApp.getConnection());
        attendanceDao = new AttendanceDao(mainApp.getConnection());
        attendanceStateDao = new AttendanceStateDao(mainApp.getConnection());


        try {
            comboBoxItemsAttendanceState = FXCollections.observableArrayList(attendanceStateDao.findAllComboBoxData());

            tableColumnAttendanceState.setCellFactory(ComboBoxTableCell.forTableColumn(comboBoxItemsAttendanceState));

        } catch (SQLException e) {
            logger.error(e.getMessage());
            //e.printStackTrace();
        }

        datePickerSelectedDate.setValue(DateUtil.getLocalDate(new Date()));

    }

    private void refreshCurriculumTableData() {
        showCurriculumDetails(null);
        curriculumObservableList.clear();
        tableViewCurriculumData.getItems().removeAll(tableViewCurriculumData.getItems());

    }

    private void refreshAttendanceTableData() {
        attendanceObservableList.clear();
        tableViewAttendanceData.getItems().removeAll(tableViewAttendanceData.getItems());
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void initialize() {
        tabPane.setStyle("-fx-open-tab-animation: NONE; -fx-close-tab-animation: NONE;");

        tableViewAttendanceData.setPlaceholder(new Label("Дані відсутні"));

        initButtonsIcons();
        initButtonsToolTip();

        initTabPane();

        initColumnValueFactories();

        showCurriculumDetails(null);

        initComponentListeners();
    }

    private void initComponentListeners() {
        tableViewCurriculumData.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showCurriculumDetails(newValue));

        datePickerSelectedDate.valueProperty().addListener((ov, oldValue, newValue) -> {

            //logger.warn("New value: " + newValue);
            try {
                fillCurriculumDataOnDate(newValue);
            } catch (SQLException e) {
                //e.printStackTrace();
                Dialogs.showErrorDialog(e, new DialogText("Помилка отримання даних", "",
                                                          "Неможливо отримати данні з " + "таблиці 'Розклад занять'"),
                                        logger);
            }
        });


        tableColumnAttendanceState.setOnEditCommit((TableColumn.CellEditEvent<Attendance, ComboBoxItem> event) -> {
            TablePosition<Attendance, ComboBoxItem> pos = event.getTablePosition();

            ComboBoxItem newComboBoxItem = event.getNewValue();
            int row = pos.getRow();
            Attendance attendance = event.getTableView().getItems().get(row);

            try {
                attendanceDao.updateState(attendance, Integer.parseInt(newComboBoxItem.getObjectId()));
                attendance.setAttendanceState(
                        attendanceStateDao.findById(Integer.parseInt(newComboBoxItem.getObjectId())));
            } catch (SQLException e) {
                //e.printStackTrace();
                Dialogs.showErrorDialog(e, new DialogText("Помилка редагування даних", "Неможливо змінити стан для " +
                                                                                       attendance.getStudent()
                                                                                               .getFullPIP(),
                                                          e.getMessage()), logger);
            }
        });

        //genderCol.setMinWidth(120);
    }

    private void initColumnValueFactories() {
        tableColumnLessonNo.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(cellData.getValue().getLesson().getNumber()));
        tableColumnLessonTime
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLesson().getTime()));
        tableColumnLessonTypeName.setCellValueFactory(cellData -> cellData.getValue().getLessonType().nameProperty());


        tableColumnSubjectName.setCellValueFactory(cellData -> cellData.getValue().getSubject().nameProperty());
        tableColumnTeacherPIP.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getTeacher().getFullPIP()));
        tableColumnGroupName
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGroup().getName()));
        tableColumnClassroom.setCellValueFactory(cellData -> cellData.getValue().getClassroom().nameProperty());


        tableColumnStudentID.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getStudent().getId())));
        tableColumnStudentPIP.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getStudent().getFullPIP())));
        //tableColumnAttendanceState.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAttendanceState().getName())));

        // ==== AttendanceState (COMBO BOX) ===
        tableColumnAttendanceState.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Attendance, ComboBoxItem>, ObservableValue<ComboBoxItem>>() {

                    @Override
                    public ObservableValue<ComboBoxItem> call(TableColumn.CellDataFeatures<Attendance, ComboBoxItem> param) {
                        Attendance attendance = param.getValue();

                        ComboBoxItem comboBoxItem = attendanceStateDao.getComboBoxItem(attendance.getAttendanceState());

                        return new SimpleObjectProperty<ComboBoxItem>(comboBoxItem);
                    }
                });

    }

    private void initTabPane() {
        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabTable, tabView);
        tabPane.getSelectionModel().select(tabTable);
    }

    private void initHBoxes(boolean isView) {
        //hBoxEdit.setVisible(isView);
        if (mainApp != null) {
            hBoxEdit.setVisible(isView && mainApp.getLoginUser().getUserType().getId() != 3);
        } else {
            hBoxEdit.setVisible(isView);
        }
        hBoxOkCancel.setVisible(!isView);
        hBoxFilter.setVisible(isView);
        hBoxNavigation.setVisible(isView);
    }

    private void initButtonsToolTip() {
        buttonCopyAllDateCurriculum.setTooltip(new Tooltip("Скопіювати разклаз за заданими параметрами"));

        buttonNew.setTooltip(new Tooltip("Додати запис"));
        buttonEdit.setTooltip(new Tooltip("Редагувати запис"));
        buttonDelete.setTooltip(new Tooltip("Вилучити запис"));

        buttonCopy.setTooltip(new Tooltip("Копіювати запис"));
        buttonFilter.setTooltip(new Tooltip("Відфільтрувати записи"));

        buttonOk.setTooltip(new Tooltip("Підтвердити зміни"));
        buttonCancel.setTooltip(new Tooltip("Відмінити зміни"));

        buttonFirst.setTooltip(new Tooltip("Перший запис"));
        buttonPrior.setTooltip(new Tooltip("Попередній запис"));
        buttonNext.setTooltip(new Tooltip("Наступний запис"));
        buttonLast.setTooltip(new Tooltip("Останній запис"));
        buttonRefresh.setTooltip(new Tooltip("Оновити дані"));

        menuButtonReports.setTooltip(new Tooltip("Звіти"));
    }

    private void initButtonsIcons() {
        buttonCopyAllDateCurriculum.setGraphic(new ImageView(ImageResources.getButtonCopy()));

        buttonNew.setGraphic(new ImageView(ImageResources.getButtonPlus()));
        buttonEdit.setGraphic(new ImageView(ImageResources.getButtonEdit()));
        buttonDelete.setGraphic(new ImageView(ImageResources.getButtonDelete()));

        buttonCopy.setGraphic(new ImageView(ImageResources.getButtonCopy()));

        buttonFilter.setGraphic(new ImageView(ImageResources.getButtonFilter()));

        buttonOk.setGraphic(new ImageView(ImageResources.getButtonOk()));
        buttonCancel.setGraphic(new ImageView(ImageResources.getButtonCancel()));

        buttonFirst.setGraphic(new ImageView(ImageResources.getButtonFirst()));
        buttonPrior.setGraphic(new ImageView(ImageResources.getButtonPrior()));
        buttonNext.setGraphic(new ImageView(ImageResources.getButtonNext()));
        buttonLast.setGraphic(new ImageView(ImageResources.getButtonLast()));
        buttonRefresh.setGraphic(new ImageView(ImageResources.getButtonRefresh()));

        menuButtonReports.setGraphic(new ImageView(ImageResources.getReportIcon()));
        menuItemExcel.setGraphic(new ImageView(ImageResources.getXlsx16Icon()));
        menuItemJasper.setGraphic(new ImageView(ImageResources.getReportsIcon()));

    }

    public void onButtonNew(ActionEvent actionEvent) {
        if (datePickerSelectedDate.getValue() != null) {
            Curriculum curriculum = new Curriculum();
            curriculum.setDate(datePickerSelectedDate.getValue());
            if (mainApp.showEditCurriculumFormDialog(curriculum, false)) {
                try {
                    curriculumDao.insert(curriculum);
                    fillCurriculumDataOnDate(datePickerSelectedDate.getValue());
                    tabPane.getSelectionModel().select(tabTable);
                } catch (SQLException e) {
                    //e.printStackTrace();
                    Dialogs.showMessage(Alert.AlertType.ERROR,
                                        new DialogText("Помилка редагування даних", "Неможливо додати новий запис",
                                                       "Вкажіть вірні дані"), null);
                }
            }
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка редагування даних", "Дата не вибрана",
                                                                      "Будь ласка виберіть дату в календарі"), null);
            datePickerSelectedDate.requestFocus();
        }
    }

    public void onButtonEdit(ActionEvent actionEvent) {
        int selectedIndex = tableViewCurriculumData.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Curriculum curriculum = tableViewCurriculumData.getItems().get(selectedIndex);
            //logger.info(curriculum);

            if (mainApp.showEditCurriculumFormDialog(curriculum, true)) {
                try {
                    curriculumDao.update(curriculum);
                    fillCurriculumDataOnDate(datePickerSelectedDate.getValue());
                    tabPane.getSelectionModel().select(tabTable);

                } catch (SQLException e) {
                    //e.printStackTrace();
                    Dialogs.showMessage(Alert.AlertType.ERROR,
                                        new DialogText("Помилка редагування даних", "Неможливо додати новий запис",
                                                       "Вкажіть вірні дані"), null);
                }
            }

            isEdited = true;
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка редагування даних", "Запис не вибраний",
                                                                      "Будь ласка виберіть запис в таблиці"), null);
        }
    }


    public void onButtonCopy(ActionEvent actionEvent) {
        int selectedIndex = tableViewCurriculumData.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            if (datePickerSelectedDate.getValue() != null) {
                Curriculum curriculum = new Curriculum();
                curriculum.setDate(datePickerSelectedDate.getValue());

                Curriculum selectedCurriculum = tableViewCurriculumData.getItems().get(selectedIndex);
                curriculum.setLesson(selectedCurriculum.getLesson());
                curriculum.setLessonType(selectedCurriculum.getLessonType());
                curriculum.setClassroom(selectedCurriculum.getClassroom());
                curriculum.setSubject(selectedCurriculum.getSubject());
                curriculum.setTeacher(selectedCurriculum.getTeacher());
                curriculum.setGroup(selectedCurriculum.getGroup());

                if (mainApp.showEditCurriculumFormDialog(curriculum, false)) {
                    try {
                        curriculumDao.insert(curriculum);
                        fillCurriculumDataOnDate(datePickerSelectedDate.getValue());
                        tabPane.getSelectionModel().select(tabTable);
                    } catch (SQLException e) {
                        //e.printStackTrace();
                        Dialogs.showMessage(Alert.AlertType.ERROR,
                                            new DialogText("Помилка редагування даних", "Неможливо додати новий запис",
                                                           "Вкажіть вірні дані"), null);
                    }
                }
            } else {
                Dialogs.showMessage(Alert.AlertType.ERROR,
                                    new DialogText("Помилка редагування даних", "Дата не вибрана",
                                                   "Будь ласка виберіть дату в календарі"), null);
                datePickerSelectedDate.requestFocus();
            }
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка редагування даних", "Запис не вибраний",
                                                                      "Будь ласка виберіть запис в таблиці"), null);
        }
    }

    public void onButtonDelete(ActionEvent actionEvent) {
        int selectedIndex = tableViewCurriculumData.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Curriculum curriculum = tableViewCurriculumData.getItems().get(selectedIndex);
            if (Dialogs.showConfirmDialog(new DialogText("Підтвердження дії", "Бажаєте вілучити запис",
                                                         "Ви дійсно бажаєте вилучити '" +
                                                         curriculum.getLesson().getNumber() + " " +
                                                         curriculum.getSubject().getName() + "'"), logger)) {
                try {
                    if (curriculumDao.deleteById(curriculum.getId())) {
                        tableViewCurriculumData.getItems().remove(selectedIndex);
                    }
                } catch (SQLException e) {
                    Dialogs.showErrorDialog(e, new DialogText("Помилка вилучення даних", "", "Неможливо вилучити '" +
                                                                                             curriculum.getLesson()
                                                                                                     .getNumber() +
                                                                                             " " +
                                                                                             curriculum.getSubject()
                                                                                                     .getName() + "'"),
                                            logger);
                }
            }
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка вилучення даних", "Запис не вибраний",
                                                                      "Будь ласка виберіть запис в таблиці"), null);
        }
    }

    public void onButtonOk(ActionEvent actionEvent) {
        //initTabPane();
        //initHBoxes(true);
        //boolean isInsert = (textFEditId.getText() == null || textFEditId.getText().equals("")) ? true : false;
        if (isEdited) {
            //            if (isInputValid()) {
            //                LessonType LessonType = new LessonType();
            //                LessonType.setName(textFEditFullName.getText());
            //                LessonType.setShortName(textFEditShortName.getText());
            //
            //                initTabPane();
            //                initHBoxes(true);
            //                isEdited = false;
            //            }
        } else {
            // for filter
            filteredData = new FilteredList<>(curriculumObservableList, p -> true);

            if (!hasFilterPattern()) {
                filteredData.setPredicate(curriculum -> true);
            } else {
                filteredData.setPredicate(curriculum -> compareForFilter(curriculum));
            }

            SortedList<Curriculum> sortedData = new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(tableViewCurriculumData.comparatorProperty());

            tableViewCurriculumData.setItems(sortedData);

            isFilter = false;

            initTabPane();
            initHBoxes(true);
        }

    }

    private boolean hasFilterPattern() {
        if (textFFilterLesson.getText() != null && textFFilterLesson.getText().length() > 0) {
            return true;
        }
        if (textFFilterLessonType.getText() != null && textFFilterLessonType.getText().length() > 0) {
            return true;
        }
        if (textFFilterTime.getText() != null && textFFilterTime.getText().length() > 0) {
            return true;
        }
        if (textFFilterSubject.getText() != null && textFFilterSubject.getText().length() > 0) {
            return true;
        }
        if (textFFilterGroup.getText() != null && textFFilterGroup.getText().length() > 0) {
            return true;
        }
        if (textFFilterClassroom.getText() != null && textFFilterClassroom.getText().length() > 0) {
            return true;
        }
        if (textFFilterTeacher.getText() != null && textFFilterTeacher.getText().length() > 0) {
            return true;
        }

        return false;
    }

    private boolean compareForFilter(Curriculum curriculum) {
        //logger.info(student);
        if (textFFilterLesson.getText() != null && textFFilterLesson.getText().length() > 0) {
            if (!String.valueOf(curriculum.getLesson().getNumber()).toUpperCase()
                    .contains(textFFilterLesson.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterLessonType.getText() != null && textFFilterLessonType.getText().length() > 0) {
            if (!curriculum.getLessonType().getName().toUpperCase()
                    .contains(textFFilterLessonType.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterTime.getText() != null && textFFilterTime.getText().length() > 0) {
            if (!curriculum.getLesson().getTime().toUpperCase().contains(textFFilterTime.getText().toUpperCase())) {
                return false;
            }
        }

        if (textFFilterSubject.getText() != null && textFFilterSubject.getText().length() > 0) {
            if (!curriculum.getSubject().getName().toUpperCase().contains(textFFilterSubject.getText().toUpperCase())) {
                return false;
            }
        }

        if (textFFilterGroup.getText() != null && textFFilterGroup.getText().length() > 0) {
            if (!curriculum.getGroup().getName().toUpperCase().contains(textFFilterGroup.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterClassroom.getText() != null && textFFilterClassroom.getText().length() > 0) {
            if (!curriculum.getClassroom().getName().toUpperCase()
                    .contains(textFFilterClassroom.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterTeacher.getText() != null && textFFilterTeacher.getText().length() > 0) {
            if (!curriculum.getTeacher().getFullPIP().toUpperCase()
                    .contains(textFFilterTeacher.getText().toUpperCase())) {
                return false;
            }
        }

        return true;
    }

    public void onButtonCancel(ActionEvent actionEvent) {
        initTabPane();
        initHBoxes(true);
        if (isFilter && filteredData != null) {
            filteredData.setPredicate(s -> true);
        }
        isEdited = false;
        isFilter = false;
    }

    public void onButtonFilter(ActionEvent actionEvent) {

        isEdited = false;
        isFilter = true;
        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabFilter);
        initHBoxes(false);
    }

    public void onButtonFirst(ActionEvent actionEvent) {
        tableViewCurriculumData.getSelectionModel().selectFirst();
        tableViewCurriculumData.scrollTo(tableViewCurriculumData.getSelectionModel().getSelectedIndex());
    }

    public void onButtonPrior(ActionEvent actionEvent) {
        tableViewCurriculumData.getSelectionModel().selectPrevious();
        tableViewCurriculumData.scrollTo(tableViewCurriculumData.getSelectionModel().getSelectedIndex());
    }

    public void onButtonNext(ActionEvent actionEvent) {
        tableViewCurriculumData.getSelectionModel().selectNext();
        tableViewCurriculumData.scrollTo(tableViewCurriculumData.getSelectionModel().getSelectedIndex());
    }

    public void onButtonLast(ActionEvent actionEvent) {
        tableViewCurriculumData.getSelectionModel().selectLast();
        tableViewCurriculumData.scrollTo(tableViewCurriculumData.getSelectionModel().getSelectedIndex());
    }

    public void onButtonRefresh(ActionEvent actionEvent) {
        tableViewCurriculumData.refresh();
    }

    private void showCurriculumDetails(Curriculum curriculum) {

        if (curriculum != null) {
            textFViewLesson.setText(String.valueOf(curriculum.getLesson().getNumber()));
            textFViewTime.setText(curriculum.getLesson().getTime());
            textFViewLessonType.setText(curriculum.getLessonType().getName());
            textFViewClassroom.setText(curriculum.getClassroom().getName());
            textFViewSubject.setText(curriculum.getSubject().getName());
            textFViewGroup.setText(curriculum.getGroup().getName());
            textFViewTeacher.setText(curriculum.getTeacher().getFullPIP());

            try {
                fillStudentDataOnCurriculum(curriculum);
            } catch (SQLException e) {
                Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка отримання даних",
                                                                          "Помилка отримання даних про відвідувачів",
                                                                          e.getMessage()), null);
            }
        } else {
            refreshAttendanceTableData();

            textFViewLesson.setText("");
            textFViewTime.setText("");
            textFViewLessonType.setText("");
            textFViewClassroom.setText("");
            textFViewSubject.setText("");
            textFViewGroup.setText("");
            textFViewTeacher.setText("");
        }
    }

    private void fillCurriculumDataOnDate(LocalDate localDate) throws SQLException {
        refreshCurriculumTableData();
        //logger.warn("GET " + localDate);
        if (localDate == null) {

        } else {
            if (curriculumDao != null) {
                if (mainApp.getLoginUser().getUserType().getId() == 3) {
                    if (mainApp.getLoginUser().getPerson() != null) {
                        curriculumObservableList.addAll(curriculumDao.findAllDataOnDate(localDate,
                                                                                        mainApp.getLoginUser()
                                                                                                .getPerson().getId()));
                    }
                } else {
                    curriculumObservableList.addAll(curriculumDao.findAllDataOnDate(localDate));
                }
                /*if (curriculumObservableList != null && curriculumObservableList.size() > 0) {
                    filteredData.setPredicate(curriculum -> true);*/

                tableViewCurriculumData.getItems().addAll(curriculumObservableList);

            }
        }
    }

    private void fillStudentDataOnCurriculum(Curriculum curriculum) throws SQLException {

        refreshAttendanceTableData();

        if (curriculum == null) {

        } else {
            if (attendanceDao != null) {
                attendanceObservableList.addAll(attendanceDao.findAllDataByCurriculum(curriculum));
                tableViewAttendanceData.getItems().addAll(attendanceObservableList);
            }
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";
        //        if (textFEditFullName.getText() == null || textFEditFullName.getText().length() == 0) {
        //            errorMessage += "Невірна назва!\n";
        //        }
        //        if (textFEditShortName.getText() == null || textFEditShortName.getText().length() == 0) {
        //            errorMessage += "Невірна скорочена назва!\n";
        //        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR,
                                new DialogText("Помилка заповнення", "Будь ласка заповнить помилкові поля",
                                               errorMessage), null);
            //alert.initOwner(dialogStage);
            return false;
        }
    }

    public void onButtonCopyAllDateCurriculum(ActionEvent actionEvent) {
        mainApp.showCopyCurriculumParamsDialog(datePickerSelectedDate.getValue());
    }

    public void onMenuItemExcel(ActionEvent actionEvent) {
        ExportData exportData = new ExportData();

        Curriculum newCurriculum = new Curriculum();

        exportData.setFieldList(newCurriculum.getFieldList());
        exportData.setExportFileType(ExportFileType.XLSX);

        List<Object[]> objects = new ArrayList<>();

        ObservableList<Curriculum> curriculums;
        if (filteredData != null) {
            curriculums = filteredData;
        } else {
            curriculums = curriculumObservableList;
        }

        for (Curriculum curriculum : curriculums) {
            objects.add(curriculum.getObjects());
        }

        exportData.setTableData(objects);

        try {
            ExportServices.exportData(dialogStage, exportData);
            if (exportData.getFile() != null && exportData.getFile().exists()) {
                Dialogs.showMessage(Alert.AlertType.INFORMATION, new DialogText("Експорт даних", "Файл '" +
                                                                                                 exportData.getFile()
                                                                                                         .getName() +
                                                                                                 "' збережено успішно",
                                                                                ""), logger);
            }
        } catch (IOException e) {
            //e.printStackTrace();
            Dialogs.showErrorDialog(e, new DialogText("Експорт даних", "Файл не збережено", ""), logger);
        }
    }

    public void onMenuItemJasper(ActionEvent actionEvent) {
        if (mainApp != null) {
            mainApp.showPeriodAndGroupDialog(datePickerSelectedDate.getValue(), datePickerSelectedDate.getValue());
        }

/*        //MainApp.class.getResourceAsStream("/jasperReports/GroupsStudents.jrxml");
        PreparedStatement preparedStatement = null;

        try {

            // First, compile jrxml file.
            JasperReport jasperReport = JasperCompileManager
                    .compileReport(MainApp.class.getResourceAsStream("/jasperReports/Curriculum.jrxml"));
            // Parameters for report
            Map<String, Object> parameters = new HashMap<String, Object>();
//            parameters.put("paramDateFrom", new java.sql.Date(
//                    DateUtil.getDateFromLocalDate(datePickerSelectedDate.getValue()).getTime()));
//            parameters.put("paramDateTill",
//                    DateUtil.getDateFromLocalDate(datePickerSelectedDate.getValue()).getTime());
            logger.info("Date from: " + (datePickerSelectedDate.getValue()).format(DateUtil.getDateTimeFormatter()));
            parameters.put("p_From",
                    (datePickerSelectedDate.getValue()).format(DateUtil.getDateTimeFormatter()));
            logger.info("Date till: " + (datePickerSelectedDate.getValue()).format(DateUtil.getDateTimeFormatter()));
            parameters.put("p_Till",
                           (datePickerSelectedDate.getValue()).format(DateUtil.getDateTimeFormatter()));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, mainApp.getConnection());

            JasperViewer jasperViewer = new JasperViewer(jasperPrint);

            jasperViewer.viewReport(jasperPrint, false);
        } catch (JRException e) {
            Dialogs.showErrorDialog(e, new DialogText("Експорт даних", "Файл не збережено", e.getMessage()), logger);
        }
        */
    }
}
