package ua.curriculum.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import ua.curriculum.MainApp;

public class MainViewController {

    private MainApp mainApp;
    @FXML
    private Menu menuAdministration;
    @FXML
    private Menu menuStudents;
    @FXML
    private Menu menuTeachers;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        menuAdministration.setVisible(mainApp.getLoginUser().getUserType().getId() == 1);
        menuStudents.setVisible(mainApp.getLoginUser().getUserType().getId() != 3);
        menuTeachers.setVisible(mainApp.getLoginUser().getUserType().getId() != 3);
    }

    @FXML
    private void initialize() {
        //initButtonsIcons();
    }

    public void onMenuItemExit(ActionEvent actionEvent) {
        mainApp.shutDown();
    }

    public void onMenuItemTemp(ActionEvent actionEvent) {
        mainApp.showTEMPDialog();
    }

    public void onMenuItemChangePassword(ActionEvent actionEvent) {
        mainApp.showChangePasswordDialog();
    }

    public void onMenuItemGroups(ActionEvent actionEvent) {
        mainApp.showGroupsDialog();

    }

    public void onMenuItemStudents(ActionEvent actionEvent) {
        mainApp.showStudentsDialog();
    }

    public void onMenuItemTeachers(ActionEvent actionEvent) {
        mainApp.showTeachersDialog();
    }

    public void onMenuItemUsers(ActionEvent actionEvent) {
        mainApp.showUsersDialog();
    }

    public void onMenuItemSchoolyear(ActionEvent actionEvent) {
        mainApp.showSchoolyearsDialog();
    }

    public void onMenuItemClassrooms(ActionEvent actionEvent) {
        mainApp.showClassroomsDialog();
    }

    public void onMenuItemLessonTypes(ActionEvent actionEvent) {
        mainApp.showLessonTypesDialog();
    }

    public void onMenuItemSpecialities(ActionEvent actionEvent) {
        mainApp.showSpecialitiesDialog();
    }

    public void onMenuItemSubjects(ActionEvent actionEvent) {
        mainApp.showSubjectsDialog();
    }

    public void onMenuItemLesson(ActionEvent actionEvent) {
        mainApp.showLessonsDialog();
    }

    public void onMenuItemAttendance(ActionEvent actionEvent) {
        mainApp.showAttendanceStudentDialog();
    }

    public void onMenuItemAbout(ActionEvent actionEvent) {
        mainApp.showAboutDialog();
    }

    public void onMenuItemHelp(ActionEvent actionEvent) {
        mainApp.showHelpDialog();
    }
}
