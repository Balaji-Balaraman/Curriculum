package ua.curriculum.dao;

import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.model.Attendance;
import ua.curriculum.model.Curriculum;
import ua.curriculum.model.StudentAttendance;
import ua.curriculum.utils.DateUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDao implements TableDateDao<Attendance> {

    public static final String SELECT_ALL_FROM_ATTENDANCE = "SELECT * FROM Attendance";
    public static final String DELETE_ATTENDANCE_BY_ID = "DELETE FROM Attendance WHERE curriculum_id = ?";
    public static final String INSERT_INTO_ATTENDANCE =
            "INSERT INTO Attendance(curriculum_id, group_id, student_id, attendance_state_id) VALUES (?, ?, ?, ?)";
    public static final String UPDATE_ATTENDANCE =
            "UPDATE Attendance SET attendance_state_id =? WHERE curriculum_id =? and group_id =? and student_id =?";
    public static final String UPDATE_ATTENDANCE_STATE =
            "UPDATE Attendance SET attendance_state_id =? WHERE curriculum_id =? and group_id =? and student_id =?";
    private Connection connection;

    public AttendanceDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Attendance findById(Integer id) throws SQLException {
        PreparedStatement preparedStatement =
                connection.prepareStatement(SELECT_ALL_FROM_ATTENDANCE + " WHERE curriculum_id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        Attendance attendance = null;
        if (resultSet.next()) {
            attendance = getAttendanceFromResult(resultSet);
        }
        return attendance;
    }

    public Attendance findByCurriculum(Curriculum curriculum) throws SQLException {
        PreparedStatement preparedStatement =
                connection.prepareStatement(SELECT_ALL_FROM_ATTENDANCE + " WHERE curriculum_id= ?");
        preparedStatement.setInt(1, curriculum.getId());
        ResultSet resultSet = preparedStatement.executeQuery();

        Attendance attendance = null;
        if (resultSet.next()) {
            attendance = getAttendanceFromResult(resultSet);
        }
        return attendance;
    }

    @Override
    public boolean insert(Attendance attendance) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_ATTENDANCE);
        preparedStatement.setInt(1, attendance.getCurriculum().getId());
        preparedStatement.setInt(2, attendance.getGroup().getId());
        preparedStatement.setInt(3, attendance.getStudent().getId());
        preparedStatement.setInt(4, attendance.getAttendanceState().getId());

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    public boolean insertAllByCurriculumID(int id) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into attendance(curriculum_id, group_id, student_id, attendance_state_id) \n" +
                "SELECT qs.curriculum_id, qs.group_id, qs.student_id, 1 AS attendance_state_id\n" +
                "FROM QStudents_For_Curriculum AS qs WHERE qs.curriculum_id= ?");
        preparedStatement.setInt(1, id);

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    public boolean insertStudentByGroupID(int groupId, int studentId) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into attendance(curriculum_id, group_id, student_id, attendance_state_id) \n" +
                "SELECT qs.id, qs.group_id, ?, 1 AS attendance_state_id\n" +
                "FROM Curriculum AS qs WHERE qs.group_id= ?");
        preparedStatement.setInt(1, studentId);
        preparedStatement.setInt(2, groupId);

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Attendance attendance) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ATTENDANCE);
        preparedStatement.setInt(1, attendance.getAttendanceState().getId());
        preparedStatement.setInt(2, attendance.getCurriculum().getId());
        preparedStatement.setInt(3, attendance.getGroup().getId());
        preparedStatement.setInt(4, attendance.getStudent().getId());

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    public boolean updateState(Attendance attendance, int newSateId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ATTENDANCE_STATE);
        preparedStatement.setInt(1, newSateId);
        preparedStatement.setInt(2, attendance.getCurriculum().getId());
        preparedStatement.setInt(3, attendance.getGroup().getId());
        preparedStatement.setInt(4, attendance.getStudent().getId());

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) throws SQLException {
        return false;
    }

    public boolean deleteAllByCurriculum(Curriculum curriculum) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ATTENDANCE_BY_ID);
        preparedStatement.setInt(1, curriculum.getId());

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    public boolean deleteStudentByGroup(int groupId, int studentId) throws SQLException {

        PreparedStatement preparedStatement =
                connection.prepareStatement("DELETE FROM Attendance WHERE group_id =? and student_id = ?");
        preparedStatement.setInt(1, groupId);
        preparedStatement.setInt(2, studentId);

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }


    @Override
    public List<Attendance> findAllData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_FROM_ATTENDANCE);
        List<Attendance> attendanceList = new ArrayList<>();
        while (resultSet.next()) {
            Attendance attendance = getAttendanceFromResult(resultSet);
            attendanceList.add(attendance);
        }
        statement.close();
        return attendanceList;
    }

    public List<Attendance> findAllDataByCurriculum(Curriculum curriculum) throws SQLException {
        PreparedStatement preparedStatement =
                connection.prepareStatement(SELECT_ALL_FROM_ATTENDANCE + " WHERE curriculum_id =? and group_id =?");
        preparedStatement.setInt(1, curriculum.getId());
        preparedStatement.setInt(2, curriculum.getGroup().getId());

        ResultSet resultSet = preparedStatement.executeQuery();

        List<Attendance> attendanceList = new ArrayList<>();
        while (resultSet.next()) {
            Attendance attendance = getAttendanceFromResult(resultSet);
            attendanceList.add(attendance);
        }
        return attendanceList;
    }

    public List<Attendance> findAllDataByStudentAttendance(LocalDate localDateFrom, LocalDate localDateTill, StudentAttendance studentAttendance)
            throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM QCurriculumStudentAttendance WHERE calendar_date between ? and ? " +
                " AND subject_id = ? AND group_id = ? AND student_id = ?");
        preparedStatement.setDate(1, new Date(DateUtil.getDateFromLocalDate(localDateFrom).getTime()));
        preparedStatement.setDate(2, new Date(DateUtil.getDateFromLocalDate(localDateTill).getTime()));
        preparedStatement.setInt(3, studentAttendance.getSubject().getId());
        preparedStatement.setInt(4, studentAttendance.getGroup().getId());
        preparedStatement.setInt(5, studentAttendance.getStudent().getId());

        ResultSet resultSet = preparedStatement.executeQuery();

        List<Attendance> attendanceList = new ArrayList<>();
        while (resultSet.next()) {
            Attendance attendance = getAttendanceFromResult(resultSet);
            attendanceList.add(attendance);
        }
        return attendanceList;
    }


    @Override
    public List<ComboBoxItem> findAllComboBoxData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_FROM_ATTENDANCE);
        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Attendance attendance = getAttendanceFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(attendance);
            comboBoxItems.add(comboBoxItem);
        }
        statement.close();
        return comboBoxItems;
    }

    public ComboBoxItem getComboBoxItem(Attendance attendance) {
        return null;
    }

    @Override
    public ComboBoxItem findComboBoxDataById(Integer id) throws SQLException {
        PreparedStatement preparedStatement =
                connection.prepareStatement(SELECT_ALL_FROM_ATTENDANCE + " WHERE curriculum_id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        ComboBoxItem comboBoxItem = null;
        if (resultSet.next()) {
            Attendance attendance = getAttendanceFromResult(resultSet);
            comboBoxItem = getComboBoxItem(attendance);
        }
        return comboBoxItem;
    }

    private Attendance getAttendanceFromResult(ResultSet resultSet) throws SQLException {
        Attendance attendance = new Attendance();
        if (resultSet.getString("curriculum_id") != null) {
            CurriculumDao curriculumDao = new CurriculumDao(connection);
            attendance.setCurriculum(curriculumDao.findById(resultSet.getInt("curriculum_id")));
        } else {
            return null;
        }

        if (resultSet.getString("group_id") != null) {
            GroupDao groupDao = new GroupDao(connection);
            attendance.setGroup(groupDao.findById(resultSet.getInt("group_id")));
        }

        if (resultSet.getString("student_id") != null) {
            StudentDao studentDao = new StudentDao(connection);
            attendance.setStudent(studentDao.findById(resultSet.getInt("student_id")));
        } else {
            return null;
        }

        if (resultSet.getString("attendance_state_id") != null) {
            AttendanceStateDao attendanceStateDao = new AttendanceStateDao(connection);
            attendance.setAttendanceState(attendanceStateDao.findById(resultSet.getInt("attendance_state_id")));
        }

        return attendance;
    }

}
