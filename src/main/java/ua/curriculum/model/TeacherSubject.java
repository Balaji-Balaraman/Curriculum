package ua.curriculum.model;

public class TeacherSubject {
    private Teacher teacher;
    private Subject subject;

    public TeacherSubject() {
    }

    public TeacherSubject(Teacher teacher, Subject subject) {
        this.teacher = teacher;
        this.subject = subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TeacherSubject{");
        sb.append("teacher=").append(teacher);
        sb.append(", subject=").append(subject);
        sb.append('}');
        return sb.toString();
    }
}
