package ua.curriculum.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class LessonType {
    private IntegerProperty id;
    private StringProperty name;
    private StringProperty shortName;

    public LessonType() {
    }

    public LessonType(IntegerProperty id, StringProperty name, StringProperty shortName) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
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

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public String getShortName() {
        return shortName.get();
    }

    public StringProperty shortNameProperty() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = new SimpleStringProperty(shortName);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LessonType{");
        sb.append("id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", shortName=").append(shortName);
        sb.append('}');
        return sb.toString();
    }


    public Object[] getObjects(){
        Object[] objects = new Object[3];
        objects[0] = id.get();
        objects[1] = name.get();
        objects[2] = shortName.get();

        return objects;
    }
    public List<String> getFieldList(){
        List<String> strings = new ArrayList<>();
        strings.add("ID");
        strings.add("Назва");
        strings.add("Скорочено");
        return strings;
    }
}
