package ua.curriculum.dao;

import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.model.Group;
import ua.curriculum.utils.DateUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GroupDao implements TableDateDao<Group> {

    public static final String SELECT_ALL_GROUPS = "SELECT * FROM groups";
    public static final String SELECT_ALL_GROUPS_ORDER = "SELECT * FROM groups ORDER BY code, full_name";
    public static final String SELECT_BY_CODE = "SELECT * FROM groups WHERE code= \"%s\"";

    public static final String DELETE_BY_ID = "DELETE FROM groups WHERE id=%d";

    public static final String INSERT_INTO_GROUPS =
            "INSERT INTO groups(code, schoolyear_id, speciality_id, full_name, short_name) VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_GROUP =
            "UPDATE groups SET code=?, schoolyear_id=?, speciality_id=?, full_name=?, short_name=? WHERE id=?";
    public static final String UPDATE_GROUP_EXPECT_CODE =
            "UPDATE groups SET schoolyear_id=?, speciality_id=?, full_name=?, short_name=? WHERE id=?";

    private Connection connection;

    public GroupDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Group findById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_GROUPS + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        Group group = null;
        if (resultSet.next()) {
            group = getGroupFromResult(resultSet);
        }
        return group;
    }

    @Override
    public boolean insert(Group group) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_GROUPS);
        preparedStatement.setString(1, group.getCode());
        preparedStatement.setInt(2, group.getSchoolyear().getId());
        preparedStatement.setInt(3, group.getSpeciality().getId());
        preparedStatement.setString(4, group.getName());
        preparedStatement.setString(5, group.getShortName());

        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Group group) throws SQLException {
        Group checkGroup = findById(group.getId());
        int i;
        if (checkGroup.getId() == group.getId() && checkGroup.getCode().equals(group.getCode())) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_GROUP_EXPECT_CODE);
            preparedStatement.setInt(1, group.getSchoolyear().getId());
            preparedStatement.setInt(2, group.getSpeciality().getId());
            preparedStatement.setString(3, group.getName());
            preparedStatement.setString(4, group.getShortName());
            preparedStatement.setInt(5, group.getId());

            i = preparedStatement.executeUpdate();
        } else {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_GROUP);
            preparedStatement.setString(1, group.getCode());
            preparedStatement.setInt(2, group.getSchoolyear().getId());
            preparedStatement.setInt(3, group.getSpeciality().getId());
            preparedStatement.setString(4, group.getName());
            preparedStatement.setString(5, group.getShortName());
            preparedStatement.setInt(6, group.getId());
            i = preparedStatement.executeUpdate();
        }
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
    public List<Group> findAllData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_GROUPS);
        List<Group> groupList = new ArrayList<>();
        while (resultSet.next()) {
            Group group = getGroupFromResult(resultSet);
            groupList.add(group);
        }
        statement.close();
        return groupList;
    }

    public Group findByCode(String code) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(String.format(SELECT_BY_CODE, code));
        Group group = null;
        if (resultSet.next()) {
            group = getGroupFromResult(resultSet);
        }
        statement.close();
        return group;
    }

    @Override
    public List<ComboBoxItem> findAllComboBoxData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_GROUPS_ORDER);
        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Group group = getGroupFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(group);
            comboBoxItems.add(comboBoxItem);
        }

        statement.close();
        return comboBoxItems;
    }

    public List<ComboBoxItem> findAllGroupOnDate(LocalDate localDate) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT g.* FROM groups g, schoolyears s " +
                                                                          "WHERE g.schoolyear_id = s.id and ? between s.date_from and s.date_till");

        preparedStatement.setDate(1, new Date(DateUtil.getDateFromLocalDate(localDate).getTime()));

        ResultSet resultSet = preparedStatement.executeQuery();

        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Group group = getGroupFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(group);
            comboBoxItems.add(comboBoxItem);
        }
        return comboBoxItems;
    }

    public List<ComboBoxItem> findAllGroupOnDate(LocalDate localDateFrom, LocalDate localDateTill) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT g.* FROM groups g, schoolyears s WHERE g.schoolyear_id = s.id " +
                "and ( ? between s.date_from and s.date_till  )and ( ? between s.date_from and s.date_till)");

        preparedStatement.setDate(1, new Date(DateUtil.getDateFromLocalDate(localDateFrom).getTime()));
        preparedStatement.setDate(2, new Date(DateUtil.getDateFromLocalDate(localDateTill).getTime()));

        ResultSet resultSet = preparedStatement.executeQuery();

        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Group group = getGroupFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(group);
            comboBoxItems.add(comboBoxItem);
        }
        return comboBoxItems;
    }
    public List<ComboBoxItem> findAllGroupOnDate(LocalDate localDateFrom, LocalDate localDateTill, Integer studentId)
            throws
                                                                                                    SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT g.* FROM groups g, schoolyears s WHERE g.schoolyear_id = s.id " +
                "AND ( ? between s.date_from and s.date_till  )AND ( ? between s.date_from and s.date_till)" +
                "AND id in (select group_id from groups_students where student_id= ?)");

        preparedStatement.setDate(1, new Date(DateUtil.getDateFromLocalDate(localDateFrom).getTime()));
        preparedStatement.setDate(2, new Date(DateUtil.getDateFromLocalDate(localDateTill).getTime()));
        preparedStatement.setInt(3, studentId);

        ResultSet resultSet = preparedStatement.executeQuery();

        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Group group = getGroupFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(group);
            comboBoxItems.add(comboBoxItem);
        }
        return comboBoxItems;
    }

    @Override
    public ComboBoxItem findComboBoxDataById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_GROUPS + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        ComboBoxItem comboBoxItem = null;
        if (resultSet.next()) {
            Group group = getGroupFromResult(resultSet);
            comboBoxItem = getComboBoxItem(group);
        }
        return comboBoxItem;
    }

    public ComboBoxItem getComboBoxItem(Group group) {
        return new ComboBoxItem(String.valueOf(group.getId()), group.getCodeWithName());
    }


    public List<ComboBoxItem> findAllGroupsByStudent(Integer studentId) throws SQLException {
        Statement statement = connection.createStatement();
        PreparedStatement preparedStatement = connection.prepareStatement(
                SELECT_ALL_GROUPS + " WHERE id in (select group_id from " + "groups_students where student_id= ?)");
        preparedStatement.setInt(1, studentId);
        ResultSet resultSet = preparedStatement.executeQuery();

        List<ComboBoxItem> comboBoxItems = new ArrayList<>();
        while (resultSet.next()) {
            Group group = getGroupFromResult(resultSet);
            ComboBoxItem comboBoxItem = getComboBoxItem(group);
            comboBoxItems.add(comboBoxItem);
        }

        statement.close();
        return comboBoxItems;
    }

    public boolean getStudentsGroupsId(Integer groupId, Integer studentId) throws SQLException {
        PreparedStatement preparedStatement =
                connection.prepareStatement("SELECT * FROM Groups_Students WHERE group_id= ? and student_id = ?");
        preparedStatement.setInt(1, groupId);
        preparedStatement.setInt(2, studentId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return true;
        }
        return false;
    }

    public boolean insertIntoGroupsStudents(Integer groupId, Integer studentId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Groups_Students VALUES (?, ?)");
        preparedStatement.setInt(1, groupId);
        preparedStatement.setInt(2, studentId);
        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            AttendanceDao attendanceDao = new AttendanceDao(connection);
            attendanceDao.insertStudentByGroupID(groupId, studentId);
            return true;
        }
        return false;
    }

    public boolean deleteFromGroupsStudents(Integer groupId, Integer studentId) throws SQLException {
        PreparedStatement preparedStatement =
                connection.prepareStatement("DELETE FROM Groups_Students Where group_id= ? and student_id = ?");
        preparedStatement.setInt(1, groupId);
        preparedStatement.setInt(2, studentId);
        int i = preparedStatement.executeUpdate();
        if (i == 1) {
            AttendanceDao attendanceDao = new AttendanceDao(connection);
            attendanceDao.deleteStudentByGroup(groupId, studentId);
            return true;
        }
        return false;
    }


    private Group getGroupFromResult(ResultSet resultSet) throws SQLException {
        Group group = new Group();
        if (resultSet.getString("id") != null) {
            group.setId(Integer.parseInt(resultSet.getString("id")));
        } else {
            return null;
        }
        if (resultSet.getString("code") != null) {
            group.setCode(resultSet.getString("code"));
        } else {
            return null;
        }

        // schoolyear_id
        if (resultSet.getString("schoolyear_id") != null) {
            SchoolYearDao schoolYearDao = new SchoolYearDao(connection);
            group.setSchoolyear(schoolYearDao.findById(resultSet.getInt("schoolyear_id")));
        }

        //speciality_id
        if (resultSet.getString("speciality_id") != null) {
            SpecialityDao specialityDao = new SpecialityDao(connection);
            group.setSpeciality(specialityDao.findById(resultSet.getInt("speciality_id")));
        }
        if (resultSet.getString("full_name") != null) {
            group.setName(resultSet.getString("full_name"));
        } else {
            group.setName("");
        }
        if (resultSet.getString("short_name") != null) {
            group.setShortName(resultSet.getString("short_name"));
        } else {
            group.setShortName("");
        }

        return group;
    }

}
