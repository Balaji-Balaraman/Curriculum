package ua.curriculum.dao;

import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.model.LessonType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LessonTypeDao implements TableDateDao<LessonType> {

    public static final String SELECT_ALL_LESSON_TYPE = "SELECT * FROM Lesson_Types";
    public static final String SELECT_ALL_LESSON_TYPE_ORDER = "SELECT * FROM Lesson_Types ORDER BY 2";
    public static final String DELETE_BY_ID = "DELETE FROM Lesson_Types WHERE id=%d";
    public static final String INSERT_INTO_LESSON_TYPE = "INSERT INTO Lesson_Types(full_name, short_name) VALUES (?, ?)";
    public static final String UPDATE_LESSON_TYPE = "UPDATE Lesson_Types SET full_name=?, short_name=? WHERE id=?";
    private Connection connection;

    public LessonTypeDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public LessonType findById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_LESSON_TYPE + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        LessonType lessonType = null;
        if (resultSet.next()) {
            lessonType = getLessonTypeFromResult(resultSet);
        }
        return lessonType;
    }

    @Override
    public boolean insert(LessonType lessonType) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_LESSON_TYPE);
        preparedStatement.setString(1, lessonType.getName());
        preparedStatement.setString(2, lessonType.getShortName());

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(LessonType lessonType) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_LESSON_TYPE);
        preparedStatement.setString(1, lessonType.getName());
        preparedStatement.setString(2, lessonType.getShortName());
        preparedStatement.setInt(3, lessonType.getId());
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
    public List<LessonType> findAllData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_LESSON_TYPE);
        List<LessonType> lessonTypeList = new ArrayList<>();
        while (resultSet.next()) {
            LessonType lessonType = getLessonTypeFromResult(resultSet);
            lessonTypeList.add(lessonType);
        }
        statement.close();
        return lessonTypeList;
    }

    @Override
    public List<ComboBoxItem> findAllComboBoxData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_LESSON_TYPE_ORDER);
        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            LessonType lessonType = getLessonTypeFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(lessonType);
            comboBoxItems.add(comboBoxItem);
        }

        statement.close();
        return comboBoxItems;
    }

    @Override
    public ComboBoxItem findComboBoxDataById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_LESSON_TYPE + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        ComboBoxItem comboBoxItem = null;
        if (resultSet.next()) {
            LessonType lessonType = getLessonTypeFromResult(resultSet);
            comboBoxItem = getComboBoxItem(lessonType);
        }
        return comboBoxItem;
    }

    public ComboBoxItem getComboBoxItem(LessonType lessonType) {
        return new ComboBoxItem(String.valueOf(lessonType.getId()), lessonType.getName());
    }

    private LessonType getLessonTypeFromResult(ResultSet resultSet) throws SQLException {
        LessonType lessontype = new LessonType();
        if (resultSet.getString("id") != null) {
            lessontype.setId(Integer.parseInt(resultSet.getString("id")));
        } else {
            return null;
        }
        if (resultSet.getString("Full_name") != null) {
            lessontype.setName(resultSet.getString("Full_name"));
        } else {
            lessontype.setName("");
        }
        if (resultSet.getString("Short_name") != null) {
            lessontype.setShortName(resultSet.getString("Short_name"));
        } else {
            lessontype.setShortName("");
        }

        return lessontype;
    }

}
