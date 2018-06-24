package ua.curriculum.dao;

import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.model.Classroom;
import ua.curriculum.model.Student;
import ua.curriculum.utils.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassroomDao implements TableDateDao<Classroom> {

    public static final String SELECT_ALL_CLASSROOMS = "SELECT * FROM Classrooms";
    public static final String SELECT_ALL_CLASSROOMS_ORDER = "SELECT * FROM Classrooms ORDER BY code, full_name";
    public static final String DELETE_BY_ID = "DELETE FROM Classrooms WHERE id=%d";
    public static final String INSERT_INTO_CLASSROOMS = "INSERT INTO Classrooms(full_name, short_name, code) VALUES (?, ?, ?)";
    public static final String UPDATE_CLASSROOM = "UPDATE Classrooms SET full_name=?, short_name=?, code=? WHERE id=?";
    private Connection connection;

    public ClassroomDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Classroom findById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_CLASSROOMS + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        Classroom classroom = null;
        if (resultSet.next()) {
            classroom = getClassroomFromResult(resultSet);
        }
        return classroom;
    }

    @Override
    public boolean insert(Classroom classroom) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_CLASSROOMS);
        preparedStatement.setString(1, classroom.getName());
        preparedStatement.setString(2, classroom.getShortName());
        preparedStatement.setString(3, classroom.getCode());

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Classroom classroom) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CLASSROOM);
        preparedStatement.setString(1, classroom.getName());
        preparedStatement.setString(2, classroom.getShortName());
        preparedStatement.setString(3, classroom.getCode());
        preparedStatement.setInt(4, classroom.getId());
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
    public List<Classroom> findAllData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_CLASSROOMS);
        List<Classroom> classroomList = new ArrayList<>();
        while (resultSet.next()) {
            Classroom classroom = getClassroomFromResult(resultSet);
            classroomList.add(classroom);
        }
        //statement.executeUpdate("INSERT INTO Users(login, user_type_id) " + "VALUES ('user1', '3')");
        statement.close();
        return classroomList;
    }

    @Override
    public List<ComboBoxItem> findAllComboBoxData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_CLASSROOMS_ORDER);
        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Classroom classroom = getClassroomFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(classroom);
            comboBoxItems.add(comboBoxItem);
        }

        statement.close();
        return comboBoxItems;
    }

    @Override
    public ComboBoxItem findComboBoxDataById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_CLASSROOMS + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        ComboBoxItem comboBoxItem = null;
        if (resultSet.next()) {
            Classroom classroom = getClassroomFromResult(resultSet);
            comboBoxItem = getComboBoxItem(classroom);
        }
        return comboBoxItem;
    }

    public ComboBoxItem getComboBoxItem(Classroom classroom) {
        return new ComboBoxItem(String.valueOf(classroom.getId()), classroom.getName());
    }

    private Classroom getClassroomFromResult(ResultSet resultSet) throws SQLException {
        Classroom classroom = new Classroom();
        if (resultSet.getString("id") != null) {
            classroom.setId(Integer.parseInt(resultSet.getString("id")));
        } else {
            return null;
        }
        if (resultSet.getString("Full_name") != null) {
            classroom.setName(resultSet.getString("Full_name"));
        } else {
            classroom.setName("");
        }
        if (resultSet.getString("Short_name") != null) {
            classroom.setShortName(resultSet.getString("Short_name"));
        } else {
            classroom.setShortName("");
        }
        if (resultSet.getString("Code") != null) {
            classroom.setCode(resultSet.getString("Code"));
        } else {
            classroom.setCode("");
        }

        return classroom;
    }

}
