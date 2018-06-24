package ua.curriculum.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private IntegerProperty id;
    private StringProperty code;
    private Schoolyear schoolyear;
    private Speciality speciality;
    private StringProperty name;
    private StringProperty shortName;

    public Group() {
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

    public String getCode() {
        return code.get();
    }

    public void setCode(String code) {
        this.code = new SimpleStringProperty(code);
    }

    public StringProperty codeProperty() {
        return code;
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

    public Schoolyear getSchoolyear() {
        return schoolyear;
    }

    public void setSchoolyear(Schoolyear schoolyear) {
        this.schoolyear = schoolyear;
    }

    public Speciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(Speciality speciality) {
        this.speciality = speciality;
    }


    public String getCodeWithName() {
        final StringBuilder sb = new StringBuilder("");
        sb.append(code.get());
        sb.append(" ").append(name.get());
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Group{");
        sb.append("id=").append(id);
        sb.append(", code=").append(code);
        sb.append(", schoolyear=").append(schoolyear);
        sb.append(", speciality=").append(speciality);
        sb.append(", name=").append(name);
        sb.append(", shortName=").append(shortName);
        sb.append('}');
        return sb.toString();
    }


    public Object[] getObjects(){
        Object[] objects = new Object[6];
        objects[0] = id.get();
        objects[1] = code.get();
        objects[2] = schoolyear.getName();
        objects[3] = speciality.getName();
        objects[4] = name.get();
        objects[5] = shortName.get();


        return objects;
    }
    public List<String> getFieldList(){
        List<String> strings = new ArrayList<>();
        strings.add("ID");
        strings.add("Код");
        strings.add("Навчальний рік");
        strings.add("Спеціальність");
        strings.add("Назва");
        strings.add("Скорочено");
        return strings;
    }
}
