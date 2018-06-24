package ua.curriculum.dao;

import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.model.Curriculum;
import ua.curriculum.utils.DateUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CurriculumDao implements TableDateDao<Curriculum> {

    public static final String SELECT_ALL_DATA = "SELECT * FROM Curriculum";

    public static final String DELETE_BY_ID = "DELETE FROM Curriculum WHERE id=?";

    public static final String INSERT_INTO_CURRICULUM =
            "INSERT INTO Curriculum(calendar_date, lesson_id, lesson_type_id, subject_id, teacher_id, group_id, classroom_id) VALUES ( ?, ?, ?, ?, ?, ?, ?)";

    //public static final String UPDATE_CURRICULUM = "UPDATE Curriculum SET calendar_date = ?, lesson_id = ?, " +
    //"lesson_type_id = ?, subject_id = ?, teacher_id = ?, group_id = ?," +
    //"classroom_id=? WHERE id=?";
    public static final String UPDATE_CURRICULUM = "UPDATE Curriculum SET lesson_id = ?, lesson_type_id = ?, " +
                                                   "subject_id = ?, teacher_id = ?, group_id = ?, classroom_id=? WHERE id=?";


    private Connection connection;

    public CurriculumDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Curriculum findById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_DATA + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        Curriculum curriculum = null;
        if (resultSet.next()) {
            curriculum = getCurriculumFromResult(resultSet);
        }
        return curriculum;
    }

    @Override
    public boolean insert(Curriculum curriculum) throws SQLException {
        //pInsertOid = connection.prepareStatement(INSERT_OID_SQL, Statement.RETURN_GENERATED_KEYS);

        PreparedStatement preparedStatement =
                connection.prepareStatement(INSERT_INTO_CURRICULUM, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setDate(1, new Date(DateUtil.getDateFromLocalDate(curriculum.getDate()).getTime()));
        preparedStatement.setInt(2, curriculum.getLesson().getId());
        preparedStatement.setInt(3, curriculum.getLessonType().getId());
        preparedStatement.setInt(4, curriculum.getSubject().getId());
        preparedStatement.setInt(5, curriculum.getTeacher().getId());
        preparedStatement.setInt(6, curriculum.getGroup().getId());
        preparedStatement.setInt(7, curriculum.getClassroom().getId());

        int i = preparedStatement.executeUpdate();


        if (i == 1) {
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    AttendanceDao attendanceDao = new AttendanceDao(connection);
                    attendanceDao.insertAllByCurriculumID(generatedKeys.getInt(1));
                    return true;
                } else {
                    throw new SQLException("Creating Curriculum failed, no ID obtained.");
                }
            }


        }
        return false;
    }

    @Override
    public boolean update(Curriculum curriculum) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CURRICULUM);

        preparedStatement.setInt(1, curriculum.getLesson().getId());
        preparedStatement.setInt(2, curriculum.getLessonType().getId());
        preparedStatement.setInt(3, curriculum.getSubject().getId());
        preparedStatement.setInt(4, curriculum.getTeacher().getId());
        preparedStatement.setInt(5, curriculum.getGroup().getId());
        preparedStatement.setInt(6, curriculum.getClassroom().getId());
        preparedStatement.setInt(7, curriculum.getId());
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
    public List<Curriculum> findAllData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_DATA);
        List<Curriculum> userList = new ArrayList<>();
        while (resultSet.next()) {
            Curriculum curriculum = getCurriculumFromResult(resultSet);
            userList.add(curriculum);
        }
        statement.close();
        return userList;
    }

    public List<Curriculum> findAllDataOnDate(LocalDate localDate) throws SQLException {

        List<Curriculum> curriculumList = new ArrayList<>();
        if (localDate == null) {
            return curriculumList;
        }
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT c.* FROM Curriculum c, lessons l Where c.lesson_id = l.id and calendar_date = ? Order by l.number");
        preparedStatement.setDate(1, new Date(DateUtil.getDateFromLocalDate(localDate).getTime()));

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Curriculum curriculum = getCurriculumFromResult(resultSet);
            curriculumList.add(curriculum);
        }

        return curriculumList;
    }

    public List<Curriculum> findAllDataOnDate(LocalDate localDate, int studentId) throws SQLException {

        List<Curriculum> curriculumList = new ArrayList<>();
        if (localDate == null) {
            return curriculumList;
        }
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT c.* FROM Curriculum c, lessons l, Groups_students gs Where " +
                "c.lesson_id = l.id and c.group_id = gs.group_id and calendar_date = ? and student_id = ? " +
                "Order by l.number");
        preparedStatement.setDate(1, new Date(DateUtil.getDateFromLocalDate(localDate).getTime()));
        preparedStatement.setInt(2, studentId);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Curriculum curriculum = getCurriculumFromResult(resultSet);
            curriculumList.add(curriculum);
        }

        return curriculumList;
    }

    @Override
    public List<ComboBoxItem> findAllComboBoxData() throws SQLException {
        return null;
    }

    @Override
    public ComboBoxItem findComboBoxDataById(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_DATA + " WHERE id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        ComboBoxItem comboBoxItem = null;
        /*
        if (resultSet.next()) {
            String objectDisplayName = resultSet.getString("Login");
            comboBoxItem = new ComboBoxItem(resultSet.getString("ID"), objectDisplayName);
        }
        */
        return comboBoxItem;
    }


    private Curriculum getCurriculumFromResult(ResultSet resultSet) throws SQLException {
        Curriculum curriculum = new Curriculum();
        if (resultSet.getString("id") != null) {
            curriculum.setId(Integer.parseInt(resultSet.getString("id")));
        } else {
            return null;
        }

        if (resultSet.getString("calendar_date") != null) {
            curriculum.setDate(DateUtil.getLocalDate(resultSet.getDate("calendar_date")));
        }


        if (resultSet.getString("lesson_id") != null) {
            LessonDao lessonDao = new LessonDao(connection);
            curriculum.setLesson(lessonDao.findById(resultSet.getInt("lesson_id")));
        }

        if (resultSet.getString("lesson_type_id") != null) {
            LessonTypeDao lessonTypeDao = new LessonTypeDao(connection);
            curriculum.setLessonType(lessonTypeDao.findById(resultSet.getInt("lesson_type_id")));
        }

        if (resultSet.getString("subject_id") != null) {
            SubjectDao subjectDao = new SubjectDao(connection);
            curriculum.setSubject(subjectDao.findById(resultSet.getInt("subject_id")));
        }
        if (resultSet.getString("teacher_id") != null) {
            TeacherDao teacherDao = new TeacherDao(connection);
            curriculum.setTeacher(teacherDao.findById(resultSet.getInt("teacher_id")));
        }

        if (resultSet.getString("group_id") != null) {
            GroupDao groupDao = new GroupDao(connection);
            curriculum.setGroup(groupDao.findById(resultSet.getInt("group_id")));
        }

        if (resultSet.getString("classroom_id") != null) {
            ClassroomDao classroomDao = new ClassroomDao(connection);
            curriculum.setClassroom(classroomDao.findById(resultSet.getInt("classroom_id")));
        }

        return curriculum;
    }

}
