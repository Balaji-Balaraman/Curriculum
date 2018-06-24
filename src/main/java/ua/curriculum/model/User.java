package ua.curriculum.model;

import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

public class User {
    private IntegerProperty id;
    private StringProperty login;
    private StringProperty password;
    private UserType userType;
    private Person person;
    private StringProperty email;
    private BooleanProperty active;

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id = new SimpleIntegerProperty(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getLogin() {
        return login.get();
    }

    public void setLogin(String login) {
        this.login = new SimpleStringProperty(login);
    }

    public StringProperty loginProperty() {
        return login;
    }

    public String getPassword() {
        if (password == null) {
            return "";
        }
        return password.get();
    }

    public void setPassword(String password) {
        this.password = new SimpleStringProperty(password);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email = new SimpleStringProperty(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public boolean isActive() {
        return active.get();
    }

    public void setActive(boolean active) {
        this.active = new SimpleBooleanProperty(active);
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    //
    public StringProperty fullPIPProperty() {
        return (this.person != null) ? (new SimpleStringProperty(this.person.getFullPIP())) : new SimpleStringProperty("");
    }
    public StringProperty userTypeProperty() {
        return (this.userType != null) ? userType.nameProperty() : new SimpleStringProperty("");
    }

    // hide password print
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id.get());
        sb.append(", login=").append(login.get());
        sb.append(", userType=").append(userType);
        sb.append(", person=").append(person);
        sb.append(", email=").append(email.get());
        sb.append(", active=").append(active.get());
        sb.append('}');
        return sb.toString();
    }

    public Object[] getObjects(){
        Object[] objects = new Object[6];
        objects[0] = id.get();
        objects[1] = login.get();
        objects[2] = userType.getName();
        objects[3] = (person!=null)? person.getFullPIP():"";
        objects[4] = email.get();
        objects[5] = String.valueOf(active.get());


        return objects;
    }
    public List<String> getFieldList(){
        List<String> strings = new ArrayList<>();
        strings.add("ID");
        strings.add("Login");
        strings.add("Тип");
        strings.add("Користувач");
        strings.add("Пошта");
        strings.add("Активність");
        return strings;
    }
}
