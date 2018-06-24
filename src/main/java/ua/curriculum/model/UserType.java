package ua.curriculum.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserType {
    private IntegerProperty id;
    private StringProperty name;

    public UserType() {
    }

    public UserType(IntegerProperty id, StringProperty name) {
        this.id = id;
        this.name = name;
    }

    public UserType(int id, String name) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
    }

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserType{");
        sb.append("id=").append(id.get());
        sb.append(", name='").append(name.get()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
