package ua.curriculum.fx;

/**
 * Created by AnGo on 01.06.2017.
 */
public enum RecordDialogType {
    FILTER("Filter"),
    EDIT("Edit");

    private String dialogName;

    RecordDialogType(String dialogName) {
        this.dialogName = dialogName;
    }

    public String getDialogName() {
        return dialogName;
    }
}
