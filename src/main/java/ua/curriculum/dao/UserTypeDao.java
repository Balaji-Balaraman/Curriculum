package ua.curriculum.dao;

import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.model.UserType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserTypeDao implements TableDateDao<UserType> {

    public static final String SELECT_ALL_FROM_USER_TYPES = "SELECT * FROM User_Types";
    public static final String DELETE_BY_ID = "DELETE FROM User_Types WHERE id=%d";
    public static final String INSERT_INTO_USER_TYPES = "INSERT INTO User_Types(full_name) VALUES (?)";
    public static final String UPDATE_USER_TYPE = "UPDATE User_Types SET full_name=? WHERE id=?";
    private Connection connection;

    public UserTypeDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public UserType findById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_FROM_USER_TYPES + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        UserType userType = null;
        if (resultSet.next()) {
            userType = getUserTypeFromResult(resultSet);
        }
        return userType;
    }

    @Override
    public boolean insert(UserType userType) throws SQLException {
        //statement.executeUpdate("INSERT INTO Users(login, user_type_id) " + "VALUES ('user1', '3')");

        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_USER_TYPES);
        preparedStatement.setString(1, userType.getName());
        return false;
    }

    @Override
    public boolean update(UserType userType) throws SQLException {
        //statement.executeUpdate("INSERT INTO Users(login, user_type_id) " + "VALUES ('user1', '3')");
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_TYPE);
        preparedStatement.setString(1, userType.getName());
        preparedStatement.setInt(2, userType.getId());
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
    public List<UserType> findAllData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_FROM_USER_TYPES);
        List<UserType> studentList = new ArrayList<>();
        while (resultSet.next()) {
            UserType userType = getUserTypeFromResult(resultSet);
            studentList.add(userType);
        }
        statement.close();
        return studentList;
    }

    @Override
    public List<ComboBoxItem> findAllComboBoxData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_FROM_USER_TYPES);
        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            UserType userType = getUserTypeFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(userType);
            comboBoxItems.add(comboBoxItem);
        }
        statement.close();
        return comboBoxItems;
    }

    public ComboBoxItem getComboBoxItem(UserType userType) {
        return new ComboBoxItem(String.valueOf(userType.getId()), userType.getName());
    }

    @Override
    public ComboBoxItem findComboBoxDataById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_FROM_USER_TYPES + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        ComboBoxItem comboBoxItem = null;
        if (resultSet.next()) {
            UserType userType = getUserTypeFromResult(resultSet);
            comboBoxItem = getComboBoxItem(userType);
        }
        return comboBoxItem;
    }

    private UserType getUserTypeFromResult(ResultSet resultSet) throws SQLException {
        UserType userType = new UserType();
        if (resultSet.getString("id") != null) {
            userType.setId(Integer.parseInt(resultSet.getString("id")));
        } else {
            return null;
        }
        if (resultSet.getString("Full_name") != null) {
            userType.setName(resultSet.getString("Full_name"));
        } else {
            userType.setName("");
        }

        return userType;
    }

}
