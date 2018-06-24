package ua.curriculum.controller;

import javafx.beans.property.SimpleStringProperty;
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
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.curriculum.MainApp;
import ua.curriculum.dao.GroupDao;
import ua.curriculum.dao.SchoolYearDao;
import ua.curriculum.dao.SpecialityDao;
import ua.curriculum.dao.StudentDao;
import ua.curriculum.export.ExportData;
import ua.curriculum.export.ExportFileType;
import ua.curriculum.export.ExportServices;
import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.fx.DialogText;
import ua.curriculum.fx.Dialogs;
import ua.curriculum.fx.ImageResources;
import ua.curriculum.model.Group;
import ua.curriculum.model.Schoolyear;
import ua.curriculum.model.Speciality;
import ua.curriculum.model.Student;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class GroupsViewController {
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
    private Button buttonShowSchoolyear;
    @FXML
    private Button buttonShowSpeciality;
    @FXML
    private Button buttonShowStudents;

    @FXML
    private Button buttonNewStudent;
    @FXML
    private Button buttonDeleteStudent;

    @FXML
    private TableView<Group> tableViewGroup;
    @FXML
    private TableView<Student> tableViewStudentData;

    @FXML
    private TableColumn<Group, Integer> tableColumnId;
    @FXML
    private TableColumn<Group, String> tableColumnCode;
    @FXML
    private TableColumn<Group, String> tableColumnSchoolyearFullName;
    @FXML
    private TableColumn<Group, String> tableColumnSpecialityFullName;
    @FXML
    private TableColumn<Group, String> tableColumnFullName;
    @FXML
    private TableColumn<Group, String> tableColumnShortName;

    @FXML
    private TableColumn<Student, Integer> tableColumnStudentID;
    @FXML
    private TableColumn<Student, String> tableColumnStudentPIP;

    @FXML
    private TextField textFViewId;
    @FXML
    private TextField textFViewCode;
    @FXML
    private TextField textFViewSchoolyearFullName;
    @FXML
    private TextField textFViewSpecialityFullName;
    @FXML
    private TextField textFViewFullName;
    @FXML
    private TextField textFViewShortName;

    @FXML
    private TextField textFEditId;
    @FXML
    private TextField textFEditCode;
    @FXML
    private ComboBox<ComboBoxItem> comboBoxEditSchoolyear = new ComboBox<ComboBoxItem>();
    @FXML
    private ComboBox<ComboBoxItem> comboBoxEditSpeciality = new ComboBox<ComboBoxItem>();

    @FXML
    private TextField textFEditFullName;
    @FXML
    private TextField textFEditShortName;

    @FXML
    private TextField textFFilterId;
    @FXML
    private TextField textFFilterCode;
    @FXML
    private TextField textFFilterSchoolyearFullName;
    @FXML
    private TextField textFFilterSpecialityFullName;
    @FXML
    private TextField textFFilterFullName;
    @FXML
    private TextField textFFilterShortName;
    @FXML
    private ComboBox<ComboBoxItem> comboBoxFilterStudent = new ComboBox<>();

    @FXML
    private MenuButton menuButtonReports;
    @FXML
    private MenuItem menuItemExcel;
    @FXML
    private MenuItem menuItemJasper;

    private GroupDao groupDao;
    private StudentDao studentDao;

    private ObservableList<Group> groupObservableList = FXCollections.observableArrayList();
    private ObservableList<Student> selectedGroupStudentData = FXCollections.observableArrayList();
    private FilteredList<Group> filteredData;

    private ObservableList<ComboBoxItem> observableListSchoolyear = FXCollections.observableArrayList();
    private ObservableList<ComboBoxItem> observableListSpecialities = FXCollections.observableArrayList();
    private ObservableList<ComboBoxItem> observableListStudents = FXCollections.observableArrayList();

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

        groupDao = new GroupDao(mainApp.getConnection());
        studentDao = new StudentDao(mainApp.getConnection());

        SchoolYearDao schoolYearDao = new SchoolYearDao(mainApp.getConnection());
        SpecialityDao specialityDao = new SpecialityDao(mainApp.getConnection());
        try {
            refreshTableData();

            List<ComboBoxItem> list = schoolYearDao.findAllComboBoxData();
            observableListSchoolyear.addAll(list);
            comboBoxEditSchoolyear.setItems(observableListSchoolyear);

            list = specialityDao.findAllComboBoxData();
            observableListSpecialities.addAll(list);
            comboBoxEditSpeciality.setItems(observableListSpecialities);

            list = studentDao.findAllComboBoxData();
            observableListStudents.add(new ComboBoxItem("0", ""));
            observableListStudents.addAll(list);

            comboBoxFilterStudent.setItems(observableListStudents);

        } catch (SQLException e) {
            Dialogs.showErrorDialog(e, new DialogText("Помилка отримання даних", "",
                                                      "Неможливо отримати данні з таблиці 'Групи'"), logger);
        }
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

        initTabPane();

        initColumnValueFactories();

        showGroupDetails(null);

        initComponentListeners();
    }


    private void initComponentListeners() {
        tableViewGroup.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showGroupDetails(newValue));
    }


    private void initColumnValueFactories() {
        tableColumnId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        tableColumnCode.setCellValueFactory(cellData -> cellData.getValue().codeProperty());

        tableColumnSchoolyearFullName.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getSchoolyear().getName()));
        tableColumnSpecialityFullName.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getSpeciality().getName()));

        tableColumnFullName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tableColumnShortName.setCellValueFactory(cellData -> cellData.getValue().shortNameProperty());


        tableColumnStudentID.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        tableColumnStudentPIP
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullPIP()));
    }


    private void initTabPane() {
        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabTable, tabView);
        tabPane.getSelectionModel().select(tabTable);
    }

    private void initHBoxes(boolean isView) {
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
        buttonNew.setTooltip(new Tooltip("Додати запис"));
        buttonEdit.setTooltip(new Tooltip("Редагувати запис"));
        buttonDelete.setTooltip(new Tooltip("Вилучити запис"));

        buttonNewStudent.setTooltip(new Tooltip("Додати студента"));
        buttonDeleteStudent.setTooltip(new Tooltip("Вилучити студента"));

        buttonFilter.setTooltip(new Tooltip("Відфільтрувати записи"));

        buttonOk.setTooltip(new Tooltip("Підтвердити зміни"));
        buttonCancel.setTooltip(new Tooltip("Відмінити зміни"));

        buttonFirst.setTooltip(new Tooltip("Перший запис"));
        buttonPrior.setTooltip(new Tooltip("Попередній запис"));
        buttonNext.setTooltip(new Tooltip("Наступний запис"));
        buttonLast.setTooltip(new Tooltip("Останній запис"));
        buttonRefresh.setTooltip(new Tooltip("Оновити дані"));

        buttonShowSchoolyear.setTooltip(new Tooltip("Показати довідник навчальних років"));
        buttonShowSpeciality.setTooltip(new Tooltip("Показати довідник спеціальностей"));

        buttonShowStudents.setTooltip(new Tooltip("Показати довідник студентів"));

        menuButtonReports.setTooltip(new Tooltip("Звіти"));
    }

    private void initButtonsIcons() {
        buttonNew.setGraphic(new ImageView(ImageResources.getButtonPlus()));
        buttonEdit.setGraphic(new ImageView(ImageResources.getButtonEdit()));
        buttonDelete.setGraphic(new ImageView(ImageResources.getButtonDelete()));

        buttonNewStudent.setGraphic(new ImageView(ImageResources.getButtonPlus()));
        buttonDeleteStudent.setGraphic(new ImageView(ImageResources.getButtonDelete()));

        buttonFilter.setGraphic(new ImageView(ImageResources.getButtonFilter()));

        buttonOk.setGraphic(new ImageView(ImageResources.getButtonOk()));
        buttonCancel.setGraphic(new ImageView(ImageResources.getButtonCancel()));

        buttonFirst.setGraphic(new ImageView(ImageResources.getButtonFirst()));
        buttonPrior.setGraphic(new ImageView(ImageResources.getButtonPrior()));
        buttonNext.setGraphic(new ImageView(ImageResources.getButtonNext()));
        buttonLast.setGraphic(new ImageView(ImageResources.getButtonLast()));
        buttonRefresh.setGraphic(new ImageView(ImageResources.getButtonRefresh()));

        buttonShowSchoolyear.setGraphic(new ImageView(ImageResources.getButtonView()));
        buttonShowSpeciality.setGraphic(new ImageView(ImageResources.getButtonView()));

        buttonShowStudents.setGraphic(new ImageView(ImageResources.getButtonView()));

        menuButtonReports.setGraphic(new ImageView(ImageResources.getReportIcon()));
        menuItemExcel.setGraphic(new ImageView(ImageResources.getXlsx16Icon()));
        menuItemJasper.setGraphic(new ImageView(ImageResources.getReportsIcon()));
    }

    public void onButtonNew(ActionEvent actionEvent) {

        textFEditId.setText("");
        textFEditCode.setText("");
        comboBoxEditSchoolyear.getSelectionModel().clearSelection();
        comboBoxEditSpeciality.getSelectionModel().clearSelection();
        textFEditFullName.setText("");
        textFEditShortName.setText("");

        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabEdit);
        initHBoxes(false);

        isEdited = true;
    }

    public void onButtonEdit(ActionEvent actionEvent) {
        int selectedIndex = tableViewGroup.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Group group = tableViewGroup.getItems().get(selectedIndex);
            textFEditId.setText(String.valueOf(group.getId()));
            textFEditCode.setText(group.getCode());

            if (group.getSchoolyear() == null) {
                comboBoxEditSchoolyear.getSelectionModel().clearSelection();
            } else {
                ComboBoxItem comboBoxItemSchoolyear = new ComboBoxItem(String.valueOf(group.getSchoolyear().getId()),
                                                                       group.getSchoolyear().getName());
                comboBoxEditSchoolyear.getSelectionModel().select(comboBoxItemSchoolyear);
            }
            if (group.getSpeciality() == null) {
                comboBoxEditSpeciality.getSelectionModel().clearSelection();
            } else {
                ComboBoxItem comboBoxItemSpeciality = new ComboBoxItem(String.valueOf(group.getSpeciality().getId()),
                                                                       group.getSpeciality().getName());
                comboBoxEditSpeciality.getSelectionModel().select(comboBoxItemSpeciality);
            }

            textFEditFullName.setText(group.getName());
            textFEditShortName.setText(group.getShortName());

            tabPane.getTabs().removeAll(tabPane.getTabs());
            tabPane.getTabs().addAll(tabEdit);

            initHBoxes(false);

            isEdited = true;
        }

    }

    public void onButtonDelete(ActionEvent actionEvent) {
        int selectedIndex = tableViewGroup.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Group group = tableViewGroup.getItems().get(selectedIndex);
            if (Dialogs.showConfirmDialog(new DialogText("Підтвердження дії", "Бажаєте вілучити запис",
                                                         "Ви дійсно бажаєте вилучити '" + group.getCode() + " " +
                                                         group.getName() + "'"), logger)) {
                try {
                    if (groupDao.deleteById(group.getId())) {
                        tableViewGroup.getItems().remove(selectedIndex);
                    }
                } catch (SQLException e) {
                    Dialogs.showErrorDialog(e, new DialogText("Помилка вилучення даних", "",
                                                              "Неможливо вилучити '" + group.getCode() + " " +
                                                              group.getName() + "'"), logger);
                }
            }
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка вилучення даних", "Запис не вибраний",
                                                                      "Будь ласка виберіть запис в таблиці"), null);
        }
    }

    public void onButtonOk(ActionEvent actionEvent) {
        boolean isInsert = isInsert();
        if (isEdited) {
            if (isInputValid()) {
                Group group = new Group();
                group.setCode(textFEditCode.getText());

                ComboBoxItem comboBoxItem = comboBoxEditSchoolyear.getValue();
                Schoolyear schoolyear = null;
                if (comboBoxItem != null) {
                    schoolyear = new Schoolyear();
                    schoolyear.setId(Integer.parseInt(comboBoxItem.getObjectId()));
                }
                group.setSchoolyear(schoolyear);

                comboBoxItem = comboBoxEditSpeciality.getValue();
                Speciality speciality = null;
                if (comboBoxItem != null) {
                    speciality = new Speciality();
                    speciality.setId(Integer.parseInt(comboBoxItem.getObjectId()));
                }
                group.setSpeciality(speciality);

                group.setName(textFEditFullName.getText());
                group.setShortName(textFEditShortName.getText());

                if (isInsert) {
                    try {
                        if (groupDao.insert(group)) {
                            refreshTableData();

                            finishEdit();
                        }
                    } catch (SQLException e) {
                        Dialogs.showErrorDialog(e, new DialogText("Помилка додавання даних", "",
                                                                  "Неможливо додати '" + group.getCode() + " " +
                                                                  group.getName() + "'"), logger);
                    }
                } else if (!isInsert) {
                    group.setId(Integer.parseInt(textFEditId.getText()));
                    try {
                        if (groupDao.update(group)) {
                            refreshTableData();

                            finishEdit();
                        }

                    } catch (SQLException e) {
                        Dialogs.showErrorDialog(e, new DialogText("Помилка редагування даних", "",
                                                                  "Неможливо змінити '" + group.getCode() + " " +
                                                                  group.getName() + "'"), logger);
                    }

                }

                isEdited = false;

                initTabPane();
                initHBoxes(true);


            }
        } else {
            // for filter
            //logger.info("hasFilterPattern()= " + hasFilterPattern());

            filteredData = new FilteredList<>(groupObservableList, p -> true);

            if (!hasFilterPattern()) {
                filteredData.setPredicate(group -> true);
            } else {
                filteredData.setPredicate(group -> compareForFilter(group));
            }

            SortedList<Group> sortedData = new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(tableViewGroup.comparatorProperty());

            tableViewGroup.setItems(sortedData);

            initTabPane();
            initHBoxes(true);

            isFilter = false;
        }
    }

    private boolean hasFilterPattern() {
        if (textFFilterId.getText() != null && textFFilterId.getText().length() > 0) {
            return true;
        }
        if (textFFilterCode.getText() != null && textFFilterCode.getText().length() > 0) {
            return true;
        }
        if (textFFilterSchoolyearFullName.getText() != null && textFFilterSchoolyearFullName.getText().length() > 0) {
            return true;
        }
        if (textFFilterSpecialityFullName.getText() != null && textFFilterSpecialityFullName.getText().length() > 0) {
            return true;
        }

        if (textFFilterFullName.getText() != null && textFFilterFullName.getText().length() > 0) {
            return true;
        }
        if (textFFilterShortName.getText() != null && textFFilterShortName.getText().length() > 0) {
            return true;
        }
        if (comboBoxFilterStudent.getValue() != null && !comboBoxFilterStudent.getValue().getObjectId().equals("0")) {
            return true;
        }

        return false;
    }

    private boolean compareForFilter(Group group) {
        //logger.info(subject);
        if (textFFilterId.getText() != null && textFFilterId.getText().length() > 0) {
            if (!String.valueOf(group.getId()).toUpperCase().contains(textFFilterId.getText().toUpperCase())) {
                return false;
            }
        }

        if (textFFilterCode.getText() != null && textFFilterCode.getText().length() > 0) {
            if (!group.getCode().toUpperCase().contains(textFFilterCode.getText().toUpperCase())) {
                return false;
            }
        }


        if (textFFilterSpecialityFullName.getText() != null && textFFilterSpecialityFullName.getText().length() > 0) {
            if (!group.getSpeciality().getName().toUpperCase()
                    .contains(textFFilterSpecialityFullName.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterSpecialityFullName.getText() != null && textFFilterSpecialityFullName.getText().length() > 0) {
            if (!group.getCode().toUpperCase().contains(textFFilterCode.getText().toUpperCase())) {
                return false;
            }
        }

        if (textFFilterFullName.getText() != null && textFFilterFullName.getText().length() > 0) {
            if (!group.getName().toUpperCase().contains(textFFilterFullName.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterShortName.getText() != null && textFFilterShortName.getText().length() > 0) {
            if (!group.getShortName().toUpperCase().contains(textFFilterShortName.getText().toUpperCase())) {
                return false;
            }
        }

        if (comboBoxFilterStudent.getValue() != null && !comboBoxFilterStudent.getValue().getObjectId().equals("0")) {
            try {
                boolean groupHasStudent = groupDao.getStudentsGroupsId(group.getId(), Integer.parseInt(
                        comboBoxFilterStudent.getValue().getObjectId()));

                if (!groupHasStudent) {
                    return false;
                }
            } catch (SQLException e) {
                logger.info(e.getMessage());
            }
        }
        //logger.info("TRUE: "  + group);
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

    public void onButtonNewStudent(ActionEvent actionEvent) {
        int selectedIndex = tableViewGroup.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Group group = tableViewGroup.getItems().get(selectedIndex);
            ComboBoxItem item = null;
            try {
                item = mainApp.showSearchFormDialog(studentDao.findNewStudentsForGroup(group.getId()));
                if (item != null && item.getObjectId() != null) {
                    int studentId = Integer.parseInt(item.getObjectId());
                    if (groupDao.insertIntoGroupsStudents(group.getId(), studentId)) {
                        refreshStudentsData();

                        selectedGroupStudentData.addAll(studentDao.findGroupStudentsById(group.getId()));
                        tableViewStudentData.setItems(selectedGroupStudentData);
                    }
                }
            } catch (SQLException e) {
                Dialogs.showErrorDialog(e, new DialogText("Помилка додавання даних", "",
                                                          "Неможливо додати '" + item.getObjectDisplayName() + "'"),
                                        logger);
            }

        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка редагування даних", "Запис не вибраний",
                                                                      "Будь ласка виберіть запис в таблиці"), null);
        }
    }

    public void onButtonDeleteStudent(ActionEvent actionEvent) {
        int selectedGroupIndex = tableViewGroup.getSelectionModel().getSelectedIndex();
        int selectedStudentIndex = tableViewStudentData.getSelectionModel().getSelectedIndex();

        if (selectedGroupIndex >= 0 && selectedStudentIndex >= 0) {
            Group group = tableViewGroup.getItems().get(selectedGroupIndex);
            Student student = tableViewStudentData.getItems().get(selectedStudentIndex);
            if (Dialogs.showConfirmDialog(new DialogText("Підтвердження дії", "Бажаєте вілучити запис",
                                                         "Ви дійсно бажаєте вилучити '" + student.getFullPIP() + "'"),
                                          logger)) {
                try {
                    if (groupDao.deleteFromGroupsStudents(group.getId(), student.getId())) {
                        tableViewStudentData.getItems().remove(selectedStudentIndex);
                    }

                } catch (SQLException e) {
                    Dialogs.showErrorDialog(e, new DialogText("Помилка вилучення даних", "",
                                                              "Неможливо вилучити '" + student.getFullPIP() + "'"),
                                            logger);
                }
            }
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка вилучення даних", "Запис не вибраний",
                                                                      "Будь ласка виберіть запис в таблиці"), null);
        }
    }


    private void finishEdit() {
        initTabPane();
        initHBoxes(true);
        isEdited = false;
    }

    private boolean isInsert() {
        return (textFEditId.getText() == null || textFEditId.getText().equals("")) ? true : false;
    }

    private void refreshTableData() throws SQLException {
        groupObservableList.clear();
        tableViewGroup.getItems().removeAll(tableViewGroup.getItems());

        groupObservableList.addAll(groupDao.findAllData());
        tableViewGroup.setItems(groupObservableList);
    }

    private void refreshStudentsData() {
        selectedGroupStudentData.clear();
        tableViewStudentData.getItems().removeAll(tableViewStudentData.getItems());
    }


    public void onButtonFirst(ActionEvent actionEvent) {
        tableViewGroup.getSelectionModel().selectFirst();
        tableViewGroup.scrollTo(tableViewGroup.getSelectionModel().getSelectedIndex());
    }

    public void onButtonPrior(ActionEvent actionEvent) {
        tableViewGroup.getSelectionModel().selectPrevious();
        tableViewGroup.scrollTo(tableViewGroup.getSelectionModel().getSelectedIndex());
    }

    public void onButtonNext(ActionEvent actionEvent) {
        tableViewGroup.getSelectionModel().selectNext();
        tableViewGroup.scrollTo(tableViewGroup.getSelectionModel().getSelectedIndex());
    }

    public void onButtonLast(ActionEvent actionEvent) {
        tableViewGroup.getSelectionModel().selectLast();
        tableViewGroup.scrollTo(tableViewGroup.getSelectionModel().getSelectedIndex());
    }

    public void onButtonRefresh(ActionEvent actionEvent) {
        tableViewGroup.refresh();
    }

    private void showGroupDetails(Group group) {
        refreshStudentsData();

        if (group != null) {
            textFViewId.setText(String.valueOf(group.getId()));
            textFViewCode.setText(group.getCode());
            textFViewSchoolyearFullName.setText((group.getSchoolyear() != null) ? group.getSchoolyear().getName() : "");
            textFViewSpecialityFullName.setText((group.getSpeciality() != null) ? group.getSpeciality().getName() : "");
            textFViewFullName.setText(group.getName());
            textFViewShortName.setText(group.getShortName());

            try {
                selectedGroupStudentData.addAll(studentDao.findGroupStudentsById(group.getId()));
            } catch (SQLException e) {
                Dialogs.showErrorDialog(e, new DialogText("Помилка отримання даних", "",
                                                          "Неможливо отримати дані про Студентів"), logger);
            }
            tableViewStudentData.setItems(selectedGroupStudentData);

        } else {
            textFViewId.setText("");
            textFViewCode.setText("");
            textFViewSchoolyearFullName.setText("");
            textFViewSpecialityFullName.setText("");
            textFViewFullName.setText("");
            textFViewShortName.setText("");

        }
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (textFEditCode.getText() == null || textFEditCode.getText().length() == 0) {
            errorMessage += "Невірний Шифр!\n";
        } else {
            try {
                Group group = groupDao.findByCode(textFEditCode.getText());
                if (group != null && !(!isInsert() && group.getId() == Integer.parseInt(textFEditId.getText()) &&
                                       group.getCode().equals(textFEditCode.getText()))) {
                    errorMessage += "Вказаний шифр існує!\n";
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (comboBoxEditSchoolyear.getValue() == null) {
            errorMessage += "Вкажіть навчальний рік!\n";
        }

        if (comboBoxEditSpeciality.getValue() == null) {
            errorMessage += "Вкажіть спеціальність!\n";
        }

        if (textFEditFullName.getText() == null || textFEditFullName.getText().length() == 0) {
            errorMessage += "Невірна назва!\n";
        }

        if (textFEditShortName.getText() == null || textFEditShortName.getText().length() == 0) {
            errorMessage += "Невірне скорочення!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR,
                                new DialogText("Помилка заповнення", "Будь ласка заповнить помилкові поля",
                                               errorMessage), null);
            return false;
        }
    }

    public void onButtonShowSchoolyear(ActionEvent actionEvent) {
        ComboBoxItem item = mainApp.showSearchFormDialog(observableListSchoolyear);
        if (item != null) {
            comboBoxEditSchoolyear.getSelectionModel().select(item);
        }
    }

    public void onButtonShowPerson(ActionEvent actionEvent) {
        ComboBoxItem item = mainApp.showSearchFormDialog(observableListSpecialities);
        if (item != null) {
            comboBoxEditSpeciality.getSelectionModel().select(item);
        }
    }

    public void onButtonShowStudents(ActionEvent actionEvent) {

        ComboBoxItem item = mainApp.showSearchFormDialog(observableListStudents);
        if (item != null) {
            comboBoxFilterStudent.getSelectionModel().select(item);
        }
    }

    public void onMenuItemExcel(ActionEvent actionEvent) {
        ExportData exportData = new ExportData();
        Group newGroup = new Group();
        exportData.setFieldList(newGroup.getFieldList());
        exportData.setExportFileType(ExportFileType.XLSX);

        List<Object[]> objects = new ArrayList<>();

        ObservableList<Group> groups = getReportGroups();

        for (Group group : groups) {
            objects.add(group.getObjects());
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

    public ObservableList<Group> getReportGroups() {
        ObservableList<Group> groups;
        if (filteredData != null) {
            groups = filteredData;
        } else {
            groups = groupObservableList;
        }
        return groups;
    }

    public void onMenuItemJasper(ActionEvent actionEvent) throws IOException {
        //MainApp.class.getResourceAsStream("/jasperReports/GroupsStudents.jrxml");
        PreparedStatement preparedStatement = null;
        ObservableList<Group> groups = getReportGroups();

        try {
            preparedStatement = mainApp.getConnection().prepareStatement("DELETE FROM temp_report");
            preparedStatement.executeUpdate();
            for (Group group : groups) {
                preparedStatement =
                        mainApp.getConnection().prepareStatement("INSERT INTO temp_report(id) " + "values(?)");
                preparedStatement.setInt(1, group.getId());
                preparedStatement.executeUpdate();
            }
            // First, compile jrxml file.
            JasperReport jasperReport = JasperCompileManager
                    .compileReport(MainApp.class.getResourceAsStream("/jasperReports/GroupsStudents.jrxml"));
            // Parameters for report
            Map<String, Object> parameters = new HashMap<String, Object>();

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, mainApp.getConnection());

            JasperViewer jasperViewer = new JasperViewer(jasperPrint);


            //List<JRSaveContributor> saveOptions = Arrays.asList(jasperViewer.get);
            //Iterator<JRSaveContributor> i = saveOptions.iterator();

            jasperViewer.viewReport(jasperPrint, false);
        } catch (SQLException | JRException e) {
            Dialogs.showErrorDialog(e, new DialogText("Експорт даних", "Файл не збережено", e.getMessage()), logger);
        }
    }


}
