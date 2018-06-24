package ua.curriculum.fx;

import javafx.scene.control.TextField;

/**
 * Created by AnGo on 25.05.2017.
 */
public class NumberTextField extends TextField {
    public NumberTextField() {
        this.setPromptText("Enter only numbers");
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if (text.matches("[0-9]") || text.isEmpty()) {
            super.replaceText(start, end, text);
        }
    }
}
