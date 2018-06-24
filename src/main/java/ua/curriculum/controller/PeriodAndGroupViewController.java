package ua.curriculum.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.curriculum.MainApp;
import ua.curriculum.dao.GroupDao;
import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.fx.DialogText;
import ua.curriculum.fx.Dialogs;
import ua.curriculum.fx.ImageResources;
import ua.curriculum.utils.DateUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PeriodAndGroupViewController {

    private Logger logger = LogManager.getLogger(this.getClass());


    @FXML
    private Button buttonShowGroups;

    @FXML
    private DatePicker datePickerDateFrom;
    @FXML
    private DatePicker datePickerDateTill;

    @FXML
    private ComboBox<ComboBoxItem> comboBoxGroup = new ComboBox<>();

    GroupDao groupDao;

    private ObservableList<ComboBoxItem> observableListGroups = FXCollections.observableArrayList();

    private MainApp mainApp;
    private Stage dialogStage;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        groupDao = new GroupDao(mainApp.getConnection());
        try {
            fillGroupsComboBoxOnDate(datePickerDateFrom.getValue(), datePickerDateTill.getValue());
        } catch (SQLException e) {
            Dialogs.showErrorDialog(e, new DialogText("Помилка отримання даних", "",
                                                      "Неможливо отримати данні з таблиці 'Групи'"), logger);
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    private void initButtonsToolTip() {
        buttonShowGroups.setTooltip(new Tooltip("Показати довідник груп"));
    }

    private void initButtonsIcons() {
        buttonShowGroups.setGraphic(new ImageView(ImageResources.getButtonView()));
    }

    @FXML
    private void initialize() {
        initButtonsIcons();

        initButtonsToolTip();

        datePickerDateFrom.setValue(DateUtil.getLocalDate(new Date()));
        datePickerDateTill.setValue(DateUtil.getLocalDate(new Date()));

    }

    public void initDates(LocalDate dateFrom, LocalDate dateTill){
        datePickerDateFrom.setValue(dateFrom);
        datePickerDateTill.setValue(dateTill);
    }

    private void fillGroupsComboBoxOnDate(LocalDate localDateFrom, LocalDate localDateTill) throws SQLException {
        observableListGroups.clear();

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

    public void onButtonOk(ActionEvent actionEvent) {
        if (isInputValid()) {
            //PreparedStatement preparedStatement = null;

            try {
                // First, compile jrxml file.
                JasperReport jasperReport = JasperCompileManager.compileReport(MainApp.class.getResourceAsStream("/jasperReports/Curriculum.jrxml"));
                // Parameters for report
                Map<String, Object> parameters = new HashMap<String, Object>();
                //            parameters.put("paramDateFrom", new java.sql.Date(
                //                    DateUtil.getDateFromLocalDate(datePickerSelectedDate.getValue()).getTime()));
                //            parameters.put("paramDateTill",
                //                    DateUtil.getDateFromLocalDate(datePickerSelectedDate.getValue()).getTime());
                //logger.info("Date from: " + (datePickerDateFrom.getValue()).format(DateUtil.getDateTimeFormatter()));
                parameters.put("paramGroup", Integer.valueOf(comboBoxGroup.getValue().getObjectId()));
                parameters.put("p_From", (datePickerDateFrom.getValue()).format(DateUtil.getDateTimeFormatter()));
                //logger.info("Date till: " + (datePickerDateTill.getValue()).format(DateUtil.getDateTimeFormatter()));
                parameters.put("p_Till", (datePickerDateTill.getValue()).format(DateUtil.getDateTimeFormatter()));

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, mainApp.getConnection());

                JasperViewer jasperViewer = new JasperViewer(jasperPrint);

                jasperViewer.viewReport(jasperPrint, false);
            } catch (JRException e) {
                Dialogs.showErrorDialog(e, new DialogText("Експорт даних", "Файл не збережено", e.getMessage()), logger);
            }
            dialogStage.close();
        }
    }


    public void onButtonShowGroups(ActionEvent actionEvent) {

            ComboBoxItem item = mainApp.showSearchFormDialog(observableListGroups);
            if (item!=null){
                comboBoxGroup.getSelectionModel().select(item);
            }
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (datePickerDateFrom.getValue() == null) {
            errorMessage += "Невірна дата З!\n";
            datePickerDateFrom.requestFocus();
        }
        if (datePickerDateTill.getValue() == null) {
            errorMessage += "Невірна дата ПО!\n";
            datePickerDateTill.requestFocus();
        }

        if (comboBoxGroup.getValue() == null) {
            errorMessage += "Вкажіть групу!\n";
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
}
