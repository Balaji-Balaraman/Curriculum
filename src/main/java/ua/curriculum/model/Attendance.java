package ua.curriculum.model;

public class Attendance {
    private Curriculum curriculum;
    private Group group;
    private Student student;
    private AttendanceState attendanceState;

    public Curriculum getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(Curriculum curriculum) {
        this.curriculum = curriculum;
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

    public AttendanceState getAttendanceState() {
        return attendanceState;
    }

    public void setAttendanceState(AttendanceState attendanceState) {
        this.attendanceState = attendanceState;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Attendance{");
        sb.append("curriculum=").append(curriculum);
        sb.append(", group=").append(group);
        sb.append(", student=").append(student);
        sb.append(", attendanceState=").append(attendanceState);
        sb.append('}');
        return sb.toString();
    }
}
