package ua.curriculum.export;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ua.curriculum.fx.Dialogs;

import java.io.File;
import java.io.IOException;

public class ExportServices {
    public static void exportData(Stage stage, ExportData exportData) throws IOException {

        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter(exportData.getExportFileType().name() + " " + "файли",
                                                "*." + exportData.getExportFileType().getType().toLowerCase());

        File file = Dialogs.saveFileDialog(null, extFilter, stage);

        if (file != null) {
            exportData.setFile(file);
            System.out.println(file.getAbsoluteFile());
            AbstractExportFile writer = new FactoryExportFile().getWriter(file);
            writer.write(exportData);
        }
    }
}
