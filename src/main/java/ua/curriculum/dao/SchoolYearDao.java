package ua.curriculum.dao;

import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.model.Schoolyear;
import ua.curriculum.model.Student;
import ua.curriculum.utils.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SchoolYearDao implements TableDateDao<Schoolyear> {

    public static final String SELECT_ALL_SCHOOLYEARS = "SELECT * FROM Schoolyears";
    public static final String SELECT_ALL_SCHOOLYEARS_ORDER = "SELECT * FROM Schoolyears ORDER BY date_from, date_till DESC";
    public static final String DELETE_BY_ID = "DELETE FROM Schoolyears WHERE id=%d";
    public static final String INSERT_INTO_SCHOOLYEARS = "INSERT INTO Schoolyears(full_name, short_name, date_from, date_till, description) VALUES (?, ?, ?, ?,?)";
    public static final String UPDATE_SCHOOLYEARS = "UPDATE Schoolyears SET full_name=?, short_name=?, date_from=?, date_till=?, description=? WHERE id=?";
    private Connection connection;

    public SchoolYearDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Schoolyear findById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SCHOOLYEARS + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        Schoolyear schoolYear = null;
        if (resultSet.next()) {
            schoolYear = getSchoolyearFromResult(resultSet);
        }
        return schoolYear;
    }

    @Override
    public boolean insert(Schoolyear schoolyear) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_SCHOOLYEARS);
        preparedStatement.setString(1, schoolyear.getName());
        preparedStatement.setString(2, schoolyear.getShortName());
        preparedStatement.setDate(3, new Date(DateUtil.getDateFromLocalDate(schoolyear.getDateFrom()).getTime()));
        preparedStatement.setDate(4, new Date(DateUtil.getDateFromLocalDate(schoolyear.getDateTill()).getTime()));
        preparedStatement.setString(5, schoolyear.getDescription());

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Schoolyear schoolyear) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SCHOOLYEARS);
        preparedStatement.setString(1, schoolyear.getName());
        preparedStatement.setString(2, schoolyear.getShortName());
        preparedStatement.setDate(3, new Date(DateUtil.getDateFromLocalDate(schoolyear.getDateFrom()).getTime()));
        preparedStatement.setDate(4, new Date(DateUtil.getDateFromLocalDate(schoolyear.getDateTill()).getTime()));
        preparedStatement.setString(5, schoolyear.getDescription());
        preparedStatement.setInt(6, schoolyear.getId());
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
    public List<Schoolyear> findAllData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_SCHOOLYEARS);
        List<Schoolyear> schoolyearList = new ArrayList<>();
        while (resultSet.next()) {
            Schoolyear schoolyear = getSchoolyearFromResult(resultSet);
            schoolyearList.add(schoolyear);
        }
        statement.close();
        return schoolyearList;
    }

    @Override
    public List<ComboBoxItem> findAllComboBoxData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_SCHOOLYEARS_ORDER);
        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Schoolyear schoolyear = getSchoolyearFromResult(resultSet);
            ComboBoxItem comboBoxItem = new ComboBoxItem(String.valueOf(schoolyear.getId()), schoolyear.getName());
            comboBoxItems.add(comboBoxItem);
        }

        statement.close();
        return comboBoxItems;
    }

    @Override
    public ComboBoxItem findComboBoxDataById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SCHOOLYEARS + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        ComboBoxItem comboBoxItem = null;
        if (resultSet.next()) {
            Schoolyear schoolyear = getSchoolyearFromResult(resultSet);
            comboBoxItem = new ComboBoxItem(String.valueOf(schoolyear.getId()), schoolyear.getName());
        }
        return comboBoxItem;
    }

    private Schoolyear getSchoolyearFromResult(ResultSet resultSet) throws SQLException {
        Schoolyear schoolyear = new Schoolyear();
        if (resultSet.getString("id") != null) {
            schoolyear.setId(Integer.parseInt(resultSet.getString("id")));
        } else {
            return null;
        }
        if (resultSet.getString("Full_name") != null) {
            schoolyear.setName(resultSet.getString("Full_name"));
        } else {
            schoolyear.setName("");
        }
        if (resultSet.getString("Short_name") != null) {
            schoolyear.setShortName(resultSet.getString("Short_name"));
        } else {
            schoolyear.setShortName("");
        }
        if (resultSet.getString("Date_from") != null) {
            schoolyear.setDateFrom(DateUtil.getLocalDate(resultSet.getDate("Date_from")));
        }
        if (resultSet.getString("Date_till") != null) {
            schoolyear.setDateTill(DateUtil.getLocalDate(resultSet.getDate("Date_till")));
        }
        if (resultSet.getString("Description") != null) {
            schoolyear.setDescription(resultSet.getString("Description"));
        } else {
            schoolyear.setDescription("");
        }

        return schoolyear;
    }

}
