package ua.curriculum.dao;

import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao implements TableDateDao<User> {

    public static final String SELECT_ALL_USERS = "SELECT * FROM Users";
    public static final String SELECT_BY_LOGIN = "SELECT * FROM Users WHERE login= \"%s\"";

    public static final String DELETE_BY_ID = "DELETE FROM Users WHERE id=%d";
    public static final String DELETE_BY_LOGIN = "DELETE FROM Users WHERE login\"%s\"";

    public static final String INSERT_INTO_USERS = "INSERT INTO Users(id, login, user_type_id, person_id, password, email, active) VALUES (NULL, ?, ?, ?, ?,?,?)";
    public static final String UPDATE_USER = "UPDATE Users SET login=?, user_type_id=?, person_id=?, password=?, email=?, active=? WHERE id=?";
    public static final String UPDATE_USER_PASSWORD = "UPDATE Users SET password=? WHERE id=?";
    public static final String UPDATE_USER_EXPECT_LOGIN = "UPDATE Users SET user_type_id=?, person_id=?, password=?, email=?, active=? WHERE id=?";

    private Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User findById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        User user = null;
        if (resultSet.next()) {
            user = getUserFromResult(resultSet);
        }
        return user;
    }

    @Override
    public boolean insert(User user) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_USERS);
        preparedStatement.setString(1, user.getLogin());
        preparedStatement.setInt(2, user.getUserType().getId());
        if (user.getPerson() == null) {
            preparedStatement.setNull(3, Types.INTEGER);
        } else {
            preparedStatement.setInt(3, user.getPerson().getId());
        }
        preparedStatement.setString(4, user.getPassword());
        preparedStatement.setString(5, user.getEmail());
        preparedStatement.setBoolean(6, user.isActive());

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(User user) throws SQLException {
        User checkUser = findById(user.getId());
        int i;
        if (checkUser.getId() == user.getId() && checkUser.getLogin().equals(user.getLogin())) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_EXPECT_LOGIN);
            preparedStatement.setInt(1, user.getUserType().getId());

            if (user.getPerson()==null){
                preparedStatement.setNull(2, Types.INTEGER);
            }else{
            preparedStatement.setInt(2, user.getPerson().getId());}

            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setBoolean(5, user.isActive());
            preparedStatement.setInt(6, user.getId());

            i = preparedStatement.executeUpdate();
        } else {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER);
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setInt(2, user.getUserType().getId());
            preparedStatement.setInt(3, user.getPerson().getId());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, user.getEmail());
            preparedStatement.setBoolean(6, user.isActive());
            preparedStatement.setInt(7, user.getId());
            i = preparedStatement.executeUpdate();
        }
        if (i == 1) {
            return true;
        }
        return false;
    }

    public boolean updatePassword(int id, String password) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_PASSWORD);
        preparedStatement.setString(1, password);
        preparedStatement.setInt(2, id);

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

    public boolean deleteByLogin(String login) throws SQLException {
        Statement statement = connection.createStatement();
        int i = statement.executeUpdate(String.format(DELETE_BY_LOGIN, login));
        statement.close();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public List<User> findAllData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_USERS);
        List<User> userList = new ArrayList<>();
        while (resultSet.next()) {
            User user = getUserFromResult(resultSet);
            userList.add(user);
        }
        //statement.executeUpdate("INSERT INTO Users(login, user_type_id) " + "VALUES ('user1', '3')");
        statement.close();
        return userList;
    }

    public User findByLogin(String login) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(String.format(SELECT_BY_LOGIN, login));
        User user = null;
        if (resultSet.next()) {
            user = getUserFromResult(resultSet);
        }
        statement.close();
        return user;
    }

    @Override
    public List<ComboBoxItem> findAllComboBoxData() throws SQLException {
        return null;
    }

    @Override
    public ComboBoxItem findComboBoxDataById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        ComboBoxItem comboBoxItem = null;
        if (resultSet.next()) {
            String objectDisplayName = resultSet.getString("Login");
            comboBoxItem = new ComboBoxItem(resultSet.getString("ID"), objectDisplayName);
        }
        return comboBoxItem;
    }

    private User getUserFromResult(ResultSet resultSet) throws SQLException {
        User user = new User();
        if (resultSet.getString("id") != null) {
            user.setId(Integer.parseInt(resultSet.getString("id")));
        } else {
            return null;
        }
        if (resultSet.getString("login") != null) {
            user.setLogin(resultSet.getString("login"));
        } else {
            return null;
        }

        //private UserType userType;3 user_type_id
        if (resultSet.getString("user_type_id") != null) {
            UserTypeDao userTypeDao = new UserTypeDao(connection);
            user.setUserType(userTypeDao.findById(resultSet.getInt("user_type_id")));
        }

        //private Person person;4 person_id
        if (resultSet.getString("person_id") != null) {
            if (user.getUserType().getId() == 1) {
                user.setPerson(null);
            } else if (user.getUserType().getId() == 2) {
                TeacherDao teacherDao = new TeacherDao(connection);
                user.setPerson(teacherDao.findById(resultSet.getInt("person_id")));
                //teacher
            } else if (user.getUserType().getId() == 3) {
                //student
                StudentDao studentDao = new StudentDao(connection);
                user.setPerson(studentDao.findById(resultSet.getInt("person_id")));
            }
        }
        if (resultSet.getString("password") != null) {
            user.setPassword(resultSet.getString("password"));
        }
        if (resultSet.getString("email") != null) {
            user.setEmail(resultSet.getString("email"));
        } else {
            user.setEmail("");
        }
        if (resultSet.getString("active") != null) {
            user.setActive(resultSet.getBoolean("active"));
        } else {
            user.setActive(true);
        }

        return user;
    }

}
