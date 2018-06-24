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
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.curriculum.MainApp;
import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.fx.DialogText;
import ua.curriculum.fx.Dialogs;
import ua.curriculum.fx.ImageResources;

import java.util.List;

public class SearchFormViewController {
    private Logger logger = LogManager.getLogger(SearchFormViewController.class);

    @FXML
    private TextField textFieldFiltered;

    @FXML
    private Button buttonOk;
    @FXML
    private Button buttonCancel;

    @FXML
    private TableView<ComboBoxItem> tableViewData;

    @FXML
    private TableColumn<ComboBoxItem, String> tableColumnID;
    @FXML
    private TableColumn<ComboBoxItem, String> tableColumnName;

    private ObservableList<ComboBoxItem> searchFormData = FXCollections.observableArrayList();

    private MainApp mainApp;
    private Stage dialogStage;

    private ComboBoxItem selectedItem;

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;

        dialogStage.setOnCloseRequest(event -> {
            selectedItem = null;
        });
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public ComboBoxItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(ComboBoxItem selectedItem) {
        this.selectedItem = selectedItem;
    }

    @FXML
    private void initialize() {
        initButtonsIcons();
        initColumnValueFactoies();

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<ComboBoxItem> filteredData = new FilteredList<>(searchFormData, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        textFieldFiltered.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                // If filter text is empty, display all records.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare name and id of every records with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (item.getObjectDisplayName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (item.getObjectId().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<ComboBoxItem> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tableViewData.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tableViewData.setItems(sortedData);

        initComponentListeners();

    }

    private void initButtonsIcons() {
        buttonOk.setGraphic(new ImageView(ImageResources.getButtonOk()));
        buttonCancel.setGraphic(new ImageView(ImageResources.getButtonCancel()));
    }

    private void initColumnValueFactoies() {
        //tableColumnId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        //tableColumnName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tableColumnID.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getObjectId()));
        tableColumnName
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getObjectDisplayName()));
    }

    private void initComponentListeners() {
        tableViewData.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> setSelectedObject(newValue));
    }

    private void setSelectedObject(ComboBoxItem newValue) {
        if (newValue != null) {
            selectedItem = newValue;

        } else {
            selectedItem = null;
        }
    }

    public void onButtonOk(ActionEvent actionEvent) {
        selectedItem = tableViewData.getSelectionModel().getSelectedItem();
        if (selectedItem==null){
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Вибір запису", "Помилка вибору запису",
                                                  "Запис не вибрано"), null);
        }else {
            dialogStage.close();
        }
    }

    public void onButtonCancel(ActionEvent actionEvent) {
        selectedItem = null;
        dialogStage.close();
    }

    public void setTableDataList(List<ComboBoxItem> dataList) {
        searchFormData.clear();
        searchFormData.addAll(dataList);
    }
}
