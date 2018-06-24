package ua.curriculum.fx;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class FilterComboBox extends ComboBox<ComboBoxItem> {
        private ObservableList<ComboBoxItem> initialList;
        private ObservableList<ComboBoxItem> bufferList = FXCollections.observableArrayList();
        private ComboBoxItem previousValue = new ComboBoxItem();

        public FilterComboBox(ObservableList<ComboBoxItem> items) {
            super(items);
            super.setEditable(true);
            this.initialList = items;

            this.configAutoFilterListener();
        }

        private void configAutoFilterListener() {
            final FilterComboBox currentInstance = this;
            this.getEditor().textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    previousValue.setObjectDisplayName(oldValue);
                    final TextField editor = currentInstance.getEditor();
                    final ComboBoxItem selected = currentInstance.getSelectionModel().getSelectedItem();

                    if (selected == null || !selected.equals(editor.getText())) {
                        filterItems(newValue, currentInstance);

                        currentInstance.show();
                        if (currentInstance.getItems().size() == 1) {
                            setUserInputToOnlyOption(currentInstance, editor);
                        }
                    }
                }
            });
        }

        private void filterItems(String filter, ComboBox<ComboBoxItem> comboBox) {
            if (filter.startsWith(previousValue.getObjectDisplayName()) && !previousValue.getObjectDisplayName().isEmpty()) {
                ObservableList<ComboBoxItem> filteredList = this.readFromList(filter, bufferList);
                bufferList.clear();
                bufferList = filteredList;
            } else {
                bufferList = this.readFromList(filter, initialList);
            }
            comboBox.setItems(bufferList);
        }

        private ObservableList<ComboBoxItem> readFromList(String filter, ObservableList<ComboBoxItem> originalList) {
            ObservableList<ComboBoxItem> filteredList = FXCollections.observableArrayList();
            for (ComboBoxItem item : originalList) {
                if (item.getObjectDisplayName().toLowerCase().startsWith(filter.toLowerCase())) {
                    filteredList.add(item);
                }
            }

            return filteredList;
        }

        private void setUserInputToOnlyOption(ComboBox<ComboBoxItem> currentInstance, final TextField editor) {
            final ComboBoxItem onlyOption = currentInstance.getItems().get(0);
            final String currentText = editor.getText();
            if (onlyOption.getObjectDisplayName().length() > currentText.length()) {
                editor.setText(onlyOption.getObjectDisplayName());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        editor.selectRange(currentText.length(), onlyOption.getObjectDisplayName().length());
                    }
                });
            }
        }
    }

