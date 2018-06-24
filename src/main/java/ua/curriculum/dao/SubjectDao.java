package ua.curriculum.dao;

import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDao implements TableDateDao<Subject> {

    public static final String SELECT_ALL_SUBJECTS = "SELECT * FROM Subjects";
    public static final String SELECT_ALL_SUBJECTS_ORDER = "SELECT * FROM Subjects ORDER BY code, full_name";
    public static final String DELETE_BY_ID = "DELETE FROM Subjects WHERE id=%d";
    public static final String INSERT_INTO_SUBJECTS =
            "INSERT INTO Subjects(full_name, short_name, code) VALUES (?, ?, ?)";
    public static final String UPDATE_SUBJECT = "UPDATE Subjects SET full_name=?, short_name=?, code=? WHERE id=?";
    private Connection connection;

    public SubjectDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Subject findById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SUBJECTS + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        Subject subject = null;
        if (resultSet.next()) {
            subject = getSubjectFromResult(resultSet);
        }
        return subject;
    }

    @Override
    public boolean insert(Subject subject) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_SUBJECTS);
        preparedStatement.setString(1, subject.getName());
        preparedStatement.setString(2, subject.getShortName());
        preparedStatement.setString(3, subject.getCode());

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Subject Subjecty) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SUBJECT);
        preparedStatement.setString(1, Subjecty.getName());
        preparedStatement.setString(2, Subjecty.getShortName());
        preparedStatement.setString(3, Subjecty.getCode());
        preparedStatement.setInt(4, Subjecty.getId());
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
    public List<Subject> findAllData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_SUBJECTS);
        List<Subject> subjectList = new ArrayList<>();
        while (resultSet.next()) {
            Subject Subjecty = getSubjectFromResult(resultSet);
            subjectList.add(Subjecty);
        }
        statement.close();
        return subjectList;
    }

    @Override
    public List<ComboBoxItem> findAllComboBoxData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_SUBJECTS_ORDER);
        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Subject Subjecty = getSubjectFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(Subjecty);
            comboBoxItems.add(comboBoxItem);
        }

        statement.close();
        return comboBoxItems;
    }

    @Override
    public ComboBoxItem findComboBoxDataById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SUBJECTS + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        ComboBoxItem comboBoxItem = null;
        if (resultSet.next()) {
            Subject subject = getSubjectFromResult(resultSet);
            comboBoxItem = getComboBoxItem(subject);
        }
        return comboBoxItem;
    }

    public ComboBoxItem getComboBoxItem(Subject subject) {
        return new ComboBoxItem(String.valueOf(subject.getId()), subject.getName());
    }

    public boolean getTeachersSubjectsId(Integer subjectId, Integer teacherId) throws SQLException {
        PreparedStatement preparedStatement = connection
                .prepareStatement("SELECT * FROM Teachers_Subjects WHERE subject_id= ? and teacher_id = ?");
        preparedStatement.setInt(1, subjectId);
        preparedStatement.setInt(2, teacherId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return true;
        }
        return false;
    }

    public boolean insertIntoTeachersSubjects(Integer subjectId, Integer teacherId) throws SQLException {
        PreparedStatement preparedStatement = connection
                .prepareStatement("INSERT INTO Teachers_Subjects VALUES (?, ?)");
        preparedStatement.setInt(1, subjectId);
        preparedStatement.setInt(2, teacherId);
        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    public boolean deleteFromTeachersSubjects(Integer subjectId, Integer teacherId) throws SQLException {
        PreparedStatement preparedStatement = connection
                .prepareStatement("DELETE FROM Teachers_Subjects Where subject_id= ? and teacher_id = ?");
        preparedStatement.setInt(1, subjectId);
        preparedStatement.setInt(2, teacherId);
        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    private Subject getSubjectFromResult(ResultSet resultSet) throws SQLException {
        Subject subject = new Subject();
        if (resultSet.getString("id") != null) {
            subject.setId(Integer.parseInt(resultSet.getString("id")));
        } else {
            return null;
        }
        if (resultSet.getString("Full_name") != null) {
            subject.setName(resultSet.getString("Full_name"));
        } else {
            subject.setName("");
        }
        if (resultSet.getString("Short_name") != null) {
            subject.setShortName(resultSet.getString("Short_name"));
        } else {
            subject.setShortName("");
        }
        if (resultSet.getString("Code") != null) {
            subject.setCode(resultSet.getString("Code"));
        } else {
            subject.setCode("");
        }

        return subject;
    }

}
