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

public class LoginViewController {
    private Logger logger = LogManager.getLogger(LoginViewController.class);

    @FXML
    private TextField textFLogin;
    @FXML
    private TextField passwordFPassword;

    @FXML
    private Button buttonOk;
    @FXML
    private Button buttonCancel;

    private MainApp mainApp;
    private Stage dialogStage;

    private boolean login;

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    @FXML
    private void initialize() {
        initButtonsIcons();
    }

    private void initButtonsIcons() {
        buttonOk.setGraphic(new ImageView(ImageResources.getButtonOk()));
        buttonCancel.setGraphic(new ImageView(ImageResources.getButtonCancel()));
    }

    public void onButtonOk(ActionEvent actionEvent) {
        try {
            UserDao userDao = new UserDao(mainApp.getConnection());
            mainApp.setLoginUser(userDao.findByLogin(textFLogin.getText()));
            checkLogin();
        } catch (SQLException e) {
            //e.printStackTrace();
            Dialogs.showErrorDialog(e, new DialogText("Помилка запуску додатку", "Помилка в структурі бази даних", "Неможливо знайти користувача '" + textFLogin.getText() + "'"), logger);
        }
    }

    private void checkLogin() {
        if (mainApp.getLoginUser() != null) {
            logger.info(mainApp.getLoginUser());

            if (mainApp.getLoginUser().getPassword() == null || mainApp.getLoginUser().getPassword().equals("")) {
                login = true;
                dialogStage.close();
            } else if (Encrypt.encryptString(passwordFPassword.getText()).equals(mainApp.getLoginUser().getPassword())) {

                login = true;
                dialogStage.close();
            } else {
                Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка запуску додатку", "Помилка логування", "Невірний пароль"), logger);

                passwordFPassword.requestFocus();
            }
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR, new DialogText("Помилка запуску додатку", "Помилка логування", "Невідомий користувач '" + textFLogin.getText() + "'"), logger);
            login = false;
            textFLogin.requestFocus();
        }
    }

    public void onButtonCancel(ActionEvent actionEvent) {
        login = false;
        dialogStage.close();
    }

}
