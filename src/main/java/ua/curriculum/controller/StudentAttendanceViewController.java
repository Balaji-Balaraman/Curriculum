package ua.curriculum.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.curriculum.MainApp;
import ua.curriculum.dao.*;
import ua.curriculum.export.ExportData;
import ua.curriculum.export.ExportFileType;
import ua.curriculum.export.ExportServices;
import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.fx.DialogText;
import ua.curriculum.fx.Dialogs;
import ua.curriculum.fx.ImageResources;
import ua.curriculum.model.Attendance;
import ua.curriculum.model.StudentAttendance;
import ua.curriculum.utils.DateUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class StudentAttendanceViewController {
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
    private Button buttonFilter;
    @FXML
    private Button buttonShowGroups;
    @FXML
    private Button buttonShowStudents;
    @FXML
    private Button buttonShowSubjects;

    @FXML
    private TableView<StudentAttendance> tableViewStudentAttendanceData;

    @FXML
    private TableColumn<StudentAttendance, String> tableColumnGroupName;
    @FXML
    private TableColumn<StudentAttendance, String> tableColumnStudentPIP;
    @FXML
    private TableColumn<StudentAttendance, String> tableColumnSubjectName;
    @FXML
    private TableColumn<StudentAttendance, Integer> tableColumnTotalLessons;
    @FXML
    private TableColumn<StudentAttendance, Integer> tableColumnTotalPresence;
    @FXML
    private TableColumn<StudentAttendance, Integer> tableColumnTotalAbsences;
    @FXML
    private TableColumn<StudentAttendance, Integer> tableColumnTotalByIllness;

    @FXML
    private TextField textFViewSubject;
    @FXML
    private TextField textFViewGroup;
    @FXML
    private TextField textFViewStudent;

    @FXML
    private TableView<Attendance> tableViewAttendanceData;
    @FXML
    private TableColumn<Attendance, LocalDate> tableColumnDate;
    @FXML
    private TableColumn<Attendance, Integer> tableColumnLessonNo;
    @FXML
    private TableColumn<Attendance, String> tableColumnLessonTime;
    @FXML
    private TableColumn<Attendance, String> tableColumnLessonType;
    @FXML
    private TableColumn<Attendance, String> tableColumnClassRoom;
    @FXML
    private TableColumn<Attendance, String> tableColumnAttendanceState;


    @FXML
    private DatePicker datePickerFilterPeriodFrom;
    @FXML
    private DatePicker datePickerFilterPeriodTill;

    @FXML
    private ComboBox<ComboBoxItem> comboBoxGroup = new ComboBox<>();
    @FXML
    private ComboBox<ComboBoxItem> comboBoxStudent = new ComboBox<>();
    @FXML
    private ComboBox<ComboBoxItem> comboBoxSubject = new ComboBox<>();

    @FXML
    private MenuButton menuButtonReports;
    @FXML
    private MenuItem menuItemExcel;
    @FXML
    private MenuItem menuItemJasper;

    private ObservableList<ComboBoxItem> observableListGroups = FXCollections.observableArrayList();
    private ObservableList<ComboBoxItem> observableListStudents = FXCollections.observableArrayList();
    private ObservableList<ComboBoxItem> observableListSubjects = FXCollections.observableArrayList();

    private GroupDao groupDao;
    private StudentDao studentDao;
    private SubjectDao subjectDao;
    private StudentAttendanceDao studentAttendanceDao;
    private AttendanceDao attendanceDao;


    private ObservableList<StudentAttendance> studentAttendanceObservableList = FXCollections.observableArrayList();

    private ObservableList<Attendance> attendanceObservableList = FXCollections.observableArrayList();

    private MainApp mainApp;

    private Stage dialogStage;

    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        //initHBoxes(true);

        if (mainApp.getLoginUser().getUserType().getId() == 3) {
            comboBoxStudent.setDisable(true);
        }

        groupDao = new GroupDao(mainApp.getConnection());
        studentDao = new StudentDao(mainApp.getConnection());
        subjectDao = new SubjectDao(mainApp.getConnection());
        studentAttendanceDao = new StudentAttendanceDao(mainApp.getConnection());
        attendanceDao = new AttendanceDao(mainApp.getConnection());

        datePickerFilterPeriodFrom.setValue(DateUtil.getLocalDate(new Date()));
        datePickerFilterPeriodTill.setValue(DateUtil.getLocalDate(new Date()));

        fillStudentsComboBox(null);
        fillSubjectsComboBox(null);
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

        tableViewStudentAttendanceData.setPlaceholder(new Label("Дані відсутні"));
        tableViewAttendanceData.setPlaceholder(new Label("Дані відсутні"));

        initButtonsIcons();
        initButtonsToolTip();

        //initTabPane();

        initColumnValueFactories();

        showAttendanceDetails(null);

        initComponentListeners();
    }

    private void initComponentListeners() {
        tableViewStudentAttendanceData.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showAttendanceDetails(newValue));

        datePickerFilterPeriodFrom.valueProperty().addListener((ov, oldValue, newValue) -> {

            //logger.warn("New value: " + newValue);
            try {
                fillGroupsComboBoxOnDate(newValue, datePickerFilterPeriodTill.getValue());
            } catch (SQLException e) {
                //e.printStackTrace();
                Dialogs.showErrorDialog(e, new DialogText("Помилка отримання даних", "",
                                                          "Неможливо отримати данні з таблиці 'Групи'"), logger);
            }
        });

        datePickerFilterPeriodTill.valueProperty().addListener((ov, oldValue, newValue) -> {

            //logger.warn("New value: " + newValue);
            try {
                fillGroupsComboBoxOnDate(datePickerFilterPeriodFrom.getValue(), newValue);
            } catch (SQLException e) {
                //e.printStackTrace();
                Dialogs.showErrorDialog(e, new DialogText("Помилка отримання даних", "",
                                                          "Неможливо отримати данні з таблиці 'Групи'"), logger);
            }
        });

        comboBoxGroup.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            fillStudentsComboBox(newValue);
        });
    }

    private void fillStudentsComboBox(ComboBoxItem newValue) {
        observableListStudents.clear();
        observableListStudents.add(new ComboBoxItem("0", ""));
        if (studentDao != null) {
            try {
                if (mainApp.getLoginUser().getUserType().getId() == 3) {
                    observableListStudents
                            .addAll(studentDao.findStudentsByGroupId(mainApp.getLoginUser().getPerson().getId()));
                    comboBoxStudent.getSelectionModel()
                            .select(studentDao.findComboBoxDataById(mainApp.getLoginUser().getPerson().getId()));
                } else {

                    if (newValue == null || newValue.getObjectId().equals("0")) {
                        observableListStudents.addAll(studentDao.findAllComboBoxData());
                    } else {
                        observableListStudents
                                .addAll(studentDao.findStudentsByGroupId(Integer.valueOf(newValue.getObjectId())));
                    }
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
                //e.printStackTrace();
            }
        }
        comboBoxStudent.setItems(observableListStudents);
    }

    private void fillSubjectsComboBox(ComboBoxItem newValue) {
        observableListSubjects.clear();
        observableListSubjects.add(new ComboBoxItem("0", ""));
        if (subjectDao != null) {
            try {
                observableListSubjects.addAll(subjectDao.findAllComboBoxData());
            } catch (SQLException e) {
                logger.error(e.getMessage());
                //e.printStackTrace();
            }
        }
        comboBoxSubject.setItems(observableListSubjects);
    }

    private void initColumnValueFactories() {
        tableColumnGroupName
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGroup().getName()));
        tableColumnStudentPIP.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getStudent().getFullPIP()));
        tableColumnSubjectName
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSubject().getName()));
        //tableColumnSubjectName.setCellValueFactory(cellData -> cellData.getValue().getSubject().nameProperty());
        tableColumnTotalLessons
                .setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTotalLessons()));

        tableColumnTotalPresence
                .setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTotalPresence()));
        tableColumnTotalAbsences
                .setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTotalAbsences()));
        tableColumnTotalByIllness
                .setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTotalByIllness()));

        //children table Attendance

        tableColumnDate.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(cellData.getValue().getCurriculum().getDate()));
        tableColumnLessonNo.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(cellData.getValue().getCurriculum().getLesson().getNumber()));
        tableColumnLessonTime.setCellValueFactory(cellData -> new SimpleStringProperty(
                String.valueOf(cellData.getValue().getCurriculum().getLesson().getTime())));
        tableColumnLessonType.setCellValueFactory(cellData -> new SimpleStringProperty(
                String.valueOf(cellData.getValue().getCurriculum().getLessonType().getName())));
        tableColumnClassRoom.setCellValueFactory(cellData -> new SimpleStringProperty(
                String.valueOf(cellData.getValue().getCurriculum().getClassroom().getName())));
        tableColumnAttendanceState.setCellValueFactory(cellData -> new SimpleStringProperty(
                String.valueOf(cellData.getValue().getAttendanceState().getName())));

    }
