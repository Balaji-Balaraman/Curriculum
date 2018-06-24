package ua.curriculum;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.curriculum.controller.*;
import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.fx.DialogText;
import ua.curriculum.fx.Dialogs;
import ua.curriculum.fx.ImageResources;
import ua.curriculum.model.Curriculum;
import ua.curriculum.model.User;
import ua.curriculum.utils.DateUtil;
import ua.curriculum.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class MainApp extends Application {
    private static final String JDBC_DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
    private static final String JDBC_CONNECTION_STRING = "jdbc:ucanaccess:////%s";
    private static final String APP_NAME = "Модуль моніторингу відвідування занять";
    private static final String APP_NAME_FORMAT = APP_NAME + ": %s [ %s / %s] %s";
    private static final String ACCESS_FILE_NAME = "Curriculum.accdb";
    private static final String CONFIG_FILE_NAME = "Curriculum.properties";

    private static final Logger rootLogger = LogManager.getRootLogger();

    private Stage primaryStage;
    private BorderPane mainView;

    //private AccessConnection connection = AccessConnection.getInstance();
    private Connection connection;
    private User loginUser;

    public static void main(String[] args) {
        rootLogger.info("Start");
        launch(args);
        rootLogger.info("Close");
    }


    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        initConnection();

        if (showLoginDialog()) {
            initMainView();
            showCurriculumView();
        }

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                if (isConfirmShutDown()) {
                    Platform.exit();
                    System.exit(0);
                }
                else{
                    we.consume();
                }
            }
        });
    }

    private void initConnection() throws ClassNotFoundException {
        //                "////media/mbrunarskiy/92AC0E82AC0E60D9/AnGo/java/test_dip/Curriculum.accdb";
        //        msAccessDBName =
        //                "P:\\Java\\FX\\Curriculum\\target\\classes\\Curriculum.accdb";
        // For Jasper
        // JDBS Driver: net.ucanaccess.jdbc.UcanaccessDriver
        // JDBC URL: Report jdbc:ucanaccess://P:/Java/FX/Curriculum/target/classes/Curriculum.accdb
        //

        File dataBaseFile = FileUtils.getFileWithName(this.getClass(), ACCESS_FILE_NAME);
        String msAccessDBName = dataBaseFile.getAbsolutePath();
        //rootLogger.info(" File: " + msAccessDBName + " , exist: " + dataBaseFile.exists());

        Class.forName(JDBC_DRIVER);

        try {
            connection = DriverManager.getConnection(String.format(JDBC_CONNECTION_STRING, msAccessDBName));
        } catch (SQLException e) {
            Dialogs.showErrorDialog(e, new DialogText("Application start error", "Error with DatBase file",
                                                      "Can't find DataBase file '" + dataBaseFile + "'"), rootLogger);
        }
    }

    public void initMainView() {
        try {
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(MainApp.class.getResource("/views/MainView.fxml"));

            mainView = loader.load();

            String userFIP = (loginUser.getPerson()!=null && loginUser.getPerson().getShortPIP()!=null)? loginUser
                    .getPerson().getShortPIP(): "";

            primaryStage.setTitle(String.format(APP_NAME_FORMAT, userFIP, loginUser.getLogin(), loginUser
                        .getUserType().getName(), DateUtil.dateToString(new Date())));

            primaryStage.getIcons().add(ImageResources.getAppIcon());

            Scene scene = new Scene(mainView);
            primaryStage.setScene(scene);

            MainViewController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();

//            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//                public void handle(WindowEvent we) {
//                    if (!isConfirmShutDown()) {
//                        we.consume();
//                    }
//                }
//            });

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'MainView'"), rootLogger);
        }
    }

    private void showCurriculumView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/CurriculumView.fxml"));
            AnchorPane calendarView = (AnchorPane) loader.load();

            mainView.setCenter(calendarView);

            CurriculumViewController controller = loader.getController();
            //controller.setDialogStage(dialogStage);
            controller.setMainApp(this);

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'CalendarView'"),
                                    rootLogger);
        }
    }

    public boolean showLoginDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/LoginForm.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.setTitle("Login");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            dialogStage.initStyle(StageStyle.UNDECORATED);

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);


            LoginViewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainApp(this);

            dialogStage.showAndWait();

            return controller.isLogin();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Login'"), rootLogger);
            return false;
        }
    }

    public void showChangePasswordDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/ChangePasswordForm.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.setTitle("Зміна паролю");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            //dialogStage.initStyle(StageStyle.UNDECORATED);

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);


            ChangePasswordViewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainApp(this);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Login'"), rootLogger);
        }
    }

    public void showAboutDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/AboutView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.setTitle("Про програму");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            //dialogStage.initStyle(StageStyle.UNDECORATED);

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);


            AboutViewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainApp(this);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Login'"), rootLogger);
        }
    }


    public void showPeriodAndGroupDialog(LocalDate dateFrom, LocalDate dateTill) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/PeriodAndGroupView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.setTitle("Параметри звіту");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            //dialogStage.initStyle(StageStyle.UNDECORATED);

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);


            PeriodAndGroupViewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.initDates(dateFrom, dateTill);
            controller.setMainApp(this);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'PeriodAndView'"),
                                    rootLogger);
        }
    }

    public void showHelpDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/HelpView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            //dialogStage.setResizable(false);
            dialogStage.setTitle("Інструкція користувача");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            //dialogStage.initStyle(StageStyle.UNDECORATED);

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);


            HelpViewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainApp(this);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Hepl'"), rootLogger);
        }
    }

    public void showTEMPDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/TempTableView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            //dialogStage.setResizable(false);
            dialogStage.setTitle("TEMP");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            dialogStage.initModality(Modality.WINDOW_MODAL);

            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            TempTableViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'TEMP'"), rootLogger);
        }
    }

    public void showStudentsDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/StudentsView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            //dialogStage.setResizable(false);
            dialogStage.setTitle("Студенти");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            dialogStage.initStyle(StageStyle.DECORATED);

            dialogStage.initModality(Modality.WINDOW_MODAL);

            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            StudentsViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Students'"), rootLogger);
        }
    }

    public void showTeachersDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/TeachesView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            //dialogStage.setResizable(false);
            dialogStage.setTitle("Викладачі");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            TeachersViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Teachers'"), rootLogger);
        }
    }


    public void showUsersDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/UsersView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            //dialogStage.setResizable(false);
            dialogStage.setTitle("Користувачі");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            UsersViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Users'"), rootLogger);
        }
    }

    public void showSchoolyearsDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/SchoolYearView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            //dialogStage.setResizable(false);
            dialogStage.setTitle("Навчальні роки");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            SchoolYearsViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Schoolyears'"),
                                    rootLogger);
        }
    }

    public void showClassroomsDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/ClassroomsView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            //dialogStage.setResizable(false);
            dialogStage.setTitle("Аудиторії");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ClassroomsViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Classrooms'"),
                                    rootLogger);
        }
    }

    public void showLessonTypesDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/LessonTypesView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            //dialogStage.setResizable(false);
            dialogStage.setTitle("Типи занять");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            LessonTypesViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Classrooms'"),
                                    rootLogger);
        }
    }

    public void showLessonsDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/LessonsView.fxml"));

            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            //dialogStage.setResizable(false);
            dialogStage.setTitle("Час занять");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            LessonsViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Lessons'"),
                                    rootLogger);
        }
    }

    public void showSpecialitiesDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/SpecialitiesView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            //dialogStage.setResizable(false);
            dialogStage.setTitle("Спеціальності");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            SpecialitiesViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Specialities'"),
                                    rootLogger);
        }
    }

    public void showSubjectsDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/SubjectsView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            //dialogStage.setResizable(false);
            dialogStage.setTitle("Перелік предметів");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            SubjectsViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Specialities'"),
                                    rootLogger);
        }
    }

    public void showGroupsDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/GroupsView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            //dialogStage.setResizable(false);
            dialogStage.setTitle("Групи студентів");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            GroupsViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Schoolyears'"),
                                    rootLogger);
        }
    }
    public void showAttendanceStudentDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/StudentAttendance.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            //dialogStage.setResizable(false);
            dialogStage.setTitle("Облік відвідування занять");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            StudentAttendanceViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Attendance'"),
                                    rootLogger);
        }
    }

    public ComboBoxItem showSearchFormDialog(List<ComboBoxItem> comboBoxItems) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/SearchFormView.fxml"));
            //AnchorPane page = (AnchorPane) loader.load();
            BorderPane page = (BorderPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.setTitle("Довідник");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            SearchFormViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            controller.setTableDataList(comboBoxItems);

            dialogStage.showAndWait();

            return controller.getSelectedItem();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Specialities'"),
                                    rootLogger);
            return null;
        }
    }

    public Boolean showEditCurriculumFormDialog(Curriculum curriculum, boolean isEdit) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/EditCurriculum.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            //BorderPane page = (BorderPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.setTitle("Редагування запису");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            EditCurriculumViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);
            controller.setEdit(isEdit);

            controller.setCurrentCurriculum(curriculum);

            dialogStage.showAndWait();

            return controller.isOkClicked();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Specialities'"),
                                    rootLogger);
            return null;
        }
    }


    public void showCopyCurriculumParamsDialog(LocalDate date) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/CopyCurriculumParams.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.setTitle("Копіювання розкладу");
            dialogStage.getIcons().add(ImageResources.getAppIcon());

            //dialogStage.initStyle(StageStyle.UNDECORATED);

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);


            CopyCurriculumParamsViewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainApp(this);
            controller.setDateFrom(date);

            dialogStage.showAndWait();

        } catch (IOException e) {
            Dialogs.showErrorDialog(e, new DialogText("Form show error", "", "Can't open form 'Login'"), rootLogger);
        }
    }

    public boolean shutDown() {
        if (isConfirmShutDown()) {

            Platform.exit();

            return true;
        }
        return false;
    }

    private boolean isConfirmShutDown() {
        if (Dialogs.showConfirmDialog(new DialogText("Припинення роботи", "Додаток буде закритий", "Підтвердити?"),
                                      rootLogger)) {
            return true;
        }
        return false;
    }

    public User getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}
