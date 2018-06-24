package ua.curriculum.dao;

import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.model.Teacher;
import ua.curriculum.utils.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDao implements TableDateDao<Teacher> {

    public static final String SELECT_ALL_TEACHERS = "SELECT * FROM Teachers";
    public static final String SELECT_ALL_TEACHERS_ORDER = "SELECT * FROM Teachers ORDER BY 2,3,4";
    public static final String DELETE_BY_ID = "DELETE FROM Teachers WHERE id=%d";
    public static final String INSERT_INTO_TEACHERS =
            "INSERT INTO Teachers(id,lastname, Firstname, Middlename, Birthday, Position, Address) VALUES (NULL, ?, ?, ?, ?,?,?)";
    public static final String UPDATE_TEACHERS =
            "UPDATE Teachers SET lastname=?, Firstname=?, Middlename=?, Birthday=?, Position=?, Address=? WHERE id=?";
    private Connection connection;

    public TeacherDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Teacher findById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_TEACHERS + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        Teacher teacher = null;
        if (resultSet.next()) {
            teacher = getTeacherFromResult(resultSet);
        }
        return teacher;
    }

    @Override
    public boolean insert(Teacher teacher) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_TEACHERS);
        preparedStatement.setString(1, teacher.getLastName());
        preparedStatement.setString(2, teacher.getFirsName());
        preparedStatement.setString(3, teacher.getMiddleName());
        preparedStatement.setDate(4, new Date(DateUtil.getDateFromLocalDate(teacher.getBirthday()).getTime()));
        preparedStatement.setString(5, teacher.getPosition());
        preparedStatement.setString(6, teacher.getAddress());

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Teacher teacher) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TEACHERS);
        preparedStatement.setString(1, teacher.getLastName());
        preparedStatement.setString(2, teacher.getFirsName());
        preparedStatement.setString(3, teacher.getMiddleName());
        preparedStatement.setDate(4, new Date(DateUtil.getDateFromLocalDate(teacher.getBirthday()).getTime()));
        preparedStatement.setString(5, teacher.getPosition());
        preparedStatement.setString(6, teacher.getAddress());
        preparedStatement.setInt(7, teacher.getId());
        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) throws SQLException {
        Statement statement = connection.createStatement();
        int i = statement.executeUpdate(String.format(DELETE_BY_ID, id));
        statement.close();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public List<Teacher> findAllData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_TEACHERS);
        List<Teacher> teacherList = new ArrayList<>();
        while (resultSet.next()) {
            Teacher teacher = getTeacherFromResult(resultSet);
            teacherList.add(teacher);
        }
        statement.close();
        return teacherList;
    }

    @Override
    public List<ComboBoxItem> findAllComboBoxData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_TEACHERS_ORDER);
        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Teacher teacher = getTeacherFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(teacher);
            comboBoxItems.add(comboBoxItem);
        }

        statement.close();
        return comboBoxItems;
    }

    @Override
    public ComboBoxItem findComboBoxDataById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_TEACHERS + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        ComboBoxItem comboBoxItem = null;
        if (resultSet.next()) {
            Teacher teacher = getTeacherFromResult(resultSet);
            comboBoxItem = getComboBoxItem(teacher);
        }
        return comboBoxItem;
    }

    public List<ComboBoxItem> findNewTeachersForSubject(Integer subjectId) throws SQLException {
        Statement statement = connection.createStatement();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_TEACHERS +
                                                                          " WHERE id not in (select teacher_id from teachers_subjects where subject_id= ?)");
        preparedStatement.setInt(1, subjectId);
        ResultSet resultSet = preparedStatement.executeQuery();

        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Teacher teacher = getTeacherFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(teacher);
            comboBoxItems.add(comboBoxItem);
        }

        statement.close();
        return comboBoxItems;
    }

    public List<ComboBoxItem> findAllTeachersWithSubject(Integer subjectId) throws SQLException {
        Statement statement = connection.createStatement();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_TEACHERS +
                                                                          " WHERE id in (select teacher_id from teachers_subjects where subject_id= ?)");
        preparedStatement.setInt(1, subjectId);
        ResultSet resultSet = preparedStatement.executeQuery();

        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Teacher teacher = getTeacherFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(teacher);
            comboBoxItems.add(comboBoxItem);
        }

        statement.close();
        return comboBoxItems;
    }

    public ComboBoxItem getComboBoxItem(Teacher teacher) {
        return new ComboBoxItem(String.valueOf(teacher.getId()), teacher.getFullPIP());
    }

    public List<Teacher> findSubjectTeachersById(int subjectId) throws SQLException {
        Statement statement = connection.createStatement();
        PreparedStatement preparedStatement = connection.prepareStatement(
                SELECT_ALL_TEACHERS + " WHERE id in (select teacher_id from teachers_subjects where subject_id= ?)");
        preparedStatement.setInt(1, subjectId);
        ResultSet resultSet = preparedStatement.executeQuery();

        List<Teacher> teacherList = new ArrayList<>();
        while (resultSet.next()) {
            Teacher teacher = getTeacherFromResult(resultSet);
            teacherList.add(teacher);
        }
        statement.close();
        return teacherList;
    }

    private Teacher getTeacherFromResult(ResultSet resultSet) throws SQLException {
        Teacher teacher = new Teacher();
        if (resultSet.getString("id") != null) {
            teacher.setId(Integer.parseInt(resultSet.getString("id")));
        } else {
            return null;
        }
        if (resultSet.getString("Lastname") != null) {
            teacher.setLastName(resultSet.getString("Lastname"));
        } else {
            teacher.setLastName("");
        }
        if (resultSet.getString("Firstname") != null) {
            teacher.setFirsName(resultSet.getString("Firstname"));
        } else {
            teacher.setFirsName("");
        }
        if (resultSet.getString("Middlename") != null) {
            teacher.setMiddleName(resultSet.getString("Middlename"));
        } else {
            teacher.setMiddleName("");
        }

        if (resultSet.getString("Birthday") != null) {
            teacher.setBirthday(DateUtil.getLocalDate(resultSet.getDate("Birthday")));
        }
        if (resultSet.getString("Position") != null) {
            teacher.setPosition(resultSet.getString("Position"));
        } else {
            teacher.setPosition("");
        }
        if (resultSet.getString("Address") != null) {
            teacher.setAddress(resultSet.getString("Address"));
        } else {
            teacher.setAddress("");
        }

        return teacher;
    }

}
