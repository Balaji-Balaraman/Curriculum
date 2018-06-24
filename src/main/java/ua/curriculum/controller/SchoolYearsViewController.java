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
import ua.curriculum.dao.SchoolYearDao;
import ua.curriculum.export.ExportData;
import ua.curriculum.export.ExportFileType;
import ua.curriculum.export.ExportServices;
import ua.curriculum.fx.DialogText;
import ua.curriculum.fx.Dialogs;
import ua.curriculum.fx.ImageResources;
import ua.curriculum.model.Schoolyear;
import ua.curriculum.utils.DateUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SchoolYearsViewController {
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
    private TableView<Schoolyear> tableViewSchoolyear;

    @FXML
    private TableColumn<Schoolyear, Integer> tableColumnId;
    @FXML
    private TableColumn<Schoolyear, String> tableColumnFullName;
    @FXML
    private TableColumn<Schoolyear, String> tableColumnShortName;
    @FXML
    private TableColumn<Schoolyear, LocalDate> tableColumnDateFrom;
    @FXML
    private TableColumn<Schoolyear, LocalDate> tableColumnDateTill;
    @FXML
    private TableColumn<Schoolyear, String> tableColumnDescription;

    @FXML
    private TextField textFViewId;
    @FXML
    private TextField textFViewFullName;
    @FXML
    private TextField textFViewShortName;
    @FXML
    private TextField textFViewDateFrom;
    @FXML
    private TextField textFViewDateTill;
    @FXML
    private TextField textFViewDescription;

    @FXML
    private TextField textFEditId;
    @FXML
    private TextField textFEditFullName;
    @FXML
    private TextField textFEditShortName;
    @FXML
    private DatePicker dataPickerEditDateFrom;
    @FXML
    private DatePicker dataPickerEditDateTill;
    @FXML
    private TextField textFEditDescription;


    @FXML
    private TextField textFFilterId;
    @FXML
    private TextField textFFilterFullName;
    @FXML
    private TextField textFFilterShortName;
    @FXML
    private TextField textFFilterDescription;
    @FXML
    private DatePicker dataPickerFilterDateFromFrom;
    @FXML
    private DatePicker dataPickerFilterDateFromTill;
    @FXML
    private DatePicker dataPickerFilterDateTillFrom;
    @FXML
    private DatePicker dataPickerFilterDateTillTill;

    @FXML
    private MenuButton menuButtonReports;
    @FXML
    private MenuItem menuItemExcel;

    private SchoolYearDao dao;

    private ObservableList<Schoolyear> schoolyearObservableList = FXCollections.observableArrayList();
    private FilteredList<Schoolyear> filteredData;

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

        dao = new SchoolYearDao(mainApp.getConnection());
        try {
            refreshTableData();

        } catch (SQLException e) {
            Dialogs.showErrorDialog(e, new DialogText("Помилка отримання даних", "",
                                                      "Неможливо отримати данні з таблиці 'Навчальні роки'"), logger);
        }
    }

    private void refreshTableData() throws SQLException {
        schoolyearObservableList.clear();
        tableViewSchoolyear.getItems().removeAll(tableViewSchoolyear.getItems());

        schoolyearObservableList.addAll(dao.findAllData());
        tableViewSchoolyear.setItems(schoolyearObservableList);
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

        showSchoolyearDetails(null);

        initComponentListeners();
    }

    private void initComponentListeners() {
        tableViewSchoolyear.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showSchoolyearDetails(newValue));
    }

    private void initColumnValueFactories() {
        tableColumnId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        tableColumnFullName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tableColumnShortName.setCellValueFactory(cellData -> cellData.getValue().shortNameProperty());
        tableColumnDateFrom.setCellValueFactory(cellData -> cellData.getValue().dateFromProperty());
        tableColumnDateTill.setCellValueFactory(cellData -> cellData.getValue().dateTillProperty());
        tableColumnDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        //tableColumnDateFrom.setCellFactory(FXUtils.getTableCellLocalDateCallback(tableViewSchoolyear));
        tableColumnDateFrom.setCellFactory(getTableCellDateCallback());
        tableColumnDateTill.setCellFactory(getTableCellDateCallback());
    }

    private Callback<TableColumn<Schoolyear, LocalDate>, TableCell<Schoolyear, LocalDate>> getTableCellDateCallback() {
        return column -> {
            return new TableCell<Schoolyear, LocalDate>() {
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
        textFEditFullName.setText("");
        textFEditShortName.setText("");
        dataPickerEditDateFrom.getEditor().clear();
        dataPickerEditDateTill.getEditor().clear();
        textFEditDescription.setText("");

        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabEdit);
        initHBoxes(false);

        isEdited = true;

    }

    public void onButtonEdit(ActionEvent actionEvent) {
        int selectedIndex = tableViewSchoolyear.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Schoolyear schoolyear = tableViewSchoolyear.getItems().get(selectedIndex);
            textFEditId.setText(String.valueOf(schoolyear.getId()));
            textFEditFullName.setText(schoolyear.getName());
            textFEditShortName.setText(schoolyear.getShortName());
            dataPickerEditDateFrom.setValue(schoolyear.getDateFrom());
            dataPickerEditDateTill.setValue(schoolyear.getDateTill());
            textFEditDescription.setText(schoolyear.getDescription());

            tabPane.getTabs().removeAll(tabPane.getTabs());
            tabPane.getTabs().addAll(tabEdit);

            initHBoxes(false);

            isEdited = true;
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка редагування даних", "Запис не вибраний",
                                                                      "Будь ласка виберіть запис в таблиці"), null);
        }
    }

    public void onButtonDelete(ActionEvent actionEvent) {
        int selectedIndex = tableViewSchoolyear.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Schoolyear schoolyear = tableViewSchoolyear.getItems().get(selectedIndex);
            if (Dialogs.showConfirmDialog(new DialogText("Підтвердження дії", "Бажаєте вілучити запис",
                                                         "Ви дійсно бажаєте вилучити '" + schoolyear.getName() + "'"),
                                          logger)) {
                //int selectedIndex = tableViewStudent.getSelectionModel().getSelectedIndex();
                //Student student = tableViewStudent.getItems().get(selectedIndex);
                try {
                    if (dao.deleteById(schoolyear.getId())) {
                        tableViewSchoolyear.getItems().remove(selectedIndex);
                    }
                } catch (SQLException e) {
                    Dialogs.showErrorDialog(e, new DialogText("Помилка вилучення даних", "",
                                                              "Неможливо вилучити '" + schoolyear.getName() + "'"),
                                            logger);
                }
            }
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка вилучення даних", "Запис не вибраний",
                                                                      "Будь ласка виберіть запис в таблиці"), null);
        }
    }

    public void onButtonOk(ActionEvent actionEvent) {
        boolean isInsert = (textFEditId.getText() == null || textFEditId.getText().equals("")) ? true : false;
        if (isEdited) {
            if (isInputValid()) {
                Schoolyear schoolyear = new Schoolyear();
                schoolyear.setName(textFEditFullName.getText());
                schoolyear.setShortName(textFEditShortName.getText());
                schoolyear.setDateFrom(dataPickerEditDateFrom.getValue());
                schoolyear.setDateTill(dataPickerEditDateTill.getValue());
                schoolyear.setDescription(textFEditDescription.getText());

                if (isInsert) {
                    try {
                        if (dao.insert(schoolyear)) {
                            refreshTableData();
                        }
                    } catch (SQLException e) {
                        Dialogs.showErrorDialog(e, new DialogText("Помилка додавання даних", "",
                                                                  "Неможливо додати '" + schoolyear.getName() + "'"),
                                                logger);
                    }
                } else if (!isInsert) {
                    schoolyear.setId(Integer.parseInt(textFEditId.getText()));
                    try {
                        if (dao.update(schoolyear)) {
                            refreshTableData();
                        }
                    } catch (SQLException e) {
                        Dialogs.showErrorDialog(e, new DialogText("Помилка редагування даних", "",
                                                                  "Неможливо змінити '" + schoolyear.getName() + "'"),
                                                logger);
                    }

                }

                initTabPane();
                initHBoxes(true);

                isEdited = false;
            }
        } else {
            // for filter
            //logger.info("hasFilterPattern()= " + hasFilterPattern());

            filteredData = new FilteredList<>(schoolyearObservableList, p -> true);

            if (!hasFilterPattern()) {
                filteredData.setPredicate(schoolyear -> true);
            } else {
                filteredData.setPredicate(schoolyear -> compareForFilter(schoolyear));
            }

            SortedList<Schoolyear> sortedData = new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(tableViewSchoolyear.comparatorProperty());

            tableViewSchoolyear.setItems(sortedData);


            isFilter = false;

            initTabPane();
            initHBoxes(true);
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

        if (dataPickerFilterDateFromFrom.getValue() != null) {
            return true;
        }
        if (dataPickerFilterDateFromTill.getValue() != null) {
            return true;
        }
        if (dataPickerFilterDateTillFrom.getValue() != null) {
            return true;
        }
        if (dataPickerFilterDateTillTill.getValue() != null) {
            return true;
        }

        if (textFFilterDescription.getText() != null && textFFilterDescription.getText().length() > 0) {
            return true;
        }

        return false;
    }

    private boolean compareForFilter(Schoolyear schoolyear) {
        //logger.info(student);
        if (textFFilterId.getText() != null && textFFilterId.getText().length() > 0) {
            if (!String.valueOf(schoolyear.getId()).toUpperCase().contains(textFFilterId.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterFullName.getText() != null && textFFilterFullName.getText().length() > 0) {
            if (!schoolyear.getName().toUpperCase().contains(textFFilterFullName.getText().toUpperCase())) {

                return false;
            }
        }
        if (textFFilterShortName.getText() != null && textFFilterShortName.getText().length() > 0) {
            if (!schoolyear.getShortName().toUpperCase().contains(textFFilterShortName.getText().toUpperCase())) {
                return false;
            }
        }

        if (dataPickerFilterDateFromFrom.getValue() != null) {
            if (schoolyear.getDateFrom().compareTo(dataPickerFilterDateFromFrom.getValue()) < 0) {
                return false;
            }
        }
        if (dataPickerFilterDateFromTill.getValue() != null) {
            if (schoolyear.getDateFrom().compareTo(dataPickerFilterDateFromTill.getValue()) > 0) {
                return false;
            }
        }

        if (dataPickerFilterDateTillFrom.getValue() != null) {
            if (schoolyear.getDateTill().compareTo(dataPickerFilterDateTillFrom.getValue()) < 0) {
                return false;
            }
        }

        if (dataPickerFilterDateTillTill.getValue() != null) {
            if (schoolyear.getDateTill().compareTo(dataPickerFilterDateTillTill.getValue()) > 0) {
                return false;
            }
        }


        if (textFFilterDescription.getText() != null && textFFilterDescription.getText().length() > 0) {
            if (!schoolyear.getDescription().toUpperCase().contains(textFFilterDescription.getText().toUpperCase())) {
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
        tableViewSchoolyear.getSelectionModel().selectFirst();
        tableViewSchoolyear.scrollTo(tableViewSchoolyear.getSelectionModel().getSelectedIndex());
    }

    public void onButtonPrior(ActionEvent actionEvent) {
        tableViewSchoolyear.getSelectionModel().selectPrevious();
        tableViewSchoolyear.scrollTo(tableViewSchoolyear.getSelectionModel().getSelectedIndex());
    }

    public void onButtonNext(ActionEvent actionEvent) {
        tableViewSchoolyear.getSelectionModel().selectNext();
        tableViewSchoolyear.scrollTo(tableViewSchoolyear.getSelectionModel().getSelectedIndex());
    }

    public void onButtonLast(ActionEvent actionEvent) {
        tableViewSchoolyear.getSelectionModel().selectLast();
        tableViewSchoolyear.scrollTo(tableViewSchoolyear.getSelectionModel().getSelectedIndex());
    }

    public void onButtonRefresh(ActionEvent actionEvent) {
        tableViewSchoolyear.refresh();
    }

    private void showSchoolyearDetails(Schoolyear schoolyear) {

        if (schoolyear != null) {
            textFViewId.setText(String.valueOf(schoolyear.getId()));
            textFViewFullName.setText(schoolyear.getName());
            textFViewShortName.setText(schoolyear.getShortName());
            textFViewDateFrom.setText(DateUtil.format(schoolyear.getDateFrom()));
            textFViewDateTill.setText(DateUtil.format(schoolyear.getDateTill()));
            textFViewDescription.setText(schoolyear.getDescription());

        } else {
            textFViewId.setText("");
            textFViewFullName.setText("");
            textFViewShortName.setText("");
            textFViewDateFrom.setText("");
            textFViewDateTill.setText("");
            textFViewDescription.setText("");
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (textFEditFullName.getText() == null || textFEditFullName.getText().length() == 0) {
            errorMessage += "Невірна назва!\n";
        }
        if (textFEditShortName.getText() == null || textFEditShortName.getText().length() == 0) {
            errorMessage += "Невірне скорочена назва!\n";
        }
        if (dataPickerEditDateFrom.getValue() == null) {
            errorMessage += "Невірна дата початку!\n";
        }
        if (dataPickerEditDateTill.getValue() == null) {
            errorMessage += "Невірна дата закінчення!\n";
        }

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

    public void onMenuItemExcel(ActionEvent actionEvent) {
        ExportData exportData = new ExportData();
        Schoolyear newSchoolyear = new Schoolyear();
        exportData.setFieldList(newSchoolyear.getFieldList());
        exportData.setExportFileType(ExportFileType.XLSX);

        List<Object[]> objects = new ArrayList<>();

        ObservableList<Schoolyear> schoolyears;
        if (filteredData!=null ){
            schoolyears=filteredData;
        }
        else {
            schoolyears = schoolyearObservableList;
        }

        for (Schoolyear schoolyear : schoolyears) {
            objects.add(schoolyear.getObjects());
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
