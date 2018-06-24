package ua.curriculum.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.curriculum.MainApp;
import ua.curriculum.dao.StudentDao;
import ua.curriculum.dao.TeacherDao;
import ua.curriculum.dao.UserDao;
import ua.curriculum.dao.UserTypeDao;
import ua.curriculum.export.ExportData;
import ua.curriculum.export.ExportFileType;
import ua.curriculum.export.ExportServices;
import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.fx.DialogText;
import ua.curriculum.fx.Dialogs;
import ua.curriculum.fx.ImageResources;
import ua.curriculum.model.Person;
import ua.curriculum.model.Student;
import ua.curriculum.model.User;
import ua.curriculum.model.UserType;
import ua.curriculum.security.Encrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersViewController {
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
    private Button buttonShowPerson;

    @FXML
    private TableView<User> tableViewUser;
    @FXML
    private TableColumn<User, Integer> tableColumnId;
    @FXML
    private TableColumn<User, String> tableColumnLogin;
    @FXML
    private TableColumn<User, String> tableColumnUserType;
    @FXML
    private TableColumn<User, String> tableColumnPersonFullName;
    @FXML
    private TableColumn<User, String> tableColumnEmail;
    @FXML
    private TableColumn<User, Boolean> tableColumnActive;

    @FXML
    private TextField textFViewId;
    @FXML
    private TextField textFViewLogin;
    @FXML
    private TextField textFViewUserType;
    @FXML
    private TextField textFViewPerson;
    @FXML
    private TextField textFViewEmail;
    @FXML
    private TextField textFViewActive;

    @FXML
    private TextField textFEditId;
    @FXML
    private TextField textFEditLogin;
    @FXML
    private ComboBox<ComboBoxItem> comboBoxEditUserType = new ComboBox<ComboBoxItem>();
    @FXML
    private ComboBox<ComboBoxItem> comboBoxEditPerson = new ComboBox<ComboBoxItem>();

    @FXML
    private TextField textFEditEmail;
    @FXML
    private CheckBox checkBoxEditActive;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField passwordFieldRepeat;

    @FXML
    private AnchorPane anchorPaneChangePassword;
    @FXML
    private CheckBox checkBoxChangePassword;


    @FXML
    private TextField textFFilterId;
    @FXML
    private TextField textFFilterLogin;
    @FXML
    private TextField textFFilterUserType;
    @FXML
    private TextField textFFilterPerson;
    @FXML
    private TextField textFFilterEmail;
    @FXML
    private CheckBox checkBoxFilterActive;

    @FXML
    private MenuButton menuButtonReports;
    @FXML
    private MenuItem menuItemExcel;

    private UserDao dao;

    private ObservableList<User> userObservableList = FXCollections.observableArrayList();
    private FilteredList<User> filteredData;

    private ObservableList<ComboBoxItem> observableListPersons = FXCollections.observableArrayList();

    private MainApp mainApp;

    private Stage dialogStage;

    private boolean isEdited;
    private boolean isFilter;

    private String currentUserPassword;

    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        dao = new UserDao(mainApp.getConnection());

        //ObservableList<ComboBoxItem> userTypeList =FXCollections.observableArrayList();
        UserTypeDao userTypeDao = new UserTypeDao(mainApp.getConnection());
        //comboBoxUserType.setItems(userTypeDao.findAllComboBoxData());

        try {
            refreshTableData();

            ObservableList<ComboBoxItem> userTypeList = FXCollections.observableArrayList();
            List<ComboBoxItem> list = userTypeDao.findAllComboBoxData();
            userTypeList.addAll(list);

            comboBoxEditUserType.setItems(userTypeList);

        } catch (SQLException e) {
            Dialogs.showErrorDialog(e, new DialogText("Помилка отримання даних", "", "Неможливо отримати данні з таблиці 'Студенти'"), logger);
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
        checkBoxFilterActive.setIndeterminate(true);

        initButtonsIcons();
        initButtonsToolTip();

        initHBoxes(true);

        initTabPane();

        initColumnValueFactories();

        showUserDetails(null);

        initComponentListeners();

    }


    private void initComponentListeners() {
        tableViewUser.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showUserDetails(newValue));

        comboBoxEditUserType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            observableListPersons = getPersonComboBoxItems(newValue);
            comboBoxEditPerson.setItems(observableListPersons);

            buttonShowPerson.setVisible(newValue != null && !newValue.getObjectId().equals("1"));

            //filteredListPersons = new FilteredList<ComboBoxItem>(observableListPersons, p -> true);
        });
    }

    private ObservableList<ComboBoxItem> getPersonComboBoxItems(ComboBoxItem newValue) {
        ObservableList<ComboBoxItem> personList = FXCollections.observableArrayList();
        if (mainApp != null && newValue != null) {
            //ObservableList<ComboBoxItem> personList = FXCollections.observableArrayList();
            if (newValue.getObjectId().equals("2")) {
                TeacherDao teacherDao = new TeacherDao(mainApp.getConnection());
                try {
                    List<ComboBoxItem> list = teacherDao.findAllComboBoxData();
                    personList.addAll(list);
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (newValue.getObjectId().equals("3")) {
                StudentDao studentDao = new StudentDao(mainApp.getConnection());
                try {
                    List<ComboBoxItem> list = studentDao.findAllComboBoxData();
                    personList.addAll(list);
                } catch (SQLException e) {
                    logger.error(e);
                }
            }

            //comboBoxEditPerson.setItems(personList);

        } else {

        }
        return personList;
    }

    private void initColumnValueFactories() {
        tableColumnId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        tableColumnLogin.setCellValueFactory(cellData -> cellData.getValue().loginProperty());

        tableColumnUserType.setCellValueFactory(cellData -> cellData.getValue().userTypeProperty());
        tableColumnPersonFullName.setCellValueFactory(cellData -> cellData.getValue().fullPIPProperty());

        tableColumnEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        tableColumnActive.setCellValueFactory(cellData -> cellData.getValue().activeProperty());
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

        buttonShowPerson.setTooltip(new Tooltip("Показати довідник користувачів"));

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

        buttonShowPerson.setGraphic(new ImageView(ImageResources.getButtonView()));

        menuButtonReports.setGraphic(new ImageView(ImageResources.getReportIcon()));
        menuItemExcel.setGraphic(new ImageView(ImageResources.getXlsx16Icon()));
    }

    public void onButtonNew(ActionEvent actionEvent) {

        textFEditId.setText("");
        textFEditLogin.setText("");
        comboBoxEditUserType.getSelectionModel().clearSelection();
        comboBoxEditPerson.getSelectionModel().clearSelection();
        textFEditEmail.setText("");
        checkBoxEditActive.setSelected(true);

        currentUserPassword = "";
        passwordField.setText("");
        passwordFieldRepeat.setText("");

        anchorPaneChangePassword.setVisible(true);
        checkBoxChangePassword.setVisible(false);

        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabEdit);
        initHBoxes(false);

        isEdited = true;
    }

    public void onButtonEdit(ActionEvent actionEvent) {
        anchorPaneChangePassword.setVisible(false);
        checkBoxChangePassword.setVisible(true);
        checkBoxChangePassword.setSelected(false);

        int selectedIndex = tableViewUser.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            User user = tableViewUser.getItems().get(selectedIndex);
            textFEditId.setText(String.valueOf(user.getId()));
            textFEditLogin.setText(user.getLogin());

            if (user.getUserType() == null) {
                comboBoxEditUserType.getSelectionModel().clearSelection();
            } else {
                ComboBoxItem comboBoxItemUserType = new ComboBoxItem(String.valueOf(user.getUserType().getId()), user.getUserType().getName());
                comboBoxEditUserType.getSelectionModel().select(comboBoxItemUserType);
            }
            if (user.getPerson() == null) {
                comboBoxEditPerson.getSelectionModel().clearSelection();
            } else {
                ComboBoxItem comboBoxItemPerson = new ComboBoxItem(String.valueOf(user.getPerson().getId()), user.getPerson().getFullPIP());
                comboBoxEditPerson.getSelectionModel().select(comboBoxItemPerson);
            }

            textFEditEmail.setText(user.getEmail());
            checkBoxEditActive.setSelected(user.isActive());

            currentUserPassword = user.getPassword();
            passwordField.setText("");
            passwordFieldRepeat.setText("");

            tabPane.getTabs().removeAll(tabPane.getTabs());
            tabPane.getTabs().addAll(tabEdit);

            initHBoxes(false);

            isEdited = true;
        }

    }

    public void onButtonDelete(ActionEvent actionEvent) {
        int selectedIndex = tableViewUser.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            User user = tableViewUser.getItems().get(selectedIndex);
            if (Dialogs.showConfirmDialog(new DialogText("Підтвердження дії", "Бажаєте вілучити запис", "Ви дійсно бажаєте вилучити '" + user.getLogin() + "'"), logger)) {
                try {
                    if (dao.deleteById(user.getId())) {
                        tableViewUser.getItems().remove(selectedIndex);
                    }
                } catch (SQLException e) {
                    Dialogs.showErrorDialog(e, new DialogText("Помилка вилучення даних", "", "Неможливо вилучити '" + user.getLogin() + "'"), logger);
                }
            }
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка вилучення даних", "Користувач не вибраний", "Будь ласка виберіть Користувача в таблиці"), null);
        }
    }

    public void onButtonOk(ActionEvent actionEvent) {
        boolean isInsert = isInsert();
        if (isEdited) {
            if (isInputValid()) {
                User user = new User();
                user.setLogin(textFEditLogin.getText());

                ComboBoxItem comboBoxItem = comboBoxEditUserType.getValue();
                UserType userType = null;
                if (comboBoxItem != null) {
                    userType = new UserType(Integer.parseInt(comboBoxItem.getObjectId()), comboBoxItem.getObjectDisplayName());
                }
                user.setUserType(userType);

                comboBoxItem = comboBoxEditPerson.getValue();
                Person person = null;
                if (comboBoxItem != null) {
                    person = new Student();
                    person.setId(Integer.parseInt(comboBoxItem.getObjectId()));
                }
                user.setPerson(person);

                user.setEmail(textFEditEmail.getText());
                user.setActive(checkBoxEditActive.isSelected());

                user.setPassword(Encrypt.encryptString(passwordField.getText()));

                if (isInsert) {
                    try {
                        if (dao.insert(user)) {
                            refreshTableData();

                            finishEdit();
                        }
                    } catch (SQLException e) {
                        Dialogs.showErrorDialog(e, new DialogText("Помилка додавання даних", "", "Неможливо додати '" + user.getLogin() + "'"), logger);
                    }
                } else if (!isInsert) {
                    user.setId(Integer.parseInt(textFEditId.getText()));
                    try {
                        if (dao.update(user)) {
                            refreshTableData();

                            finishEdit();
                        }

                    } catch (SQLException e) {
                        Dialogs.showErrorDialog(e, new DialogText("Помилка редагування даних", "", "Неможливо змінити '" + user.getLogin() + "'"), logger);
                    }

                }
                isEdited = false;

                initTabPane();
                initHBoxes(true);
            }
        } else {
            // for filter
            //logger.info("hasFilterPattern()= " + hasFilterPattern());
            filteredData = new FilteredList<>(userObservableList, p -> true);

            if (!hasFilterPattern()) {
                filteredData.setPredicate(user -> true);
            } else {
                filteredData.setPredicate(user -> compareForFilter(user));
            }

            SortedList<User> sortedData = new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(tableViewUser.comparatorProperty());

            tableViewUser.setItems(sortedData);

            isFilter = false;

            initTabPane();
            initHBoxes(true);
        }
    }

    private boolean hasFilterPattern() {
        if (textFFilterId.getText() != null && textFFilterId.getText().length() > 0) {
            return true;
        }
        if (textFFilterLogin.getText() != null && textFFilterLogin.getText().length() > 0) {
            return true;
        }
        if (textFFilterUserType.getText() != null && textFFilterUserType.getText().length() > 0) {
            return true;
        }
        if (textFFilterPerson.getText() != null && textFFilterPerson.getText().length() > 0) {
            return true;
        }
        if (textFFilterEmail.getText() != null && textFFilterEmail.getText().length() > 0) {
            return true;
        }
        if (!checkBoxFilterActive.isIndeterminate()) {
            return true;
        }


        return false;
    }

    private boolean compareForFilter(User user) {
        //logger.info(subject);
        if (textFFilterId.getText() != null && textFFilterId.getText().length() > 0) {
            if (!String.valueOf(user.getId()).toUpperCase().contains(textFFilterId.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterLogin.getText() != null && textFFilterLogin.getText().length() > 0) {
            if (!user.getLogin().toUpperCase().contains(textFFilterLogin.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterUserType.getText() != null && textFFilterUserType.getText().length() > 0) {
            if (!user.getUserType().getName().toUpperCase().contains(textFFilterUserType.getText().toUpperCase())) {
                return false;
            }
        }
        if (textFFilterEmail.getText() != null && textFFilterEmail.getText().length() > 0) {
            if (!user.getEmail().toUpperCase().contains(textFFilterEmail.getText().toUpperCase())) {
                return false;
            }
        }
        if (!checkBoxFilterActive.isIndeterminate()) {
            if (user.isActive()!= checkBoxFilterActive.isSelected()) {
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

    private void finishEdit() {
        initTabPane();
        initHBoxes(true);
        isEdited = false;
    }

    private boolean isInsert() {
        return (textFEditId.getText() == null || textFEditId.getText().equals("")) ? true : false;
    }

    private void refreshTableData() throws SQLException {
        userObservableList.clear();
        tableViewUser.getItems().removeAll(tableViewUser.getItems());

        userObservableList.addAll(dao.findAllData());
        tableViewUser.setItems(userObservableList);
    }


    public void onButtonFirst(ActionEvent actionEvent) {
        tableViewUser.getSelectionModel().selectFirst();
        tableViewUser.scrollTo(tableViewUser.getSelectionModel().getSelectedIndex());
    }

    public void onButtonPrior(ActionEvent actionEvent) {
        tableViewUser.getSelectionModel().selectPrevious();
        tableViewUser.scrollTo(tableViewUser.getSelectionModel().getSelectedIndex());
    }

    public void onButtonNext(ActionEvent actionEvent) {
        tableViewUser.getSelectionModel().selectNext();
        tableViewUser.scrollTo(tableViewUser.getSelectionModel().getSelectedIndex());
    }

    public void onButtonLast(ActionEvent actionEvent) {
        tableViewUser.getSelectionModel().selectLast();
        tableViewUser.scrollTo(tableViewUser.getSelectionModel().getSelectedIndex());
    }

    public void onButtonRefresh(ActionEvent actionEvent) {
        tableViewUser.refresh();
    }

    private void showUserDetails(User user) {
        if (user != null) {
            textFViewId.setText(String.valueOf(user.getId()));
            textFViewLogin.setText(user.getLogin());
            textFViewUserType.setText((user.getUserType() != null) ? user.getUserType().getName() : "");
            textFViewPerson.setText((user.getPerson() != null) ? user.getPerson().getFullPIP() : "");
            textFViewEmail.setText(user.getEmail());
            textFViewActive.setText(String.valueOf(user.isActive()));

            buttonShowPerson.setVisible(user.getUserType() != null && user.getUserType().getId() != 1);
        } else {
            textFViewId.setText("");
            textFViewLogin.setText("");
            textFViewUserType.setText("");
            textFViewPerson.setText("");
            textFViewEmail.setText("");
            textFViewActive.setText("");

            buttonShowPerson.setVisible(false);
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (textFEditLogin.getText() == null || textFEditLogin.getText().length() == 0) {
            errorMessage += "Невірний Login!\n";
        } else {
            try {
                User user = dao.findByLogin(textFEditLogin.getText());
                if (user != null && !(!isInsert() && user.getId() == Integer.parseInt(textFEditId.getText()) && user.getLogin().equals(textFEditLogin.getText()))) {
                    errorMessage += "Вказаний Login існує!\n";
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (comboBoxEditUserType.getValue() == null) {
            errorMessage += "Вкажіть тип користувача!\n";
        }

        if ((comboBoxEditUserType.getValue() != null && !comboBoxEditUserType.getValue().getObjectId().equals("1")) && comboBoxEditPerson.getValue() == null) {
            errorMessage += "Не вибраний користувач!\n";
        }
        if (textFEditEmail.getText() == null || textFEditEmail.getText().length() == 0) {
            errorMessage += "Невірний email!\n";
        }

        if ((checkBoxChangePassword.isSelected() && checkBoxChangePassword.isVisible()) || isInsert()) {
            if (passwordFieldRepeat.getText() == null || passwordFieldRepeat.getText().length() == 0) {
                errorMessage += "Пароль не задано!\n";
            } else if (passwordFieldRepeat.getText() == null || passwordFieldRepeat.getText().length() == 0) {
                errorMessage += "Паролі не співпадають!\n";
            } else if (!passwordFieldRepeat.getText().equals(passwordField.getText())) {
                errorMessage += "Паролі не співпадають!\n";
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка заповнення", "Будь ласка заповнить помилкові поля", errorMessage), null);
            //alert.initOwner(dialogStage);
            return false;
        }
    }

    public void onCheckBoxChangePassword(ActionEvent actionEvent) {
        anchorPaneChangePassword.setVisible(checkBoxChangePassword.isSelected());
    }

    public void onButtonShowPerson(ActionEvent actionEvent) {
        ComboBoxItem item= mainApp.showSearchFormDialog(observableListPersons);
        if (item!=null){
            comboBoxEditPerson.getSelectionModel().select(item);
        }
    }

    public void onMenuItemExcel(ActionEvent actionEvent) {
        ExportData exportData = new ExportData();
        User newUser = new User();
        exportData.setFieldList(newUser.getFieldList());
        exportData.setExportFileType(ExportFileType.XLSX);

        List<Object[]> objects = new ArrayList<>();

        ObservableList<User> users;
        if (filteredData!=null ){
            users=filteredData;
        }
        else {
            users = userObservableList;
        }

        for (User user : users) {
            objects.add(user.getObjects());
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
