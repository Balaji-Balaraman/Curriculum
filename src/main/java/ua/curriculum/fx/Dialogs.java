package ua.curriculum.fx;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * Created by AnGo on 07.03.2017.
 */
public class Dialogs {
    /** Some tutorial and examples
     * http://code.makery.ch/blog/javafx-dialogs-official/
     */

    private static final String LABEL_STACKTRACE_TEXT = "The exception stacktrace is:";

    public static void showMessage(Alert.AlertType alertType, DialogText dialogText, Logger logger) {
        Alert alert = getAlert(alertType, dialogText);

        if (logger != null) {
            if (alertType == Alert.AlertType.ERROR) {
                logger.error(dialogText.toString());
            } else if (alertType == Alert.AlertType.WARNING) {
                logger.warn(dialogText.toString());
            } else {
                logger.info(dialogText.toString());
            }
        }

        alert.showAndWait();
    }

    private static Alert getAlert(Alert.AlertType alertType, DialogText dialogText) {
        Alert alert = new Alert(alertType);

//        alert.setGraphic(new ImageView(new Dialogs().DIALOG_ICON));

        alert.setTitle(dialogText.getTitleText());
        alert.setHeaderText(dialogText.getHeaderText());
        alert.setContentText(dialogText.getContentText());

        // Get the Stage.
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        // Add a custom icon.
        stage.getIcons().add(ImageResources.getAppIcon());

        return alert;
    }

    public static Boolean showConfirmDialog(DialogText dialogText, Logger logger) {
        Alert alert = getAlert(Alert.AlertType.CONFIRMATION, dialogText);

        Optional<ButtonType> result = alert.showAndWait();
        String logText = "";
        boolean isOk;
        if (result.get() == ButtonType.OK) {
            isOk = true;
            logText = "Confirmed: " + dialogText;

        } else {
            isOk = false;
            logText = "Canceled: " + dialogText;
        }
        if (logger != null) logger.info(logText);
        return isOk;
    }


    public static int showCustomsActionsDialog(DialogText dialogText, Logger logger) {
        Alert alert = getAlert(Alert.AlertType.CONFIRMATION, dialogText);


        ButtonType buttonTypeOne = new ButtonType("Ok");
        ButtonType buttonTypeTwo = new ButtonType("No");

        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        String logText = "";
        int res =0 ;

        if (result.get() == buttonTypeOne){
            res = 1;
            logText = "Confirmed: " + dialogText;
        } else if (result.get() == buttonTypeTwo) {
            res= 2;
            logText = "Not confirmed: " + dialogText;
        } else {
            logText = "Canceled: " + dialogText;
        }

        if (logger != null) logger.info(logText);
        return res;
    }

    public static void showErrorDialog(Exception ex, DialogText dialogText, Logger logger) {

        Alert alert = getAlert(Alert.AlertType.ERROR, dialogText);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = dialogText.getContentText() + " : " + sw.toString();

        logger.error(dialogText + "\n. Error message= \n" + sw.toString());

        Label label = new Label(LABEL_STACKTRACE_TEXT);

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public static File openFileDialog(File file, FileChooser.ExtensionFilter extFilter, Stage stage) {
        FileChooser fileChooser = new FileChooser();

        //Open directory from existing directory
        if (file != null && file.exists()) {
            File existDirectory = file.getParentFile();
            fileChooser.setInitialDirectory(existDirectory);
        }
        //Set extension filter
        fileChooser.getExtensionFilters().add(extFilter);
        //Show open file dialog, with primaryStage blocked.
        return fileChooser.showOpenDialog(stage);
    }

    public static File saveFileDialog(File file, FileChooser.ExtensionFilter extFilter, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        //Open directory from existing directory
        if (file != null && file.exists()) {
            File existDirectory = file.getParentFile();
            fileChooser.setInitialDirectory(existDirectory);
        }
        //Set extension filter
        fileChooser.getExtensionFilters().add(extFilter);
        //Show open file dialog, with primaryStage blocked.
        return fileChooser.showSaveDialog(stage);
    }

    public static File directoryChooseDialog(File file, Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (file != null && file.exists()) {
            File existDirectory = file.getParentFile();
            directoryChooser.setInitialDirectory(existDirectory);
        }
        return directoryChooser.showDialog(stage);
    }

    public static String showTextInputDialog( DialogText dialogText, String defaultText){
        TextInputDialog dialog = new TextInputDialog(defaultText);
        dialog.setTitle(dialogText.getTitleText());
        dialog.setHeaderText(dialogText.getHeaderText());
        dialog.setContentText(dialogText.getContentText());
        // Traditional way to get the response value.
        Optional<String> dialogResult = dialog.showAndWait();

        String result = "";
        if (dialogResult.isPresent()){
            result = dialogResult.get();
        }
        return result;
    }

    public static void showInfoDialog( DialogText dialogText, String link, String defaultLinkText, HostServices hostServices){
        Alert alert = getAlert(Alert.AlertType.WARNING, dialogText);

        final Hyperlink hyperlink = new Hyperlink(defaultLinkText);
        hyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hostServices.showDocument(link);
            }

        });
        alert.getDialogPane().setExpandableContent(hyperlink);

        alert.showAndWait();
    }
}
