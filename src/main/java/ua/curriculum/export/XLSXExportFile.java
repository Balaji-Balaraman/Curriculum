package ua.curriculum.export;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class XLSXExportFile extends AbstractExportFile {
    @Override
    public void write(ExportData exportData) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        int rowNum = 0;
        if (exportData.getFieldList()!=null && !exportData.getFieldList().isEmpty()){
            int colNum = 0;
            Row row = sheet.createRow(rowNum++);
            for (String field : exportData.getFieldList()) {
                Cell cell = row.createCell(colNum++);
                setCellValue(field, cell);
            }
        }

        if (exportData != null && exportData.getTableData() != null) {
            for (Object[] objects : exportData.getTableData()) {

                Row row = sheet.createRow(rowNum++);

                int colNum = 0;
                for (Object field : objects) {

                    Cell cell = row.createCell(colNum++);
                    setCellValue(field, cell);
                }
            }
        }
        try (FileOutputStream outputStream = new FileOutputStream(exportData.getFile())) {
            workbook.write(outputStream);

            workbook.close();
        }
    }

    public void setCellValue(Object field, Cell cell) {
        if (field instanceof String) {
            cell.setCellValue((String) field);
        } else if (field instanceof Integer) {
            cell.setCellValue((Integer) field);
        }
    }

}
