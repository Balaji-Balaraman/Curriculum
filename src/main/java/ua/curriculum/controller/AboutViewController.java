package ua.curriculum.controller;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import ua.curriculum.MainApp;

public class AboutViewController {

    private MainApp mainApp;
    private Stage dialogStage;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    public void onButtonOk(ActionEvent actionEvent) {
        dialogStage.close();
    }
}
