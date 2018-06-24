package ua.curriculum.model;

import ua.curriculum.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class Student extends Person{

    public Object[] getObjects(){
        Object[] objects = new Object[5];
        objects[0] = id.get();
        objects[1] = lastName.get();
        objects[2] = firsName.get();
        objects[3] = middleName.get();
        objects[4] = DateUtil.format(birthday.get());

        return objects;
    }
    public List<String> getFieldList(){
        List<String> strings = new ArrayList<>();
        strings.add("ID");
        strings.add("Прізвище");
        strings.add("Ім’я");
        strings.add("Побатькові");
        strings.add("День народження");
        return strings;
    }

}
