package ua.curriculum.export;


import java.io.File;
import java.util.List;

public class ExportData {
    private List<String> fieldList;
    private List<Object[]> tableData;
    private File file;
    private ExportFileType exportFileType;

    public List<String> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<String> fieldList) {
        this.fieldList = fieldList;
    }

    public List<Object[]> getTableData() {
        return tableData;
    }

    public void setTableData(List<Object[]> tableData) {
        this.tableData = tableData;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ExportFileType getExportFileType() {
        return exportFileType;
    }

    public void setExportFileType(ExportFileType exportFileType) {
        this.exportFileType = exportFileType;
    }
}
