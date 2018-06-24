package ua.curriculum.model;

import javafx.beans.property.*;
import ua.curriculum.utils.DateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Schoolyear {
    private IntegerProperty id;
    private StringProperty name;
    private StringProperty shortName;
    private ObjectProperty<LocalDate> dateFrom;
    private ObjectProperty<LocalDate> dateTill;
    private StringProperty description;

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id = new SimpleIntegerProperty(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getShortName() {
        return shortName.get();
    }

    public void setShortName(String shortName) {
        this.shortName = new SimpleStringProperty(shortName);
    }

    public StringProperty shortNameProperty() {
        return shortName;
    }

    public LocalDate getDateFrom() {
        return dateFrom.get();
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = new SimpleObjectProperty<LocalDate>(dateFrom);
    }

    public ObjectProperty<LocalDate> dateFromProperty() {
        return dateFrom;
    }

    public LocalDate getDateTill() {
        return dateTill.get();
    }

    public void setDateTill(LocalDate dateTill) {
        this.dateTill = new SimpleObjectProperty<LocalDate>(dateTill);
    }

    public ObjectProperty<LocalDate> dateTillProperty() {
        return dateTill;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description = new SimpleStringProperty(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Schoolyear{");
        sb.append("id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", shortName=").append(shortName);
        sb.append(", dateFrom=").append(dateFrom);
        sb.append(", dateTill=").append(dateTill);
        sb.append(", description=").append(description);
        sb.append('}');
        return sb.toString();
    }


    public Object[] getObjects(){
        Object[] objects = new Object[6];
        objects[0] = id.get();
        objects[1] = name.get();
        objects[2] = shortName.get();
        objects[3] = DateUtil.format(dateFrom.get());
        objects[4] = DateUtil.format(dateTill.get());
        objects[5] = description.get();

        return objects;
    }
    public List<String> getFieldList(){
        List<String> strings = new ArrayList<>();
        strings.add("ID");
        strings.add("Назва");
        strings.add("Скорочено");
        strings.add("Дата початку");
        strings.add("Дата закінчення");
        strings.add("Опис");
        return strings;
    }
}
