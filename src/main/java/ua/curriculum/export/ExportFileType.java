package ua.curriculum.export;

/**
 * Created by AnGo on 05.03.2017.
 */
public enum ExportFileType {
    TXT("TXT"), XLS("XLS"), XLSX("XLSX");

    private String type;

    ExportFileType(String c) {
        this.type = c;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
