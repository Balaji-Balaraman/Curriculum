package ua.curriculum.fx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import java.awt.*;

/**
 * Created by AnGo on 31.05.2017.
 */
public class TextFieldNumberListener implements ChangeListener<String> {
    private TextField textField;

    public TextFieldNumberListener(TextField textField) {
        this.textField = textField;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (newValue != null && !newValue.equals("")) {
            if (!newValue.matches("\\d*")) {
                Toolkit.getDefaultToolkit().beep();
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        }
    }
}
