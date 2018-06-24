package ua.curriculum.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private IntegerProperty id;
    private IntegerProperty number;
    private StringProperty time;
    private StringProperty description;

    public Lesson() {
    }

    public Lesson(IntegerProperty id, IntegerProperty number, StringProperty time, StringProperty description) {
        this.id = id;
        this.number = number;
        this.time = time;
        this.description = description;
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id = new SimpleIntegerProperty(id);
    }

    public int getNumber() {
        return number.get();
    }

    public IntegerProperty numberProperty() {
        return number;
    }

    public void setNumber(int number) {
        this.number = new SimpleIntegerProperty(number);
    }

    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time = new SimpleStringProperty(time);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description = new SimpleStringProperty(description);
    }


    public String getNumberWithTime(){
        return number.get() + " " + time.get();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Lesson{");
        sb.append("id=").append(id);
        sb.append(", number=").append(number);
        sb.append(", time=").append(time);
        sb.append(", description=").append(description);
        sb.append('}');
        return sb.toString();
    }

    public Object[] getObjects(){
        Object[] objects = new Object[4];
        objects[0] = id.get();
        objects[1] = number.get();
        objects[2] = time.get();
        objects[3] = description.get();

        return objects;
    }
    public List<String> getFieldList(){
        List<String> strings = new ArrayList<>();
        strings.add("ID");
        strings.add("Номер");
        strings.add("Час");
        strings.add("Опис");
        return strings;
    }
}
