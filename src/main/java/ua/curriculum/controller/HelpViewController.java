package ua.curriculum.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.curriculum.MainApp;
import ua.curriculum.utils.FileUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class HelpViewController {

    private Logger logger = LogManager.getLogger(this.getClass());
    @FXML
    private ScrollPane scrollPane = new ScrollPane();
    private final WebView webView =new WebView();
    private final WebEngine webEngine = webView.getEngine();

    private MainApp mainApp;
    private Stage dialogStage;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void initialize() {

        scrollPane.setContent(webView);

        File helpFile = FileUtils.getFileWithName(MainApp.class, "/help/Help.htm");
        URL urlHelp = null;
        try {
            urlHelp = helpFile.toURI().toURL();
            webEngine.load(urlHelp.toExternalForm());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
