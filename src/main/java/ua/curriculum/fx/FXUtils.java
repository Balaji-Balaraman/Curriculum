package ua.curriculum.fx;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import org.apache.poi.ss.formula.functions.T;
import ua.curriculum.utils.DateUtil;

import java.time.LocalDateTime;

public class FXUtils {
    public static Callback<TableColumn<T, LocalDateTime>, TableCell<T, LocalDateTime>> getTableCellLocalDateCallback(T t) {
        return column -> {
            return new TableCell<T, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(DateUtil.format(item));
                    }
                }
            };
        };
    }
}
