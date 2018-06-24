package ua.curriculum.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AttendanceState {
    private IntegerProperty id;
    private StringProperty name;
    private StringProperty shortName;

    public AttendanceState() {
    }

    public AttendanceState(IntegerProperty id, StringProperty name, StringProperty shortName) {
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
        this.name= new SimpleStringProperty(name);
    }

    public String getShortName() {
        return shortName.get();
    }

    public StringProperty shortNameProperty() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName= new SimpleStringProperty(shortName);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AttendanceState{");
        sb.append("id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", shortName=").append(shortName);
        sb.append('}');
        return sb.toString();
    }
}
