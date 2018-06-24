package ua.curriculum.dao;

import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.model.Speciality;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecialityDao implements TableDateDao<Speciality> {

    public static final String SELECT_ALL_SPECIALITIES = "SELECT * FROM specialities";
    public static final String SELECT_ALL_SPECIALITIES_ORDER = "SELECT * FROM specialities ORDER BY code, full_name";
    public static final String DELETE_BY_ID = "DELETE FROM specialities WHERE id=%d";
    public static final String INSERT_INTO_SPECIALITIES = "INSERT INTO specialities(full_name, short_name, code) VALUES (?, ?, ?)";
    public static final String UPDATE_SPECIALITY = "UPDATE specialities SET full_name=?, short_name=?, code=? WHERE id=?";
    private Connection connection;

    public SpecialityDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Speciality findById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SPECIALITIES + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        Speciality speciality = null;
        if (resultSet.next()) {
            speciality = getSpecialityFromResult(resultSet);
        }
        return speciality;
    }

    @Override
    public boolean insert(Speciality speciality) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_SPECIALITIES);
        preparedStatement.setString(1, speciality.getName());
        preparedStatement.setString(2, speciality.getShortName());
        preparedStatement.setString(3, speciality.getCode());

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Speciality speciality) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SPECIALITY);
        preparedStatement.setString(1, speciality.getName());
        preparedStatement.setString(2, speciality.getShortName());
        preparedStatement.setString(3, speciality.getCode());
        preparedStatement.setInt(4, speciality.getId());
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
    public List<Speciality> findAllData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_SPECIALITIES);
        List<Speciality> specialityList = new ArrayList<>();
        while (resultSet.next()) {
            Speciality speciality = getSpecialityFromResult(resultSet);
            specialityList.add(speciality);
        }
        //statement.executeUpdate("INSERT INTO Users(login, user_type_id) " + "VALUES ('user1', '3')");
        statement.close();
        return specialityList;
    }

    @Override
    public List<ComboBoxItem> findAllComboBoxData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_SPECIALITIES_ORDER);
        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Speciality speciality = getSpecialityFromResult(resultSet);
            ComboBoxItem comboBoxItem = new ComboBoxItem(String.valueOf(speciality.getId()), speciality.getName());
            comboBoxItems.add(comboBoxItem);
        }

        statement.close();
        return comboBoxItems;
    }

    @Override
    public ComboBoxItem findComboBoxDataById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SPECIALITIES + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        ComboBoxItem comboBoxItem = null;
        if (resultSet.next()) {
            Speciality speciality = getSpecialityFromResult(resultSet);
            comboBoxItem = new ComboBoxItem(String.valueOf(speciality.getId()), speciality.getName());
        }
        return comboBoxItem;
    }

    private Speciality getSpecialityFromResult(ResultSet resultSet) throws SQLException {
        Speciality speciality = new Speciality();
        if (resultSet.getString("id") != null) {
            speciality.setId(Integer.parseInt(resultSet.getString("id")));
        } else {
            return null;
        }
        if (resultSet.getString("Full_name") != null) {
            speciality.setName(resultSet.getString("Full_name"));
        } else {
            speciality.setName("");
        }
        if (resultSet.getString("Short_name") != null) {
            speciality.setShortName(resultSet.getString("Short_name"));
        } else {
            speciality.setShortName("");
        }
        if (resultSet.getString("Code") != null) {
            speciality.setCode(resultSet.getString("Code"));
        } else {
            speciality.setCode("");
        }

        return speciality;
    }

}
