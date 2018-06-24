package ua.curriculum.model;

public class GroupStudent {
    private Group group;
    private Student student;

    public GroupStudent() {
    }

    public GroupStudent(Group group, Student student) {
        this.group = group;
        this.student = student;
    }

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GroupStudent{");
        sb.append("group=").append(group);
        sb.append(", student=").append(student);
        sb.append('}');
        return sb.toString();
    }
}
