package ua.curriculum.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ua.curriculum.utils.DateUtil;


import java.util.ArrayList;

import java.util.List;

public class Teacher extends Person{
    private StringProperty position;

    public String getPosition() {
        return position.get();
    }

    public StringProperty positionProperty() {
        return position;
    }

    public void setPosition(String position) {
        this.position= new SimpleStringProperty(position);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Teacher{");
        sb.append("id=").append(id);
        sb.append(", lastName=").append(lastName);
        sb.append(", firsName=").append(firsName);
        sb.append(", middleName=").append(middleName);
        sb.append(", birthday=").append(birthday);
        sb.append(", position=").append(position);
        sb.append(", address=").append(address);
        sb.append('}');
        return sb.toString();
    }


    public Object[] getObjects(){
        Object[] objects = new Object[6];
        objects[0] = id.get();
        objects[1] = lastName.get();
        objects[2] = firsName.get();
        objects[3] = middleName.get();
        objects[4] = DateUtil.format(birthday.get());
        objects[5] = position.get();

        return objects;
    }
    public List<String> getFieldList(){
        List<String> strings = new ArrayList<>();
        strings.add("ID");
        strings.add("Прізвище");
        strings.add("Ім’я");
        strings.add("Побатькові");
        strings.add("День народження");
        strings.add("Посада");
        return strings;
    }
}
