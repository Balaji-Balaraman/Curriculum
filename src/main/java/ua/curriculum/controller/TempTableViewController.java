package ua.curriculum.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.curriculum.MainApp;
import ua.curriculum.fx.ImageResources;

import java.sql.SQLException;


public class TempTableViewController {
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

    private MainApp mainApp;

    private Stage dialogStage;

    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
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

    }

    private void initTabPane() {
        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabTable,tabView);
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
    }


    public void onButtonNew(ActionEvent actionEvent) {
        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabEdit);
        initHBoxes(false);
    }

    public void onButtonEdit(ActionEvent actionEvent) {
        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabEdit);

        initHBoxes(false);
    }

    public void onButtonDelete(ActionEvent actionEvent) {
    }

    public void onButtonOk(ActionEvent actionEvent) {
        initTabPane();
        initHBoxes(true);
    }

    public void onButtonCancel(ActionEvent actionEvent) {
        initTabPane();
        initHBoxes(true);
    }

    public void onButtonFilter(ActionEvent actionEvent) {
        tabPane.getTabs().removeAll(tabPane.getTabs());
        tabPane.getTabs().addAll(tabFilter);
        initHBoxes(false);
    }

    public void onButtonFirst(ActionEvent actionEvent) {
        //tableGoogleDoc.getSelectionModel().selectFirst();
        //tableGoogleDoc.scrollTo(tableDocSheet.getSelectionModel().getSelectedIndex());
    }

    public void onButtonPrior(ActionEvent actionEvent) {
        //tableGoogleDoc.getSelectionModel().selectPrevious();
        //tableGoogleDoc.scrollTo(tableDocSheet.getSelectionModel().getSelectedIndex());
    }

    public void onButtonNext(ActionEvent actionEvent) {
        //tableGoogleDoc.getSelectionModel().selectNext();
        //tableGoogleDoc.scrollTo(tableDocSheet.getSelectionModel().getSelectedIndex());
    }

    public void onButtonLast(ActionEvent actionEvent) {
        //tableGoogleDoc.getSelectionModel().selectLast();
        //tableGoogleDoc.scrollTo(tableDocSheet.getSelectionModel().getSelectedIndex());
    }

    public void onButtonRefresh(ActionEvent actionEvent) {
        //tableGoogleDoc.refresh();
    }

    private void refreshTableData() throws SQLException {
//        studentData.clear();
//        tableViewStudent.getItems().removeAll(tableViewStudent.getItems());
//
//        studentData.addAll(dao.findAllData());
//        tableViewStudent.setItems(studentData);
    }
}
