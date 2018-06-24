package ua.curriculum.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.curriculum.MainApp;
import ua.curriculum.dao.StudentDao;
import ua.curriculum.export.ExportData;
import ua.curriculum.export.ExportFileType;
import ua.curriculum.export.ExportServices;
import ua.curriculum.fx.DialogText;
import ua.curriculum.fx.Dialogs;
import ua.curriculum.fx.ImageResources;
import ua.curriculum.model.Student;
import ua.curriculum.utils.DateUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentsViewController{
    private Logger logger = LogManager.getLogger(this.getClass());

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tabTable;
    @FXML
    private Tab tabView;
    @FXML
    private Tab tabEdit;
    @FXML
    private Tab tabFilter;

    @FXML
    private HBox hBoxEdit;
    @FXML
    private HBox hBoxOkCancel;
    @FXML
    private HBox hBoxFilter;
    @FXML
    private HBox hBoxNavigation;

    @FXML
    private Button buttonOk;
    @FXML
    private Button buttonCancel;
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
    private Button buttonFilter;

    @FXML
    private TableView<Student> tableViewStudent;

    @FXML
    private TableColumn<Student, Integer> tableColumnId;
    @FXML
    private TableColumn<Student, String> tableColumnLastName;
    @FXML
    private TableColumn<Student, String> tableColumnFirstName;
    @FXML
    private TableColumn<Student, String> tableColumnMiddleName;
    @FXML
    private TableColumn<Student, LocalDate> tableColumnBirthday;
    @FXML
    private TableColumn<Student, String> tableColumnAddress;

    @FXML
    private TextField textFViewId;
    @FXML
    private TextField textFViewLastName;
    @FXML
    private TextField textFViewFirstName;
    @FXML
    private TextField textFViewMiddleName;
    @FXML
    private TextField textFViewBirthday;
    @FXML
    private TextField textFViewAddress;

    @FXML
    private TextField textFEditId;
    @FXML
    private TextField textFEditLastName;
    @FXML
    private TextField textFEditFirstName;
    @FXML
    private TextField textFEditMiddleName;
    @FXML
    private TextField textFEditAddress;
    @FXML
    private DatePicker dataPickerEditBirthday;

    @FXML
    private TextField textFFilterId;
    @FXML
    private TextField textFFilterLastName;
    @FXML
    private TextField textFFilterFirstName;
    @FXML
    private TextField textFFilterMiddleName;
    @FXML
    private TextField textFFilterAddress;
    @FXML
    private DatePicker dataPickerFilterBirthdayFrom;
    @FXML
    private DatePicker dataPickerFilterBirthdayTill;

    @FXML
    private MenuButton menuButtonReports;
    @FXML
    private MenuItem menuItemExcel;


    private StudentDao dao;

    private ObservableList<Student> studentObservableList = FXCollections.observableArrayList();

    private FilteredList<Student> filteredData;

    private MainApp mainApp;

    private Stage dialogStage;
    private boolean isEdited;
    private boolean isFilter;

    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        dao = new StudentDao(mainApp.getConnection());
        try {
            refreshTableData();

        } catch (SQLException e) {
            Dialogs.showErrorDialog(e, new DialogText("Помилка отримання даних", "", "Неможливо отримати данні з таблиці 'Студенти'"), logger);
        }
    }

    private void refreshTableData() throws SQLException {
        studentObservableList.clear();
        tableViewStudent.getItems().removeAll(tableViewStudent.getItems());

        studentObservableList.addAll(dao.findAllData());
        tableViewStudent.setItems(studentObservableList);
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

        initButtonsIcons();
        initButtonsToolTip();

        initHBoxes(true);

        initTabPane();


        initColumnValueFactoies();

        showStudentDetails(null);

        initComponentListeners();
    }

    private void initComponentListeners() {
        tableViewStudent.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showStudentDetails(newValue));
    }

    private void initColumnValueFactoies() {
        tableColumnId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        tableColumnLastName.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        tableColumnFirstName.setCellValueFactory(cellData -> cellData.getValue().firsNameProperty());
        tableColumnMiddleName.setCellValueFactory(cellData -> cellData.getValue().middleNameProperty());
        tableColumnBirthday.setCellValueFactory(cellData -> cellData.getValue().birthdayProperty());
        tableColumnAddress.setCellValueFactory(cellData -> cellData.getValue().addressProperty());

        tableColumnBirthday.setCellFactory(getTableCellDateCallback());
    }

    private Callback<TableColumn<Student, LocalDate>, TableCell<Student, LocalDate>> getTableCellDateCallback() {
        return column -> {
            return new TableCell<Student, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(DateUtil.format(item));
                    }
                }
            };
        };
    }

    private void initTabPane() {
        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabTable, tabView);
        tabPane.getSelectionModel().select(tabTable);
    }

    private void initHBoxes(boolean isView) {
        hBoxEdit.setVisible(isView);
        hBoxOkCancel.setVisible(!isView);
        hBoxFilter.setVisible(isView);
        hBoxNavigation.setVisible(isView);
    }

    private void initButtonsToolTip() {
        buttonNew.setTooltip(new Tooltip("Додати запис"));
        buttonEdit.setTooltip(new Tooltip("Редагувати запис"));
        buttonDelete.setTooltip(new Tooltip("Вилучити запис"));

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
        buttonNew.setGraphic(new ImageView(ImageResources.getButtonPlus()));
        buttonEdit.setGraphic(new ImageView(ImageResources.getButtonEdit()));
        buttonDelete.setGraphic(new ImageView(ImageResources.getButtonDelete()));

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
    }


    public void onButtonNew(ActionEvent actionEvent) {
        textFEditId.setText("");
        textFEditLastName.setText("");
        textFEditFirstName.setText("");
        textFEditMiddleName.setText("");
        dataPickerEditBirthday.getEditor().clear();
        textFEditAddress.setText("");

        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabEdit);
        initHBoxes(false);

        isEdited = true;

    }

    public void onButtonEdit(ActionEvent actionEvent) {
        int selectedIndex = tableViewStudent.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Student student = tableViewStudent.getItems().get(selectedIndex);
            textFEditId.setText(String.valueOf(student.getId()));
            textFEditLastName.setText(student.getLastName());
            textFEditFirstName.setText(student.getFirsName());
            textFEditMiddleName.setText(student.getMiddleName());
            dataPickerEditBirthday.setValue(student.getBirthday());
            textFEditAddress.setText(student.getAddress());

            tabPane.getTabs().removeAll(tabPane.getTabs());
            tabPane.getTabs().addAll(tabEdit);

            initHBoxes(false);

            isEdited = true;
        }else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка редагування даних", "Студент не вибраний", "Будь ласка виберіть Студента в таблиці"), null);
        }
    }

    public void onButtonDelete(ActionEvent actionEvent) {
        int selectedIndex = tableViewStudent.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Student student = tableViewStudent.getItems().get(selectedIndex);
            if (Dialogs.showConfirmDialog(new DialogText("Підтвердження дії", "Бажаєте вілучити запис", "Ви дійсно бажаєте вилучити '" + student.getLastName() + " " + student.getFirsName() + "'"), logger)) {
                //int selectedIndex = tableViewStudent.getSelectionModel().getSelectedIndex();
                //Student student = tableViewStudent.getItems().get(selectedIndex);
                try {
                    if (dao.deleteById(student.getId())) {
                        tableViewStudent.getItems().remove(selectedIndex);
                    }
                } catch (SQLException e) {
                    Dialogs.showErrorDialog(e, new DialogText("Помилка вилучення даних", "", "Неможливо вилучити '" + student.getLastName() + " " + student.getFirsName() + "'"), logger);
                }
            }
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка вилучення даних", "Студент не вибраний", "Будь ласка виберіть Студента в таблиці"), null);
        }
    }

    public void onButtonOk(ActionEvent actionEvent) {
        boolean isInsert = (textFEditId.getText() == null || textFEditId.getText().equals("")) ? true : false;
        if (isEdited) {
            if (isInputValid()) {
                Student student = new Student();
                student.setLastName(textFEditLastName.getText());
                student.setFirsName(textFEditFirstName.getText());
                student.setMiddleName(textFEditMiddleName.getText());
                student.setBirthday(dataPickerEditBirthday.getValue());
                student.setAddress(textFEditAddress.getText());

                if (isInsert) {
                    try {
                        if (dao.insert(student)) {
                            refreshTableData();
                        }
                    } catch (SQLException e) {
                        Dialogs.showErrorDialog(e, new DialogText("Помилка додавання даних", "", "Неможливо додати '" + student.getLastName() + " " + student.getFirsName() + "'"), logger);
                    }
                } else if (!isInsert) {
                    student.setId(Integer.parseInt(textFEditId.getText()));
                    try {
                        if (dao.update(student)) {
                            refreshTableData();
                        }
                    } catch (SQLException e) {
                        Dialogs.showErrorDialog(e, new DialogText("Помилка редагування даних", "", "Неможливо змінити '" + student.getLastName() + " " + student.getFirsName() + "'"), logger);
                    }

                }
                isEdited = false;

                initTabPane();
                initHBoxes(true);
            }
        } else {
            // for filter
            //logger.info("hasFilterPattern()= " + hasFilterPattern());

            filteredData = new FilteredList<>(studentObservableList, p -> true);

            if (!hasFilterPattern()) {
                filteredData.setPredicate(student -> true);
            } else {
                filteredData.setPredicate(student -> compareForFilter(student));
            }

            SortedList<Student> sortedData = new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(tableViewStudent.comparatorProperty());

            tableViewStudent.setItems(sortedData);

            isFilter = false;

            initTabPane();
            initHBoxes(true);
        }

    }

    private boolean hasFilterPattern() {
        if (textFFilterId.getText() != null && textFFilterId.getText().length() > 0) {
            return true;
        }
        if (textFFilterLastName.getText() != null && textFFilterLastName.getText().length() > 0) {
            return true;
        }
        if (textFFilterFirstName.getText() != null && textFFilterFirstName.getText().length() > 0) {
            return true;
        }
        if (textFFilterMiddleName.getText() != null && textFFilterMiddleName.getText().length() > 0) {
            return true;
        }
        if (textFFilterAddress.getText() != null && textFFilterAddress.getText().length() > 0) {
            return true;
        }
        if (dataPickerFilterBirthdayFrom.getValue() != null) {
            return true;
        }
        if (dataPickerFilterBirthdayTill.getValue() != null) {
            return true;
        }
        return false;
    }

    private boolean compareForFilter(Student student) {
        //logger.info(student);
        if (textFFilterId.getText() != null && textFFilterId.getText().length() > 0) {
            if (!String.valueOf(student.getId()).toUpperCase().contains(textFFilterId.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterLastName.getText() != null && textFFilterLastName.getText().length() > 0) {
            if (!student.getLastName().toUpperCase().contains(textFFilterLastName.getText().toUpperCase())) {

                return false;
            }
        }
        if (textFFilterFirstName.getText() != null && textFFilterFirstName.getText().length() > 0) {
            if (!student.getFirsName().toUpperCase().contains(textFFilterFirstName.getText().toUpperCase())) {
                return false;
            }
        }

        if (textFFilterMiddleName.getText() != null && textFFilterMiddleName.getText().length() > 0) {
            if (!student.getMiddleName().toUpperCase().contains(textFFilterMiddleName.getText().toUpperCase())) {
                return false;
            }
        }

        if (textFFilterAddress.getText() != null && textFFilterAddress.getText().length() > 0) {
            if (!student.getAddress().toUpperCase().contains(textFFilterAddress.getText().toUpperCase())) {
                return false;
            }
        }
        if (dataPickerFilterBirthdayFrom.getValue() != null) {
            if (student.getBirthday().compareTo(dataPickerFilterBirthdayFrom.getValue())<0) {
                return false;
            }

        }
        if (dataPickerFilterBirthdayTill.getValue() != null) {
            if (student.getBirthday().compareTo(dataPickerFilterBirthdayTill.getValue())>0) {
                return false;
            }
        }

        return true;
    }

    public void onButtonCancel(ActionEvent actionEvent) {
        initTabPane();
        initHBoxes(true);
        if (isFilter && filteredData!=null) {
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
        tableViewStudent.getSelectionModel().selectFirst();
        tableViewStudent.scrollTo(tableViewStudent.getSelectionModel().getSelectedIndex());
    }

    public void onButtonPrior(ActionEvent actionEvent) {
        tableViewStudent.getSelectionModel().selectPrevious();
        tableViewStudent.scrollTo(tableViewStudent.getSelectionModel().getSelectedIndex());
    }

    public void onButtonNext(ActionEvent actionEvent) {
        tableViewStudent.getSelectionModel().selectNext();
        tableViewStudent.scrollTo(tableViewStudent.getSelectionModel().getSelectedIndex());
    }

    public void onButtonLast(ActionEvent actionEvent) {
        tableViewStudent.getSelectionModel().selectLast();
        tableViewStudent.scrollTo(tableViewStudent.getSelectionModel().getSelectedIndex());
    }

    public void onButtonRefresh(ActionEvent actionEvent) {
        tableViewStudent.refresh();
    }

    private void showStudentDetails(Student student) {
        if (student != null) {
            textFViewId.setText(String.valueOf(student.getId()));
            textFViewLastName.setText(student.getLastName());
            textFViewFirstName.setText(student.getFirsName());
            textFViewMiddleName.setText(student.getMiddleName());
            textFViewBirthday.setText(DateUtil.format(student.getBirthday()));
            textFViewAddress.setText(student.getAddress());
        } else {
            // Если Student = null, то убираем весь текст.
            textFViewId.setText("");
            textFViewLastName.setText("");
            textFViewFirstName.setText("");
            textFViewMiddleName.setText("");
            textFViewBirthday.setText("");
            textFViewAddress.setText("");
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (textFEditLastName.getText() == null || textFEditLastName.getText().length() == 0) {
            errorMessage += "Невірне прізвище!\n";
        }
        if (textFEditFirstName.getText() == null || textFEditFirstName.getText().length() == 0) {
            errorMessage += "Невірне ім’я!\n";
        }

        if (textFEditMiddleName.getText() == null || textFEditMiddleName.getText().length() == 0) {
            errorMessage += "Невірне побатькові!\n";
        }
        /*
        if (textFEditAddress.getText() == null || textFEditAddress.getText().length() == 0) {
            errorMessage += "Невірна адреса!\n";
        }
        */
        if (dataPickerEditBirthday.getValue() == null) {
            errorMessage += "Невірний день народження!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка заповнення", "Будь ласка заповнить помилкові поля", errorMessage), null);
            //alert.initOwner(dialogStage);
            return false;
        }
    }

    public void onMenuItemExcel(ActionEvent actionEvent) {
        ExportData exportData = new ExportData();
        Student newStudent = new Student();
        exportData.setFieldList(newStudent.getFieldList());
        exportData.setExportFileType(ExportFileType.XLSX);

        List<Object[]> objects = new ArrayList<>();

        ObservableList<Student> students;
        if (filteredData!=null ){
            students=filteredData;
        }
        else {
            students = studentObservableList;
        }

        for (Student teacher : students) {
            objects.add(teacher.getObjects());
        }

        exportData.setTableData(objects);

        try {
            ExportServices.exportData(dialogStage, exportData);
            if (exportData.getFile()!=null && exportData.getFile().exists()) {
                Dialogs.showMessage(Alert.AlertType.INFORMATION, new DialogText("Експорт даних", "Файл '" + exportData.getFile()
                        .getName() + "' збережено успішно", ""), logger);
            }
        } catch (IOException e) {
            //e.printStackTrace();
            Dialogs.showErrorDialog(e, new DialogText("Експорт даних", "Файл не збережено", ""), logger);
        }
    }
}
