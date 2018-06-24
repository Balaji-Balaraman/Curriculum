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
import ua.curriculum.dao.LessonDao;
import ua.curriculum.export.ExportData;
import ua.curriculum.export.ExportFileType;
import ua.curriculum.export.ExportServices;
import ua.curriculum.fx.DialogText;
import ua.curriculum.fx.Dialogs;
import ua.curriculum.fx.ImageResources;
import ua.curriculum.fx.TextFieldNumberListener;
import ua.curriculum.model.Lesson;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LessonsViewController {
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
    private TableView<Lesson> tableViewData;

    @FXML
    private TableColumn<Lesson, Integer> tableColumnId;
    @FXML
    private TableColumn<Lesson, Integer> tableColumnNumber;
    @FXML
    private TableColumn<Lesson, String> tableColumnTime;
    @FXML
    private TableColumn<Lesson, String> tableColumnDescription;

    @FXML
    private TextField textFViewId;
    @FXML
    private TextField textFViewNumber;
    @FXML
    private TextField textFViewTime;
    @FXML
    private TextField textFViewDescription;

    @FXML
    private TextField textFEditId;
    @FXML
    private TextField textFEditNumber;
    @FXML
    private TextField textFEditTime;
    @FXML
    private TextField textFEditDescription;

    @FXML
    private TextField textFFilterId;
    @FXML
    private TextField textFFilterNumber;
    @FXML
    private TextField textFFilterTime;
    @FXML
    private TextField textFFilterDescription;

    @FXML
    private MenuButton menuButtonReports;
    @FXML
    private MenuItem menuItemExcel;

    private LessonDao dao;

    private ObservableList<Lesson> lessonObservableList = FXCollections.observableArrayList();
    private FilteredList<Lesson> filteredData;

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

        dao = new LessonDao(mainApp.getConnection());
        try {
            refreshTableData();

        } catch (SQLException e) {
            Dialogs.showErrorDialog(e, new DialogText("Помилка отримання даних", "", "Неможливо отримати данні з таблиці 'Типи занять'"), logger);
        }
    }

    private void refreshTableData() throws SQLException {
        lessonObservableList.clear();
        tableViewData.getItems().removeAll(tableViewData.getItems());

        lessonObservableList.addAll(dao.findAllData());
        tableViewData.setItems(lessonObservableList);
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


        initColumnValueFactoies();

        showLessonDetails(null);

        initComponentListeners();
    }

    private void initComponentListeners() {
        tableViewData.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showLessonDetails(newValue));

        textFEditNumber.textProperty().addListener(new TextFieldNumberListener(textFEditNumber));
    }

    private void initColumnValueFactoies() {
        tableColumnId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        tableColumnNumber.setCellValueFactory(cellData -> cellData.getValue().numberProperty().asObject());
        tableColumnTime.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        tableColumnDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

    }
    private void initTabPane() {
        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabTable, tabView);
        tabPane.getSelectionModel().select(tabTable);
    }

    private void initHBoxes(boolean isView) {
        if (mainApp != null) {
            hBoxEdit.setVisible(isView && mainApp.getLoginUser().getUserType().getId() == 1);
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
        textFEditNumber.setText("");
        textFEditTime.setText("");
        textFEditDescription.setText("");

        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabEdit);
        initHBoxes(false);

        isEdited = true;

    }

    public void onButtonEdit(ActionEvent actionEvent) {
        int selectedIndex = tableViewData.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Lesson lesson = tableViewData.getItems().get(selectedIndex);
            textFEditId.setText(String.valueOf(lesson.getId()));
            textFEditNumber.setText(String.valueOf(lesson.getNumber()));
            textFEditTime.setText(lesson.getTime());
            textFEditDescription.setText(lesson.getDescription());

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
            Lesson lesson = tableViewData.getItems().get(selectedIndex);
            if (Dialogs.showConfirmDialog(new DialogText("Підтвердження дії", "Бажаєте вілучити запис", "Ви дійсно бажаєте вилучити '"
                                                                                                        + lesson.getNumberWithTime()+ "'"), logger)) {
                try {
                    if (dao.deleteById(lesson.getId())) {
                        tableViewData.getItems().remove(selectedIndex);
                    }
                } catch (SQLException e) {
                    Dialogs.showErrorDialog(e, new DialogText("Помилка вилучення даних", "", "Неможливо вилучити '" +
                                                                                             lesson.getNumberWithTime() + "'"),
                                            logger);
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
                Lesson lesson = new Lesson();
                lesson.setNumber( Integer.parseInt(textFEditNumber.getText()));
                lesson.setTime(textFEditTime.getText());
                lesson.setDescription(textFEditDescription.getText());

                if (isInsert) {
                    try {
                        if (dao.insert(lesson)) {
                            refreshTableData();
                        }
                    } catch (SQLException e) {
                        Dialogs.showErrorDialog(e, new DialogText("Помилка додавання даних", "", "Неможливо додати '"
                                                                                                 + lesson.getNumberWithTime() +
                                                                                                 "'"), logger);
                    }
                } else if (!isInsert) {
                    lesson.setId(Integer.parseInt(textFEditId.getText()));
                    try {
                        if (dao.update(lesson)) {
                            refreshTableData();
                        }
                    } catch (SQLException e) {
                        Dialogs.showErrorDialog(e, new DialogText("Помилка редагування даних", "", "Неможливо змінити" +
                                                                                                   " '" + lesson
                                                                                                           .getNumberWithTime() +
                                                                                                   "'"), logger);
                    }

                }
                isEdited = false;

                initTabPane();
                initHBoxes(true);
            }
        } else {
            // for filter
            //logger.info("hasFilterPattern()= " + hasFilterPattern());
            filteredData = new FilteredList<>(lessonObservableList, p -> true);

            if (!hasFilterPattern()) {
                filteredData.setPredicate(lessonType -> true);
            } else {
                filteredData.setPredicate(lesson -> compareForFilter(lesson));
            }

            SortedList<Lesson> sortedData = new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(tableViewData.comparatorProperty());

            tableViewData.setItems(sortedData);

            isFilter = false;

            initTabPane();
            initHBoxes(true);
        }
    }

    private boolean hasFilterPattern() {
        if (textFFilterId.getText() != null && textFFilterId.getText().length() > 0) {
            return true;
        }
        if (textFFilterNumber.getText() != null && textFFilterNumber.getText().length() > 0) {
            return true;
        }
        if (textFFilterTime.getText() != null && textFFilterTime.getText().length() > 0) {
            return true;
        }
        if (textFFilterDescription.getText() != null && textFFilterDescription.getText().length() > 0) {
            return true;
        }

        return false;
    }

    private boolean compareForFilter(Lesson lesson) {
        //logger.info(subject);
        if (textFFilterId.getText() != null && textFFilterId.getText().length() > 0) {
            if (!String.valueOf(lesson.getId()).toUpperCase().contains(textFFilterId.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterNumber.getText() != null && textFFilterNumber.getText().length() > 0) {
            if (!String.valueOf(lesson.getNumber()).toUpperCase().contains(textFFilterNumber.getText().toUpperCase
                    ())) {
                return false;
            }
        }
        if (textFFilterTime.getText() != null && textFFilterTime.getText().length() > 0) {
            if (!lesson.getTime().toUpperCase().contains(textFFilterTime.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterDescription.getText() != null && textFFilterDescription.getText().length() > 0) {
            if (!lesson.getDescription().toUpperCase().contains(textFFilterDescription.getText().toUpperCase())) {
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

    private void showLessonDetails(Lesson lesson) {
        if (lesson != null) {
            textFViewId.setText(String.valueOf(lesson.getId()));
            textFViewNumber.setText(String.valueOf(lesson.getNumber()));
            textFViewTime.setText(lesson.getTime());
            textFViewDescription.setText(lesson.getDescription());

        } else {
            textFViewId.setText("");
            textFViewNumber.setText("");
            textFViewTime.setText("");
            textFViewDescription.setText("");
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (textFEditNumber.getText() == null || textFEditNumber.getText().length() == 0) {
            errorMessage += "Невірний номер!\n";
        }
        if (textFEditTime.getText() == null || textFEditTime.getText().length() == 0) {
            errorMessage += "Невірний час!\n";
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
        Lesson newLesson = new Lesson();
        exportData.setFieldList(newLesson.getFieldList());
        exportData.setExportFileType(ExportFileType.XLSX);

        List<Object[]> objects = new ArrayList<>();

        ObservableList<Lesson> lessons;
        if (filteredData!=null ){
            lessons=filteredData;
        }
        else {
            lessons = lessonObservableList;
        }

        for (Lesson lesson : lessons) {
            objects.add(lesson.getObjects());
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
