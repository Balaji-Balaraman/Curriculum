package ua.curriculum.dao;

import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.model.Student;
import ua.curriculum.utils.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDao implements TableDateDao<Student> {

    public static final String SELECT_ALL_STUDENTS = "SELECT * FROM Students";
    public static final String SELECT_ALL_STUDENTS_ORDER = "SELECT * FROM Students ORDER BY 2,3,4";
    public static final String DELETE_BY_ID = "DELETE FROM Students WHERE id=%d";
    public static final String INSERT_INTO_STUDENTS = "INSERT INTO Students VALUES (NULL, ?, ?, ?, ?,?)";
    public static final String UPDATE_STUDENT = "UPDATE Students SET lastname=?, Firstname=?, Middlename=?, Birthday=?, Address=? WHERE id=?";
    private Connection connection;

    public StudentDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Student findById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_STUDENTS + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        Student student = null;
        if (resultSet.next()) {
            student = getStudentFromResult(resultSet);
        }
        return student;
    }

    @Override
    public boolean insert(Student student) throws SQLException {
        //statement.executeUpdate("INSERT INTO Users(login, user_type_id) " + "VALUES ('user1', '3')");

        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_STUDENTS);
        preparedStatement.setString(1, student.getLastName());
        preparedStatement.setString(2, student.getFirsName());
        preparedStatement.setString(3, student.getMiddleName());
        preparedStatement.setDate(4, new java.sql.Date(DateUtil.getDateFromLocalDate(student.getBirthday()).getTime()));
        preparedStatement.setString(5, student.getAddress());

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Student student) throws SQLException {
        //statement.executeUpdate("INSERT INTO Users(login, user_type_id) " + "VALUES ('user1', '3')");
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STUDENT);
        preparedStatement.setString(1, student.getLastName());
        preparedStatement.setString(2, student.getFirsName());
        preparedStatement.setString(3, student.getMiddleName());
        preparedStatement.setDate(4, new java.sql.Date(DateUtil.getDateFromLocalDate(student.getBirthday()).getTime()));
        preparedStatement.setString(5, student.getAddress());
        preparedStatement.setInt(6, student.getId());
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
    public List<Student> findAllData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_STUDENTS);
        List<Student> studentList = new ArrayList<>();
        while (resultSet.next()) {
            Student student = getStudentFromResult(resultSet);
            studentList.add(student);
        }
        //statement.executeUpdate("INSERT INTO Users(login, user_type_id) " + "VALUES ('user1', '3')");
        statement.close();
        return studentList;
    }

    @Override
    public List<ComboBoxItem> findAllComboBoxData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_STUDENTS_ORDER);
        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Student student = getStudentFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(student);
            comboBoxItems.add(comboBoxItem);
        }

        statement.close();
        return comboBoxItems;
    }

    @Override
    public ComboBoxItem findComboBoxDataById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_STUDENTS + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        ComboBoxItem comboBoxItem = null;
        if (resultSet.next()) {
            Student student = getStudentFromResult(resultSet);
            comboBoxItem = getComboBoxItem(student);
        }
        return comboBoxItem;
    }


    public List<ComboBoxItem> findNewStudentsForGroup(Integer groupId) throws SQLException {
        Statement statement = connection.createStatement();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_STUDENTS +
                                                                          " WHERE id not in (select student_id from " +
                                                                          "groups_students where group_id= ?)");
        preparedStatement.setInt(1, groupId);
        ResultSet resultSet = preparedStatement.executeQuery();

        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Student student = getStudentFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(student);
            comboBoxItems.add(comboBoxItem);
        }

        statement.close();
        return comboBoxItems;
    }


    public List<ComboBoxItem> findStudentsByGroupId(Integer groupId) throws SQLException {
        Statement statement = connection.createStatement();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_STUDENTS +
                                                                          " WHERE id in (select student_id from " +
                                                                          "groups_students where group_id= ?)");
        preparedStatement.setInt(1, groupId);
        ResultSet resultSet = preparedStatement.executeQuery();

        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Student student = getStudentFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(student);
            comboBoxItems.add(comboBoxItem);
        }

        statement.close();
        return comboBoxItems;
    }

    public ComboBoxItem getComboBoxItem(Student student) {
        return new ComboBoxItem(String.valueOf(student.getId()), student.getFullPIP());
    }

    public List<Student> findGroupStudentsById(int groupId) throws SQLException {
        Statement statement = connection.createStatement();
        PreparedStatement preparedStatement = connection.prepareStatement(
                SELECT_ALL_STUDENTS + " WHERE id in (select student_id from groups_students where group_id= ?)");
        preparedStatement.setInt(1, groupId);
        ResultSet resultSet = preparedStatement.executeQuery();

        List<Student> studentList = new ArrayList<>();
        while (resultSet.next()) {
            Student student = getStudentFromResult(resultSet);
            studentList.add(student);
        }
        statement.close();
        return studentList;
    }



    private Student getStudentFromResult(ResultSet resultSet) throws SQLException {
        Student student = new Student();
        if (resultSet.getString("id") != null) {
            student.setId(Integer.parseInt(resultSet.getString("id")));
        } else {
            return null;
        }
        if (resultSet.getString("Lastname") != null) {
            student.setLastName(resultSet.getString("Lastname"));
        } else {
            student.setLastName("");
        }
        if (resultSet.getString("Firstname") != null) {
            student.setFirsName(resultSet.getString("Firstname"));
        } else {
            student.setFirsName("");
        }
        if (resultSet.getString("Middlename") != null) {
            student.setMiddleName(resultSet.getString("Middlename"));
        } else {
            student.setMiddleName("");
        }

        if (resultSet.getString("Birthday") != null) {
            student.setBirthday(DateUtil.getLocalDate(resultSet.getDate("Birthday")));
        }
        if (resultSet.getString("Address") != null) {
            student.setAddress(resultSet.getString("Address"));
        } else {
            student.setAddress("");
        }

        return student;
    }

}
