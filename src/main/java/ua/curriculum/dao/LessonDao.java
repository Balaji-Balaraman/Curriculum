package ua.curriculum.dao;

import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.model.Lesson;
import ua.curriculum.utils.DateUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LessonDao implements TableDateDao<Lesson> {

    public static final String SELECT_ALL_LESSONS = "SELECT * FROM Lessons";
    public static final String SELECT_ALL_LESSONS_ORDER = "SELECT * FROM Lessons ORDER BY 2";
    public static final String DELETE_BY_ID = "DELETE FROM Lessons WHERE id= ?";
    public static final String INSERT_INTO_LESSONS =
            "INSERT INTO Lessons(number, time, description) VALUES " + "(?, ?, ?)";
    public static final String UPDATE_LESSON =
            "UPDATE Lessons SET number = ?, time= ?, description= ? WHERE" + " id= ?";
    private Connection connection;

    public LessonDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Lesson findById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_LESSONS + " WHERE id= ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        Lesson lesson = null;
        if (resultSet.next()) {
            lesson = getLessonFromResult(resultSet);
        }
        return lesson;
    }

    @Override
    public boolean insert(Lesson lesson) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_LESSONS);
        preparedStatement.setInt(1, lesson.getNumber());
        preparedStatement.setString(2, lesson.getTime());
        preparedStatement.setString(3, lesson.getDescription());

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Lesson lesson) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_LESSON);
        preparedStatement.setInt(1, lesson.getNumber());
        preparedStatement.setString(2, lesson.getTime());
        preparedStatement.setString(3, lesson.getDescription());
        preparedStatement.setInt(4, lesson.getId());
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
    public List<Lesson> findAllData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_LESSONS);
        List<Lesson> lessonList = new ArrayList<>();
        while (resultSet.next()) {
            Lesson lesson = getLessonFromResult(resultSet);
            lessonList.add(lesson);
        }
        statement.close();
        return lessonList;
    }

    @Override
    public List<ComboBoxItem> findAllComboBoxData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_LESSONS_ORDER);
        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Lesson lesson = getLessonFromResult(resultSet);
            ComboBoxItem comboBoxItem = new ComboBoxItem(String.valueOf(lesson.getId()), lesson.getNumberWithTime());
            comboBoxItems.add(comboBoxItem);
        }

        statement.close();
        return comboBoxItems;
    }

    @Override
    public ComboBoxItem findComboBoxDataById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_LESSONS + " WHERE id= ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        ComboBoxItem comboBoxItem = null;
        if (resultSet.next()) {
            Lesson lesson = getLessonFromResult(resultSet);
            comboBoxItem = getComboBoxItem(lesson);
        }
        return comboBoxItem;
    }

    public List<ComboBoxItem> findFreeLessonsOnDate(LocalDate localDate) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(
                SELECT_ALL_LESSONS + " WHERE id not in (select lesson_id from curriculum where calendar_date= ?)");

        preparedStatement.setDate(1, new Date(DateUtil.getDateFromLocalDate(localDate).getTime()));

        ResultSet resultSet = preparedStatement.executeQuery();

        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Lesson lesson = getLessonFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(lesson);
            comboBoxItems.add(comboBoxItem);
        }

        return comboBoxItems;
    }

    public List<ComboBoxItem> findFreeLessonsOnDateForGroup(LocalDate localDate, int groupId) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(
                SELECT_ALL_LESSONS + " WHERE id not in (select lesson_id from curriculum where calendar_date= ? and " +
                "group_id= ?) order by 2");

        preparedStatement.setDate(1, new Date(DateUtil.getDateFromLocalDate(localDate).getTime()));
        preparedStatement.setInt(2, groupId);

        ResultSet resultSet = preparedStatement.executeQuery();

        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Lesson lesson = getLessonFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(lesson);
            comboBoxItems.add(comboBoxItem);
        }

        return comboBoxItems;
    }

    public ComboBoxItem getComboBoxItem(Lesson lesson) {
        return new ComboBoxItem(String.valueOf(lesson.getId()), lesson.getNumberWithTime());
    }

    private Lesson getLessonFromResult(ResultSet resultSet) throws SQLException {
        Lesson lesson = new Lesson();
        if (resultSet.getString("id") != null) {
            lesson.setId(Integer.parseInt(resultSet.getString("id")));
        } else {
            return null;
        }
        if (resultSet.getString("Number") != null) {
            lesson.setNumber(resultSet.getInt("Number"));
        }
        if (resultSet.getString("Time") != null) {
            lesson.setTime(resultSet.getString("Time"));
        } else {
            lesson.setTime("");
        }

        if (resultSet.getString("Description") != null) {
            lesson.setDescription(resultSet.getString("Description"));
        } else {
            lesson.setDescription("");
        }

        return lesson;
    }

}
