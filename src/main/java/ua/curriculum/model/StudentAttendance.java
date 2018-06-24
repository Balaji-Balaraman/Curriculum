package ua.curriculum.model;

import java.util.ArrayList;
import java.util.List;

public class StudentAttendance {
    private Group group;
    private Student student;
    private Subject subject;
    private int totalLessons;
    private int totalPresence;
    private int totalAbsences;
    private int totalByIllness;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public int getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(int totalLessons) {
        this.totalLessons = totalLessons;
    }

    public int getTotalPresence() {
        return totalPresence;
    }

    public void setTotalPresence(int totalPresence) {
        this.totalPresence = totalPresence;
    }

    public int getTotalAbsences() {
        return totalAbsences;
    }

    public void setTotalAbsences(int totalAbsences) {
        this.totalAbsences = totalAbsences;
    }

    public int getTotalByIllness() {
        return totalByIllness;
    }

    public void setTotalByIllness(int totalByIllness) {
        this.totalByIllness = totalByIllness;
    }

    public Object[] getObjects(){
        Object[] objects = new Object[7];
        objects[0] = group.getName();
        objects[1] = student.getFullPIP();
        objects[2] = subject.getName();
        objects[3] = totalLessons;
        objects[4] = totalPresence;
        objects[5] = totalAbsences;
        objects[6] = totalByIllness;
        return objects;
    }
    public List<String> getFieldList(){
        List<String> strings = new ArrayList<>();
        strings.add("Група");
        strings.add("Студент");
        strings.add("Предмет");
        strings.add("Всього занять");
        strings.add("Відвідано");
        strings.add("Пропущено");
        strings.add("За хворобою");
        return strings;
    }
}
