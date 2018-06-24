package ua.curriculum.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class Classroom {
    private IntegerProperty id;
    private StringProperty name;
    private StringProperty shortName;
    private StringProperty code;

    public Classroom() {
    }

    public Classroom(IntegerProperty id, StringProperty name, StringProperty shortName, StringProperty code) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.code = code;
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

    public String getCode() {
        return code.get();
    }

    public StringProperty codeProperty() {
        return code;
    }

    public void setCode(String code) {
        this.code = new SimpleStringProperty(code);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Classroom{");
        sb.append("id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", shortName=").append(shortName);
        sb.append(", code=").append(code);
        sb.append('}');
        return sb.toString();
    }

    public Object[] getObjects(){
        Object[] objects = new Object[4];
        objects[0] = id.get();
        objects[1] = name.get();
        objects[2] = shortName.get();
        objects[3] = code.get();

        return objects;
    }
    public List<String> getFieldList(){
        List<String> strings = new ArrayList<>();
        strings.add("ID");
        strings.add("Назва");
        strings.add("Скорочено");
        strings.add("Код");
        return strings;
    }

}
