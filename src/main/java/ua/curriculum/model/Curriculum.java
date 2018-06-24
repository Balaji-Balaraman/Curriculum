package ua.curriculum.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import ua.curriculum.utils.DateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Curriculum {
    private IntegerProperty id;
    private ObjectProperty<LocalDate> date;
    private Lesson lesson;
    private LessonType lessonType;
    private Subject subject;
    private Teacher teacher;
    private Group group;
    private Classroom classroom;

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id = new SimpleIntegerProperty(id);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date= new SimpleObjectProperty<LocalDate>(date);
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Curriculum{");
        sb.append("id=").append(id);
        sb.append(", date=").append(date);
        sb.append(", lesson=").append(lesson);
        sb.append(", lessonType=").append(lessonType);
        sb.append(", subject=").append(subject);
        sb.append(", teacher=").append(teacher);
        sb.append(", group=").append(group);
        sb.append(", classroom=").append(classroom);
        sb.append('}');
        return sb.toString();
    }


    public Object[] getObjects(){
        Object[] objects = new Object[9];
        objects[0] = id.get();
        objects[1] = DateUtil.format(date.get());
        objects[2] = lesson.getNumber();
        objects[3] = lesson.getTime();
        objects[4] = lessonType.getName();
        objects[5] = subject.getName();
        objects[6] = teacher.getFullPIP();
        objects[7] = group.getName();
        objects[8] = classroom.getName();

        return objects;
    }
    public List<String> getFieldList(){
        List<String> strings = new ArrayList<>();
        strings.add("ID");
        strings.add("Дата");
        strings.add("№ уроку");
        strings.add("Час");
        strings.add("Вид");
        strings.add("Предмет");
        strings.add("Викладач");
        strings.add("Група");
        strings.add("Аудиторія");
        return strings;
    }
}