/*
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
*/

    private void initButtonsToolTip() {
        buttonFilter.setTooltip(new Tooltip("Відфільтрувати записи"));

        buttonFirst.setTooltip(new Tooltip("Перший запис"));
        buttonPrior.setTooltip(new Tooltip("Попередній запис"));
        buttonNext.setTooltip(new Tooltip("Наступний запис"));
        buttonLast.setTooltip(new Tooltip("Останній запис"));
        buttonRefresh.setTooltip(new Tooltip("Оновити дані"));

        buttonShowGroups.setTooltip(new Tooltip("Показати довідник груп"));
        buttonShowStudents.setTooltip(new Tooltip("Показати довідник студентів"));
        buttonShowSubjects.setTooltip(new Tooltip("Показати довідник предметів"));

        menuButtonReports.setTooltip(new Tooltip("Показати звіти"));
    }

    private void initButtonsIcons() {

        buttonFilter.setGraphic(new ImageView(ImageResources.getButtonFilter()));

        buttonFirst.setGraphic(new ImageView(ImageResources.getButtonFirst()));
        buttonPrior.setGraphic(new ImageView(ImageResources.getButtonPrior()));
        buttonNext.setGraphic(new ImageView(ImageResources.getButtonNext()));
        buttonLast.setGraphic(new ImageView(ImageResources.getButtonLast()));
        buttonRefresh.setGraphic(new ImageView(ImageResources.getButtonRefresh()));

        buttonShowGroups.setGraphic(new ImageView(ImageResources.getButtonView()));
        buttonShowStudents.setGraphic(new ImageView(ImageResources.getButtonView()));
        buttonShowSubjects.setGraphic(new ImageView(ImageResources.getButtonView()));

        menuButtonReports.setGraphic(new ImageView(ImageResources.getReportIcon()));
        menuItemExcel.setGraphic(new ImageView(ImageResources.getXlsx16Icon()));
        menuItemJasper.setGraphic(new ImageView(ImageResources.getReportsIcon()));

    }


    public void onButtonFilter(ActionEvent actionEvent) {
        if (isInputParamsValid()) {
            try {
                studentAttendanceObservableList.clear();
                Integer groupId =
                        (comboBoxGroup.getValue() != null && !comboBoxGroup.getValue().getObjectId().equals("0")) ?
                                Integer.valueOf(comboBoxGroup.getValue().getObjectId()) : null;
                Integer studentId =
                        (comboBoxStudent.getValue() != null && !comboBoxStudent.getValue().getObjectId().equals("0")) ?
                                Integer.valueOf(comboBoxStudent.getValue().getObjectId()) : null;
                Integer subjectId =
                        (comboBoxSubject.getValue() != null && !comboBoxSubject.getValue().getObjectId().equals("0")) ?
                                Integer.valueOf(comboBoxSubject.getValue().getObjectId()) : null;

                studentAttendanceObservableList.addAll(studentAttendanceDao.findAllDataOnPeriod(
                        datePickerFilterPeriodFrom.getValue(), datePickerFilterPeriodTill.getValue(), groupId,
                        studentId, subjectId));

                tableViewStudentAttendanceData.setItems(studentAttendanceObservableList);

            } catch (SQLException e) {
                Dialogs.showErrorDialog(e, new DialogText("Помилка отримання даних",
                                                          "Неможливо отримати дані за " + "параметрами",
                                                          "Дата З= " + datePickerFilterPeriodFrom.getValue() + "; " +
                                                          "Дата ПО= " + datePickerFilterPeriodTill.getValue() + " " +
                                                          "; ... "), logger);
            }
        }

    }

    public void onButtonFirst(ActionEvent actionEvent) {
        tableViewStudentAttendanceData.getSelectionModel().selectFirst();
        tableViewStudentAttendanceData.scrollTo(tableViewStudentAttendanceData.getSelectionModel().getSelectedIndex());
    }

    public void onButtonPrior(ActionEvent actionEvent) {
        tableViewStudentAttendanceData.getSelectionModel().selectPrevious();
        tableViewStudentAttendanceData.scrollTo(tableViewStudentAttendanceData.getSelectionModel().getSelectedIndex());
    }

    public void onButtonNext(ActionEvent actionEvent) {
        tableViewStudentAttendanceData.getSelectionModel().selectNext();
        tableViewStudentAttendanceData.scrollTo(tableViewStudentAttendanceData.getSelectionModel().getSelectedIndex());
    }

    public void onButtonLast(ActionEvent actionEvent) {
        tableViewStudentAttendanceData.getSelectionModel().selectLast();
        tableViewStudentAttendanceData.scrollTo(tableViewStudentAttendanceData.getSelectionModel().getSelectedIndex());
    }

    public void onButtonRefresh(ActionEvent actionEvent) {
        tableViewStudentAttendanceData.refresh();
    }

    private void showAttendanceDetails(StudentAttendance studentAttendance) {

        if (studentAttendance != null) {
            textFViewSubject.setText(studentAttendance.getSubject().getName());
            textFViewGroup.setText(studentAttendance.getGroup().getName());
            textFViewStudent.setText(studentAttendance.getStudent().getFullPIP());

            try {
                fillStudentDataOnCurriculum(datePickerFilterPeriodFrom.getValue(),
                                            datePickerFilterPeriodTill.getValue(), studentAttendance);
            } catch (SQLException e) {
                e.printStackTrace();
                Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка отримання даних",
                                                                          "Помилка отримання даних про відвідування",
                                                                          e.getMessage()), logger);
            }
        } else {
            refreshAttendanceTableData();
            textFViewSubject.setText("");
            textFViewGroup.setText("");
            textFViewStudent.setText("");
        }
    }

    private void fillStudentDataOnCurriculum(LocalDate localDateFrom, LocalDate localDateTill, StudentAttendance studentAttendance)
            throws SQLException {

        refreshAttendanceTableData();

        if (studentAttendance == null) {

        } else {
            if (attendanceDao != null) {
                attendanceObservableList.addAll(attendanceDao
                                                        .findAllDataByStudentAttendance(localDateFrom, localDateTill,
                                                                                        studentAttendance));
                tableViewAttendanceData.getItems().addAll(attendanceObservableList);
            }
        }
    }

    private void fillGroupsComboBoxOnDate(LocalDate localDateFrom, LocalDate localDateTill) throws SQLException {
        observableListGroups.clear();
        observableListGroups.add(new ComboBoxItem("0", ""));
        if (groupDao != null) {
            if (mainApp.getLoginUser().getUserType().getId() == 3) {
                if (localDateFrom != null && localDateTill != null) {
                    observableListGroups.addAll(groupDao.findAllGroupOnDate(localDateFrom, localDateTill,
                                                                            mainApp.getLoginUser().getPerson()
                                                                                    .getId()));
                }
            } else {
                if (localDateFrom != null && localDateTill != null) {
                    observableListGroups.addAll(groupDao.findAllGroupOnDate(localDateFrom, localDateTill));
                } else if (localDateFrom == null && localDateTill != null) {
                    observableListGroups.addAll(groupDao.findAllGroupOnDate(localDateTill));
                } else if (localDateFrom != null && localDateTill == null) {
                    observableListGroups.addAll(groupDao.findAllGroupOnDate(localDateFrom));
                }
            }
        }
        comboBoxGroup.setItems(observableListGroups);
    }

    private boolean isInputParamsValid() {
        String errorMessage = "";
        if (datePickerFilterPeriodFrom.getValue() == null) {
            errorMessage += "Невірна дата З!\n";
            datePickerFilterPeriodFrom.requestFocus();
        }
        if (datePickerFilterPeriodTill.getValue() == null) {
            errorMessage += "Невірна дата ПО!\n";
            datePickerFilterPeriodTill.requestFocus();
        }

        //        if (textFEditShortName.getText() == null || textFEditShortName.getText().length() == 0) {
        //            errorMessage += "Невірна скорочена назва!\n";
        //        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR,
                                new DialogText("Помилкові параметри", "Будь ласка заповнить параметри", errorMessage),
                                null);
            //alert.initOwner(dialogStage);
            return false;
        }
    }


    public void onButtonShowSubjects(ActionEvent actionEvent) {
        ComboBoxItem item = mainApp.showSearchFormDialog(observableListSubjects);
        if (item != null) {
            comboBoxSubject.getSelectionModel().select(item);
        }
    }

    public void onButtonShowStudents(ActionEvent actionEvent) {
        ComboBoxItem item = mainApp.showSearchFormDialog(observableListStudents);
        if (item != null) {
            comboBoxStudent.getSelectionModel().select(item);
        }
    }

    public void onButtonShowGroups(ActionEvent actionEvent) {
        ComboBoxItem item = mainApp.showSearchFormDialog(observableListGroups);
        if (item != null) {
            comboBoxGroup.getSelectionModel().select(item);
        }
    }

    public void onMenuItemExcel(ActionEvent actionEvent) {
        ExportData exportData = new ExportData();
        StudentAttendance newStudentAttendance = new StudentAttendance();
        exportData.setFieldList(newStudentAttendance.getFieldList());
        exportData.setExportFileType(ExportFileType.XLSX);
        exportData.setFile(new File("Відвідуваність"));
        List<Object[]> objects = new ArrayList<>();

        for (StudentAttendance studentAttendance : studentAttendanceObservableList) {
            objects.add(studentAttendance.getObjects());
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

    public void onMenuItemJasper(ActionEvent actionEvent) throws IOException {
        //MainApp.class.getResourceAsStream("/jasperReports/GroupsStudents.jrxml");
        PreparedStatement preparedStatement = null;


        try {
            preparedStatement = mainApp.getConnection().prepareStatement("DELETE FROM temp_report");
            preparedStatement.executeUpdate();

            // First, compile jrxml file.
            JasperReport jasperReport;
            if (Dialogs.showConfirmDialog(new DialogText("Виберіть варіант", "Показати побробиці?", ""), logger)) {
                jasperReport = JasperCompileManager
                        .compileReport(MainApp.class.getResourceAsStream("/jasperReports/Attendance.jrxml"));
            } else {
                jasperReport = JasperCompileManager
                        .compileReport(MainApp.class.getResourceAsStream("/jasperReports/AttendanceTotal.jrxml"));
            }
            // Parameters for report
            Map<String, Object> parameters = new HashMap<String, Object>();


            parameters.put("p_From", (datePickerFilterPeriodFrom.getValue()).format(DateUtil.getDateTimeFormatter()));
            parameters.put("p_Till", (datePickerFilterPeriodTill.getValue()).format(DateUtil.getDateTimeFormatter()));
            if (comboBoxGroup.getValue() != null && !comboBoxGroup.getValue().getObjectId().equals("0")) {
                parameters.put("p_Group", Integer.valueOf(comboBoxGroup.getValue().getObjectId()));
            }
            if (comboBoxSubject.getValue() != null && !comboBoxSubject.getValue().getObjectId().equals("0")) {
                parameters.put("p_Subject", Integer.valueOf(comboBoxSubject.getValue().getObjectId()));
            }
            if (comboBoxStudent.getValue() != null && !comboBoxStudent.getValue().getObjectId().equals("0")) {
                parameters.put("p_Student", Integer.valueOf(comboBoxStudent.getValue().getObjectId()));
            }


            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, mainApp.getConnection());

            JasperViewer jasperViewer = new JasperViewer(jasperPrint);

            jasperViewer.setFont(new Font("AnGo_Times_New_Roman", 0, 0));

            jasperViewer.viewReport(jasperPrint, false);
            //jasperViewer.viewReport(jasperPrint);
            //jasperViewer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


        } catch (SQLException | JRException e) {
            Dialogs.showErrorDialog(e, new DialogText("Експорт даних", "Файл не збережено", e.getMessage()), logger);
        }
    }
}
