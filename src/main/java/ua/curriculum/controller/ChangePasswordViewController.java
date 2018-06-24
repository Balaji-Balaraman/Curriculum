package ua.curriculum.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.curriculum.MainApp;
import ua.curriculum.dao.UserDao;
import ua.curriculum.fx.DialogText;
import ua.curriculum.fx.Dialogs;
import ua.curriculum.fx.ImageResources;
import ua.curriculum.security.Encrypt;

import java.sql.SQLException;

public class ChangePasswordViewController {
    private Logger logger = LogManager.getLogger(ChangePasswordViewController.class);

    @FXML
    private TextField passwordFPassword;
    @FXML
    private TextField passwordFNewPassword;
    @FXML
    private TextField passwordFRepeatPassword;

    @FXML
    private Button buttonOk;
    @FXML
    private Button buttonCancel;

    private MainApp mainApp;
    private Stage dialogStage;

    private UserDao userDao;

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        userDao = new UserDao(mainApp.getConnection());
    }

    @FXML
    private void initialize() {
        passwordFPassword.setText("");
        passwordFNewPassword.setText("");
        passwordFRepeatPassword.setText("");

        initButtonsIcons();
    }

    private void initButtonsIcons() {
        buttonOk.setGraphic(new ImageView(ImageResources.getButtonOk()));
        buttonCancel.setGraphic(new ImageView(ImageResources.getButtonCancel()));
    }

    public void onButtonOk(ActionEvent actionEvent) {
        try {
            if (isPasswordValid()) {
                userDao.updatePassword(mainApp.getLoginUser().getId(),
                                       Encrypt.encryptString(passwordFNewPassword.getText()));
                mainApp.getLoginUser().setPassword(Encrypt.encryptString(passwordFNewPassword.getText()));
                Dialogs.showMessage(Alert.AlertType.INFORMATION,
                                    new DialogText("Зміна паролю", "Запит на зміну паролю оброблено",
                                                   "Пароль успішно змінено"), null);
                dialogStage.close();
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            Dialogs.showErrorDialog(e, new DialogText("Зміна паролю", "Помилка в структурі бази даних",
                                                      "Неможливо змінити пароль"), logger);
        }
    }

    private boolean isPasswordValid() {
        String errorMessage = "";


        if ((passwordFPassword.getText() == null ||
             !Encrypt.encryptString(passwordFPassword.getText()).equals(mainApp.getLoginUser().getPassword())) &&
            !checkEmptyOldPass()) {
            errorMessage += "Невірний пароль!\n";
            passwordFPassword.requestFocus();
        } else if (passwordFNewPassword.getText() == null) {
            errorMessage += "Невірний новий пароль!\n";
            passwordFNewPassword.requestFocus();
        } else if (passwordFRepeatPassword.getText() == null ||
                   !passwordFNewPassword.getText().equals(passwordFRepeatPassword.getText())) {
            errorMessage += "Паролі не співпадають!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR,
                                new DialogText("Помилка зміни паролю", "Вкажіть правильні дані", errorMessage), null);
            //alert.initOwner(dialogStage);
            return false;
        }
    }

    public void onButtonCancel(ActionEvent actionEvent) {
        dialogStage.close();
    }

    private boolean checkEmptyOldPass() {
        return (mainApp.getLoginUser().getPassword() == null || mainApp.getLoginUser().getPassword().equals("")) &&
               (passwordFPassword.getText() == null || passwordFPassword.getText().equals(""));
    }

}
