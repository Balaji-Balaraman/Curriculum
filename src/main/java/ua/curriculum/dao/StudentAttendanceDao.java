package ua.curriculum.dao;

import ua.curriculum.model.Group;
import ua.curriculum.model.StudentAttendance;
import ua.curriculum.model.Subject;
import ua.curriculum.utils.DateUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentAttendanceDao {
    public static final String QUERY_HEADER =
            "TRANSFORM Sum(qs.presence) AS Sum_presence_day " + "SELECT qs.student_id, Sum(qs.state_Absent) AS " +
            "Sum_state_Absent, Sum" + "(qs.States_3) AS Sum_States_3, Sum(qs.presence) AS " + "Sum_presence, Sum" +
            "(qs.all_States) AS Sum_all_States " + "FROM QStudentAttendance qs WHERE calendar_date Between ? And ? ";
    public static final String QUERY_FOOTER = " GROUP BY qs.student_id PIVOT calendar_date";

    public static final String SELECT_HEADER =
            "SELECT qs.group_id, qs.student_id, qs.subject_id, Sum(qs.lessons_count) AS total_lessons, Sum(qs" +
            ".lessons_count) - Sum(qs.absences) AS total_presence, Sum(qs.absences) AS total_absences, " +
            "Sum(qs.illness) AS absence_by_illness " +
            "FROM QStudentAttendance qs WHERE calendar_date Between ? And ? ";
    public static final String SELECT_FOOTER = " GROUP BY qs.subject_id, qs.group_id, qs.student_id " +
                                               " ORDER BY qs.subject_id, qs.group_id, qs.student_id ";

    private Connection connection;

    public StudentAttendanceDao(Connection connection) {
        this.connection = connection;
    }

    /*
    SELECT table1.group_id, table1.student_id, table1.subject_id, table1.calendar_date, Sum(table1.allStates) AS total_lessons, Sum(table1.allStates)-Sum(table1.stateAbsent) AS total_presence, Sum(table1.stateAbsent) AS total_absences, Sum(table1.States3) AS absence_by_illness
    FROM(SELECT qc.group_id, qc.student_id, qc.subject_id, qc.calendar_date,  COUNT(qc.attendance_state_id) as
    allStates, 0 as stateAbsent, 0 as States3
    FROM QCurriculumStudentAttendance qc
    GROUP BY qc.group_id, qc.student_id, qc.subject_id, qc.calendar_date

    UNION ALL

    SELECT qc.group_id, qc.student_id, qc.subject_id, qc.calendar_date,  0 as allStates, COUNT(qc.attendance_state_id) as stateAbsent, 0 as States3
    FROM QCurriculumStudentAttendance qc
    WHERE qc.attendance_state_id <>1
    GROUP BY qc.group_id, qc.student_id, qc.subject_id, qc.calendar_date

    UNION ALL

    SELECT qc.group_id, qc.student_id, qc.subject_id, qc.calendar_date,  0 as allStates, 0 as stateAbsent, COUNT(qc.attendance_state_id)  as States3
    FROM QCurriculumStudentAttendance qc
    WHERE qc.attendance_state_id = 3
    GROUP BY qc.group_id, qc.student_id, qc.subject_id, qc.calendar_date

)  AS table1
    GROUP BY  table1.group_id, table1.student_id, table1.calendar_date, table1.qc.subject_id;
    */

    public List<StudentAttendance> findAllDataOnPeriod(LocalDate localDateFrom, LocalDate localDateTill, Integer groupId,
                                                       Integer studentId, Integer subjectId) throws SQLException {
        List<StudentAttendance> studentAttendances = new ArrayList<>();
        if (localDateFrom == null || localDateTill == null) {
            return studentAttendances;
        }
        String query = "";
        int paramCount = 2;// 1= DateFrom 2= DateTill
        Boolean hasGroupId =false;
        int paramNumberGroupId=-1;
        Boolean hasStudentId =false;
        int paramNumberStudentId=-1;
        Boolean hasSubjectId =false;
        int paramNumberSubjectId=-1;

        if(groupId!=null && groupId!=0){
            query += " AND group_id = ? ";
            hasGroupId =true;
            paramCount++;
            paramNumberGroupId = paramCount;
        }
        if(studentId!=null && studentId!=0){
            query += " AND student_id = ? ";
            hasStudentId =true;
            paramCount++;
            paramNumberStudentId = paramCount;
        }
        if(subjectId!=null && subjectId!=0){
            query += " AND subject_id = ? ";
            hasSubjectId =true;
            paramCount++;
            paramNumberSubjectId = paramCount;
        }

        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_HEADER + query + SELECT_FOOTER);
        preparedStatement.setDate(1, new Date(DateUtil.getDateFromLocalDate(localDateFrom).getTime()));
        preparedStatement.setDate(2, new Date(DateUtil.getDateFromLocalDate(localDateTill).getTime()));
        if (hasGroupId){
            preparedStatement.setInt(paramNumberGroupId, groupId);
        }
        if (hasStudentId){
            preparedStatement.setInt(paramNumberStudentId, studentId);
        }if (hasSubjectId){
            preparedStatement.setInt(paramNumberSubjectId, subjectId);
        }

        ResultSet resultSet = preparedStatement.executeQuery();
        /*
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();

        // The column count starts from 1
        for (int i = 1; i <= columnCount; i++) {
            String name = resultSetMetaData.getColumnName(i);
            System.out.println("Name: " + name);
            // Do stuff with name
        }
        */
        while (resultSet.next()) {
            StudentAttendance studentAttendance = getStudentAttendanceFromResult(resultSet);
            studentAttendances.add(studentAttendance);
        }

        return studentAttendances;

    }

    public List<StudentAttendance> findAllCROSSDataOnDate(LocalDate localDateFrom, LocalDate localDateTill)
            throws SQLException {

        List<StudentAttendance> studentAttendances = new ArrayList<>();
        if (localDateFrom == null) {
            return studentAttendances;
        }
        PreparedStatement preparedStatement = connection.prepareStatement(QUERY_HEADER + QUERY_FOOTER);
        preparedStatement.setDate(1, new Date(DateUtil.getDateFromLocalDate(localDateFrom).getTime()));
        preparedStatement.setDate(2, new Date(DateUtil.getDateFromLocalDate(localDateTill).getTime()));

        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();

        // The column count starts from 1
        for (int i = 1; i <= columnCount; i++) {
            String name = resultSetMetaData.getColumnName(i);
            System.out.println("Name: " + name);
            // Do stuff with name
        }

        while (resultSet.next()) {
            StudentAttendance studentAttendance = getStudentAttendanceFromResult(resultSet);
            studentAttendances.add(studentAttendance);
        }

        return studentAttendances;
    }

    private StudentAttendance getStudentAttendanceFromResult(ResultSet resultSet) throws SQLException {
        StudentAttendance studentAttendance = new StudentAttendance();

        if (resultSet.getString("group_id") != null) {
            GroupDao groupDao = new GroupDao(connection);
            studentAttendance.setGroup(groupDao.findById(resultSet.getInt("group_id")));
        }

        if (resultSet.getString("student_id") != null) {
            StudentDao studentDao = new StudentDao(connection);
            studentAttendance.setStudent(studentDao.findById(resultSet.getInt("student_id")));
        } else {
            return null;
        }

        if (resultSet.getString("subject_id") != null) {
            SubjectDao subjectDao = new SubjectDao(connection);
            studentAttendance.setSubject(subjectDao.findById(resultSet.getInt("subject_id")));
        }
        //        if (resultSet.getString("teacher_id") != null) {
        //            TeacherDao teacherDao = new TeacherDao(connection);
        //            //studentAttendance.setTeacher(teacherDao.findById(resultSet.getInt("teacher_id")));
        //        }

        if (resultSet.getString("total_lessons") != null) {
            studentAttendance.setTotalLessons(resultSet.getInt("total_lessons"));
        } else {
            studentAttendance.setTotalLessons(0);
        }
        if (resultSet.getString("total_presence") != null) {
            studentAttendance.setTotalPresence(resultSet.getInt("total_presence"));
        } else {
            studentAttendance.setTotalPresence(0);
        }
        if (resultSet.getString("total_absences") != null) {
            studentAttendance.setTotalAbsences(resultSet.getInt("total_absences"));
        } else {
            studentAttendance.setTotalAbsences(0);
        }
        if (resultSet.getString("absence_by_illness") != null) {
            studentAttendance.setTotalByIllness(resultSet.getInt("absence_by_illness"));
        } else {
            studentAttendance.setTotalByIllness(0);
        }

        return studentAttendance;
    }
}
