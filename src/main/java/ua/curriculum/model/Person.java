package ua.curriculum.model;

import javafx.beans.property.*;

import java.time.LocalDate;

public abstract class Person {
    IntegerProperty id;
    StringProperty lastName;
    StringProperty firsName;
    StringProperty middleName;
    ObjectProperty<LocalDate> birthday;
    StringProperty address;

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id = new SimpleIntegerProperty(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName = new SimpleStringProperty(lastName);
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public String getFirsName() {
        return firsName.get();
    }

    public void setFirsName(String firsName) {
        this.firsName = new SimpleStringProperty(firsName);
    }

    public StringProperty firsNameProperty() {
        return firsName;
    }

    public String getMiddleName() {
        return middleName.get();
    }

    public void setMiddleName(String middleName) {
        this.middleName = new SimpleStringProperty(middleName);
    }

    public StringProperty middleNameProperty() {
        return middleName;
    }

    public LocalDate getBirthday() {
        return birthday.get();
    }

    public void setBirthday(LocalDate birthday) {
        //this.birthday.set(birthday);
        this.birthday = new SimpleObjectProperty<LocalDate>(birthday);
    }

    public ObjectProperty<LocalDate> birthdayProperty() {
        return birthday;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address = new SimpleStringProperty(address);
    }

    public StringProperty addressProperty() {
        return address;
    }

    public String getFullPIP() {
        final StringBuilder sb = new StringBuilder("");
        sb.append(lastName.get());
        sb.append(" ").append(firsName.get());
        sb.append(" ").append(middleName.get());
        return sb.toString();
    }
    public String getShortPIP() {

        final StringBuilder sb = new StringBuilder("");
        sb.append(lastName.get());
        sb.append(" ").append((firsName.get()).substring(0,1)).append(".");
        sb.append(" ").append((middleName.get()).substring(0,1)).append(".");
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Person{");
        sb.append("id=").append(id);
        sb.append(", lastName=").append(lastName);
        sb.append(", firsName=").append(firsName);
        sb.append(", middleName=").append(middleName);
        sb.append(", birthday=").append(birthday);
        sb.append(", address=").append(address);
        sb.append('}');
        return sb.toString();
    }
}
