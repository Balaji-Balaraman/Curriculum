package ua.curriculum.export;

import org.apache.logging.log4j.core.util.FileUtils;

import java.io.File;

public class FactoryExportFile {
    public AbstractExportFile getWriter(File file) {
        if (file == null) {
            return null;
        }
        AbstractExportFile writer = null;
        //System.out.println(FileUtils.getFileExtension(file).toUpperCase().equals(ExportFileType.TXT));
        //        if (FileUtils.getFileExtension(file).toUpperCase().equals(ExportFileType.TXT.toString())) {
        //            writer = new TXTExportFile();
        //        } else if (FileUtils.getFileExtension(file).toUpperCase().equals(ExportFileType.XLS.toString())) {
        //            writer = new XLSExportFile();
        //        } else

        if (FileUtils.getFileExtension(file).toUpperCase().equals(ExportFileType.XLSX.toString())) {
            writer = new XLSXExportFile();
        } else {

        }
        return writer;
    }
}
