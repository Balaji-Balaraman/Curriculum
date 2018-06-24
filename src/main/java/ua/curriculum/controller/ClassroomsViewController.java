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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.curriculum.MainApp;
import ua.curriculum.dao.ClassroomDao;
import ua.curriculum.export.ExportData;
import ua.curriculum.export.ExportFileType;
import ua.curriculum.export.ExportServices;
import ua.curriculum.fx.DialogText;
import ua.curriculum.fx.Dialogs;
import ua.curriculum.fx.ImageResources;
import ua.curriculum.model.Classroom;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClassroomsViewController {
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
    private TableView<Classroom> tableViewData;

    @FXML
    private TableColumn<Classroom, Integer> tableColumnId;
    @FXML
    private TableColumn<Classroom, String> tableColumnFullName;
    @FXML
    private TableColumn<Classroom, String> tableColumnShortName;
    @FXML
    private TableColumn<Classroom, String> tableColumnCode;

    @FXML
    private TextField textFViewId;
    @FXML
    private TextField textFViewFullName;
    @FXML
    private TextField textFViewShortName;
    @FXML
    private TextField textFViewCode;

    @FXML
    private TextField textFEditId;
    @FXML
    private TextField textFEditFullName;
    @FXML
    private TextField textFEditShortName;
    @FXML
    private TextField textFEditCode;

    @FXML
    private TextField textFFilterId;
    @FXML
    private TextField textFFilterFullName;
    @FXML
    private TextField textFFilterShortName;
    @FXML
    private TextField textFFilterCode;

    @FXML
    private MenuButton menuButtonReports;
    @FXML
    private MenuItem menuItemExcel;

    private ClassroomDao dao;

    private ObservableList<Classroom> classroomObservableList = FXCollections.observableArrayList();
    private FilteredList<Classroom> filteredData;

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

        dao = new ClassroomDao(mainApp.getConnection());
        try {
            refreshTableData();

        } catch (SQLException e) {
            Dialogs.showErrorDialog(e, new DialogText("Помилка отримання даних", "", "Неможливо отримати данні з таблиці 'Аудиторії'"), logger);
        }
    }

    private void refreshTableData() throws SQLException {
        classroomObservableList.clear();
        tableViewData.getItems().removeAll(tableViewData.getItems());

        classroomObservableList.addAll(dao.findAllData());
        tableViewData.setItems(classroomObservableList);
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

        //tableViewData.setPlaceholder(new Label("Дані відсутні"));

        initButtonsIcons();
        initButtonsToolTip();

        initHBoxes(true);

        initTabPane();


        initColumnValueFactories();

        showClassroomDetails(null);

        initComponentListeners();
    }

    private void initComponentListeners() {
        tableViewData.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showClassroomDetails(newValue));
    }

    private void initColumnValueFactories() {
        tableColumnId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        tableColumnFullName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tableColumnShortName.setCellValueFactory(cellData -> cellData.getValue().shortNameProperty());
        tableColumnCode.setCellValueFactory(cellData -> cellData.getValue().codeProperty());

    }
    private void initTabPane() {
        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabTable, tabView);
        tabPane.getSelectionModel().select(tabTable);
    }

    private void initHBoxes(boolean isView) {

        if (mainApp!=null) {
            hBoxEdit.setVisible(isView && mainApp.getLoginUser().getUserType().getId()==1);
        }
        else{
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
        textFEditFullName.setText("");
        textFEditShortName.setText("");
        textFEditCode.setText("");

        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabEdit);
        initHBoxes(false);

        isEdited = true;

    }

    public void onButtonEdit(ActionEvent actionEvent) {
        int selectedIndex = tableViewData.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Classroom classroom = tableViewData.getItems().get(selectedIndex);
            textFEditId.setText(String.valueOf(classroom.getId()));
            textFEditFullName.setText(classroom.getName());
            textFEditShortName.setText(classroom.getShortName());
            textFEditCode.setText(classroom.getCode());

            tabPane.getTabs().removeAll(tabPane.getTabs());
            tabPane.getTabs().addAll(tabEdit);

            initHBoxes(false);

            isEdited = true;
        }else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка редагування даних", "Запис не вибраний", "Будь ласка виберіть запис в таблиці"), null);
        }
    }

    public void onButtonDelete(ActionEvent actionEvent) {
        int selectedIndex = tableViewData.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Classroom classroom = tableViewData.getItems().get(selectedIndex);
            if (Dialogs.showConfirmDialog(new DialogText("Підтвердження дії", "Бажаєте вілучити запис", "Ви дійсно бажаєте вилучити '" + classroom.getName()+ "'"), logger)) {
                try {
                    if (dao.deleteById(classroom.getId())) {
                        tableViewData.getItems().remove(selectedIndex);
                    }
                } catch (SQLException e) {
                    Dialogs.showErrorDialog(e, new DialogText("Помилка вилучення даних", "", "Неможливо вилучити '" + classroom.getName() + "'"), logger);
                }
            }
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка вилучення даних", "Запис не вибраний", "Будь ласка виберіть запис в таблиці"), null);
        }
    }

    public void onButtonOk(ActionEvent actionEvent) {
        //initTabPane();
        //initHBoxes(true);
        boolean isInsert = (textFEditId.getText() == null || textFEditId.getText().equals("")) ? true : false;
        if (isEdited) {
            if (isInputValid()) {
                Classroom classroom = new Classroom();
                classroom.setName(textFEditFullName.getText());
                classroom.setShortName(textFEditShortName.getText());
                classroom.setCode(textFEditCode.getText());

                if (isInsert) {
                    try {
                        if (dao.insert(classroom)) {
                            refreshTableData();
                        }
                    } catch (SQLException e) {
                        Dialogs.showErrorDialog(e, new DialogText("Помилка додавання даних", "", "Неможливо додати '" + classroom.getName() + "'"), logger);
                    }
                } else if (!isInsert) {
                    classroom.setId(Integer.parseInt(textFEditId.getText()));
                    try {
                        if (dao.update(classroom)) {
                            refreshTableData();
                        }
                    } catch (SQLException e) {
                        Dialogs.showErrorDialog(e, new DialogText("Помилка редагування даних", "", "Неможливо змінити '" + classroom.getName() + "'"), logger);
                    }

                }

                initTabPane();
                initHBoxes(true);

                isEdited = false;
            }
        } else {
            // for filter
            //logger.info("hasFilterPattern()= " + hasFilterPattern());
            filteredData = new FilteredList<>(classroomObservableList, p -> true);

            if (!hasFilterPattern()) {
                filteredData.setPredicate(classroom -> true);
            } else {
                filteredData.setPredicate(classroom -> compareForFilter(classroom));
            }

            SortedList<Classroom> sortedData = new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(tableViewData.comparatorProperty());

            tableViewData.setItems(sortedData);


            initTabPane();
            initHBoxes(true);

            isFilter = false;
        }
    }

    private boolean hasFilterPattern() {
        if (textFFilterId.getText() != null && textFFilterId.getText().length() > 0) {
            return true;
        }
        if (textFFilterFullName.getText() != null && textFFilterFullName.getText().length() > 0) {
            return true;
        }
        if (textFFilterShortName.getText() != null && textFFilterShortName.getText().length() > 0) {
            return true;
        }
        if (textFFilterCode.getText() != null && textFFilterCode.getText().length() > 0) {
            return true;
        }

        return false;
    }

    private boolean compareForFilter(Classroom classroom) {
        //logger.info(subject);
        if (textFFilterId.getText() != null && textFFilterId.getText().length() > 0) {
            if (!String.valueOf(classroom.getId()).toUpperCase().contains(textFFilterId.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterFullName.getText() != null && textFFilterFullName.getText().length() > 0) {
            if (!classroom.getName().toUpperCase().contains(textFFilterFullName.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterShortName.getText() != null && textFFilterShortName.getText().length() > 0) {
            if (!classroom.getShortName().toUpperCase().contains(textFFilterShortName.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterCode.getText() != null && textFFilterCode.getText().length() > 0) {
            if (!classroom.getCode().toUpperCase().contains(textFFilterCode.getText().toUpperCase())) {
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
        tableViewData.getSelectionModel().selectFirst();
        tableViewData.scrollTo(tableViewData.getSelectionModel().getSelectedIndex());
    }

    public void onButtonPrior(ActionEvent actionEvent) {
        tableViewData.getSelectionModel().selectPrevious();
        tableViewData.scrollTo(tableViewData.getSelectionModel().getSelectedIndex());
    }

    public void onButtonNext(ActionEvent actionEvent) {
        tableViewData.getSelectionModel().selectNext();
        tableViewData.scrollTo(tableViewData.getSelectionModel().getSelectedIndex());
    }

    public void onButtonLast(ActionEvent actionEvent) {
        tableViewData.getSelectionModel().selectLast();
        tableViewData.scrollTo(tableViewData.getSelectionModel().getSelectedIndex());
    }

    public void onButtonRefresh(ActionEvent actionEvent) {
        tableViewData.refresh();
    }

    private void showClassroomDetails(Classroom classroom) {
        if (classroom != null) {
            textFViewId.setText(String.valueOf(classroom.getId()));
            textFViewFullName.setText(classroom.getName());
            textFViewShortName.setText(classroom.getShortName());
            textFViewCode.setText(classroom.getCode());
        } else {
            textFViewId.setText("");
            textFViewFullName.setText("");
            textFViewShortName.setText("");
            textFViewCode.setText("");
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (textFEditFullName.getText() == null || textFEditFullName.getText().length() == 0) {
            errorMessage += "Невірна назва!\n";
        }
        if (textFEditShortName.getText() == null || textFEditShortName.getText().length() == 0) {
            errorMessage += "Невірна скорочена назва!\n";
        }

        if (textFEditCode.getText() == null || textFEditCode.getText().length() == 0) {
            errorMessage += "Невірне позначення!\n";
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
        Classroom newClassroom = new Classroom();
        exportData.setFieldList(newClassroom.getFieldList());
        exportData.setExportFileType(ExportFileType.XLSX);

        List<Object[]> objects = new ArrayList<>();

        ObservableList<Classroom> classrooms;
        if (filteredData!=null ){
            classrooms=filteredData;
        }
        else {
            classrooms = classroomObservableList;
        }

        for (Classroom classroom : classrooms) {
            objects.add(classroom.getObjects());
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
