package ua.curriculum.dao;

import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.model.AttendanceState;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceStateDao implements TableDateDao<AttendanceState> {

    public static final String SELECT_ALL_ATTENDANCE_STATE = "SELECT * FROM Attendance_state";
    public static final String SELECT_ALL_ATTENDANCE_STATE_ORDER = "SELECT * FROM Attendance_state ORDER BY 2";
    public static final String DELETE_BY_ID = "DELETE FROM Attendance_state WHERE id= ?";
    public static final String INSERT_INTO_ATTENDANCE_STATE = "INSERT INTO Attendance_state(full_name, short_name) VALUES (?, ?)";
    public static final String UPDATE_ATTENDANCE_STATE = "UPDATE Attendance_state SET full_name=?, short_name=? WHERE id=?";
    private Connection connection;

    public AttendanceStateDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AttendanceState findById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_ATTENDANCE_STATE + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        AttendanceState attendanceState = null;
        if (resultSet.next()) {
            attendanceState = getAttendanceStateFromResult(resultSet);
        }
        return attendanceState;
    }

    @Override
    public boolean insert(AttendanceState attendanceState) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_ATTENDANCE_STATE);
        preparedStatement.setString(1, attendanceState.getName());
        preparedStatement.setString(2, attendanceState.getShortName());

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(AttendanceState attendanceState) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ATTENDANCE_STATE);
        preparedStatement.setString(1, attendanceState.getName());
        preparedStatement.setString(2, attendanceState.getShortName());
        preparedStatement.setInt(3, attendanceState.getId());
        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID);
        preparedStatement.setInt(1, id);

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public List<AttendanceState> findAllData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_ATTENDANCE_STATE);
        List<AttendanceState> attendanceStates = new ArrayList<>();
        while (resultSet.next()) {
            AttendanceState attendanceState = getAttendanceStateFromResult(resultSet);
            attendanceStates.add(attendanceState);
        }
        statement.close();
        return attendanceStates;
    }

    @Override
    public List<ComboBoxItem> findAllComboBoxData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_ATTENDANCE_STATE_ORDER);
        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            AttendanceState attendanceState = getAttendanceStateFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(attendanceState);
            comboBoxItems.add(comboBoxItem);
        }

        statement.close();
        return comboBoxItems;
    }

    @Override
    public ComboBoxItem findComboBoxDataById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_ATTENDANCE_STATE + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        ComboBoxItem comboBoxItem = null;
        if (resultSet.next()) {
            AttendanceState attendanceState = getAttendanceStateFromResult(resultSet);
            comboBoxItem = getComboBoxItem(attendanceState);
        }
        return comboBoxItem;
    }

    public ComboBoxItem getComboBoxItem(AttendanceState attendanceState) {
        return new ComboBoxItem(String.valueOf(attendanceState.getId()), attendanceState.getName());
    }

    private AttendanceState getAttendanceStateFromResult(ResultSet resultSet) throws SQLException {
        AttendanceState attendanceState = new AttendanceState();
        if (resultSet.getString("id") != null) {
            attendanceState.setId(Integer.parseInt(resultSet.getString("id")));
        } else {
            return null;
        }
        if (resultSet.getString("Full_name") != null) {
            attendanceState.setName(resultSet.getString("Full_name"));
        } else {
            attendanceState.setName("");
        }
        if (resultSet.getString("Short_name") != null) {
            attendanceState.setShortName(resultSet.getString("Short_name"));
        } else {
            attendanceState.setShortName("");
        }

        return attendanceState;
    }

}
