package ua.curriculum.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.curriculum.MainApp;
import ua.curriculum.dao.CurriculumDao;
import ua.curriculum.fx.DialogText;
import ua.curriculum.fx.Dialogs;
import ua.curriculum.fx.ImageResources;
import ua.curriculum.model.Curriculum;
import ua.curriculum.utils.DateUtil;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CopyCurriculumParamsViewController {
    private static final String LABEL_FROM_FORMAT = "Копіювати з дати '%s' по дату '%s'";
    private static final String LABEL_TO_FORMAT = "Перенести з дати '%s' по дату '%s'";
    private Logger logger = LogManager.getLogger(CopyCurriculumParamsViewController.class);
    @FXML
    private TextField textFDaysCount;
    @FXML
    private DatePicker datePickerFromDate;
    @FXML
    private DatePicker datePickerToDate;

    @FXML
    private Label labelCopyFromInfo;
    @FXML
    private Label labelCopyToInfo;
    @FXML
    private Button buttonOk;
    @FXML
    private Button buttonCancel;

    private MainApp mainApp;
    private Stage dialogStage;

    private LocalDate dateFrom;

    private CurriculumDao curriculumDao;

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        curriculumDao = new CurriculumDao(mainApp.getConnection());
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
        datePickerFromDate.setValue(dateFrom);
        labelCopyFromInfo.setText(String.format(LABEL_FROM_FORMAT, DateUtil.format(datePickerFromDate.getValue()),
                                                DateUtil.format(datePickerFromDate.getValue().
                                                        plusDays(Integer.valueOf(textFDaysCount.getText()) - 1))));
    }

    @FXML
    private void initialize() {
        labelCopyFromInfo.setText("");
        labelCopyToInfo.setText("");
        textFDaysCount.setText("1");

        initButtonsIcons();
        initComponentListeners();
    }


    private void initComponentListeners() {
        //textFDaysCount.textProperty().addListener(new TextFieldNumberListener(textFDaysCount));
        textFDaysCount.textProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("")) {
                if (!newValue.matches("\\d*")) {
                    Toolkit.getDefaultToolkit().beep();
                    textFDaysCount.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
            labelCopyFromInfo.setText("");
            labelCopyToInfo.setText("");
            if (!textFDaysCount.equals("") && datePickerFromDate.getValue() != null) {
                labelCopyFromInfo.setText(
                        String.format(LABEL_FROM_FORMAT, DateUtil.format(datePickerFromDate.getValue()),
                                      DateUtil.format(datePickerFromDate.getValue().
                                              plusDays(Integer.valueOf(textFDaysCount.getText()) - 1))));
                if (datePickerToDate.getValue() != null) {
                    labelCopyToInfo.setText(String.format(LABEL_TO_FORMAT, DateUtil.format(datePickerToDate.getValue()),
                                                          DateUtil.format(datePickerToDate.getValue().plusDays(
                                                                  Integer.valueOf(textFDaysCount.getText()) - 1))));
                }
            }

        });

        datePickerToDate.valueProperty().addListener((ov, oldValue, newValue) -> {
            labelCopyToInfo.setText(DateUtil.format(newValue.plusDays(Integer.valueOf(textFDaysCount.getText()))));
            if (datePickerToDate.getValue() != null) {
                labelCopyToInfo.setText(String.format(LABEL_TO_FORMAT, DateUtil.format(datePickerToDate.getValue()),
                                                      DateUtil.format(datePickerToDate.getValue().plusDays(
                                                              Integer.valueOf(textFDaysCount.getText()) - 1))));
            }
        });
    }


    private void initButtonsIcons() {
        buttonOk.setGraphic(new ImageView(ImageResources.getButtonOk()));
        buttonCancel.setGraphic(new ImageView(ImageResources.getButtonCancel()));
    }

    public void onButtonOk(ActionEvent actionEvent) {
        /*
        try {
        */
        if (isInputValid()) {
            String resultString = "";
            int dayCount = Integer.valueOf(textFDaysCount.getText());
            LocalDate newDateFrom = datePickerFromDate.getValue();
            LocalDate newDateTo = datePickerToDate.getValue();
            for (int i = 0; i < dayCount; i++) {
                try {
                    List<Curriculum> curriculumList = curriculumDao.findAllDataOnDate(newDateFrom);
                    for (Curriculum curriculum : curriculumList) {
                        curriculum.setDate(newDateTo);
                        curriculumDao.insert(curriculum);
                    }

                    resultString +=
                            "Перенесення з '" + DateUtil.format(newDateFrom) + "' в '" + DateUtil.format(newDateTo) +
                            "': УСПІШНО\n";
                } catch (SQLException e) {
                    resultString +=
                            "Перенесення з '" + DateUtil.format(newDateFrom) + "' в '" + DateUtil.format(newDateTo) +
                            "': ПОМИЛКА!!!!\n";
                }

                newDateFrom = newDateFrom.plusDays(1);
                newDateTo = newDateTo.plusDays(1);
            }
            Dialogs.showMessage(Alert.AlertType.INFORMATION,
                                new DialogText("Копіювання розкладу", "Перенесення завершено", resultString), null);

            dialogStage.close();
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (datePickerFromDate.getValue() == null) {
            errorMessage += "Невірна дата З!\n";
            datePickerFromDate.requestFocus();
        }
        if (textFDaysCount.getText() == null || textFDaysCount.getText().length() == 0) {
            errorMessage += "Невірна кількість днів!\n";
            textFDaysCount.requestFocus();
        }
        if (datePickerToDate.getValue() == null) {
            errorMessage += "Невірний дата ПО!\n";
            datePickerToDate.requestFocus();
        }

        if (datePickerToDate.getValue() != null && datePickerFromDate.getValue() != null &&
            textFDaysCount.getText() != null && textFDaysCount.getText().length() > 0) {

            if (datePickerFromDate.getValue().compareTo(datePickerToDate.getValue()) >= 0) {
                errorMessage += "Дата ПО(" + datePickerFromDate.getValue() + ") повинна буди більша за дату З(" +
                                datePickerFromDate.getValue() + ")! \n";
            }
            LocalDate dateFromTill =
                    datePickerFromDate.getValue().plusDays(Integer.valueOf(textFDaysCount.getText()) - 1);
            if (dateFromTill.compareTo(datePickerToDate.getValue()) >= 0) {
                errorMessage += "Період З (" + DateUtil.format(datePickerFromDate.getValue()) + " - " +
                                DateUtil.format(dateFromTill) + ") перетинається з датою З (" +
                                DateUtil.format(datePickerToDate.getValue()) + ")! \n";
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR,
                                new DialogText("Помилка копіювання", "Вкажіть правильні дані", errorMessage), null);
            return false;
        }
    }

    public void onButtonCancel(ActionEvent actionEvent) {
        dialogStage.close();
    }


}
