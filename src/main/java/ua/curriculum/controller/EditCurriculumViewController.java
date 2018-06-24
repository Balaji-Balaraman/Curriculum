package ua.curriculum.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.curriculum.MainApp;
import ua.curriculum.dao.*;
import ua.curriculum.fx.ComboBoxItem;
import ua.curriculum.fx.DialogText;
import ua.curriculum.fx.Dialogs;
import ua.curriculum.fx.ImageResources;
import ua.curriculum.model.*;
import ua.curriculum.utils.DateUtil;

import java.sql.SQLException;
import java.util.List;

public class EditCurriculumViewController {
    private Logger logger = LogManager.getLogger(EditCurriculumViewController.class);

    @FXML
    private Button buttonOk;
    @FXML
    private Button buttonCancel;

    @FXML
    private Button buttonShowGroup;
    @FXML
    private Button buttonShowClassroom;
    @FXML
    private Button buttonShowSubject;
    @FXML
    private Button buttonShowTeacher;


    @FXML
    private TextField textFDate;

    @FXML
    private ComboBox<ComboBoxItem> comboBoxLesson = new ComboBox<>();
    @FXML
    private ComboBox<ComboBoxItem> comboBoxLessonType = new ComboBox<>();
    @FXML
    private ComboBox<ComboBoxItem> comboBoxClassroom = new ComboBox<>();
    @FXML
    private ComboBox<ComboBoxItem> comboBoxSubject = new ComboBox<>();
    @FXML
    private ComboBox<ComboBoxItem> comboBoxTeacher = new ComboBox<>();
    @FXML
    private ComboBox<ComboBoxItem> comboBoxGroup = new ComboBox<>();

    private Curriculum currentCurriculum;

    private MainApp mainApp;
    private Stage dialogStage;

    private boolean okClicked;
    private boolean isEdit;

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;

        dialogStage.setOnCloseRequest(event -> {
            currentCurriculum = null;
        });
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        comboBoxGroup.setDisable(edit);
    }

    public Curriculum getCurrentCurriculum() {
        return currentCurriculum;
    }

    public void setCurrentCurriculum(Curriculum currentCurriculum) {
        this.currentCurriculum = currentCurriculum;
        setSelectedObject(currentCurriculum);
    }

    @FXML
    private void initialize() {
        initButtonsIcons();

        initButtonsToolTip();

        initComponentListeners();

    }

    private void initButtonsIcons() {
        buttonOk.setGraphic(new ImageView(ImageResources.getButtonOk()));
        buttonCancel.setGraphic(new ImageView(ImageResources.getButtonCancel()));

        buttonShowGroup.setGraphic(new ImageView(ImageResources.getButtonDirectory()));
        buttonShowClassroom.setGraphic(new ImageView(ImageResources.getButtonDirectory()));
        buttonShowTeacher.setGraphic(new ImageView(ImageResources.getButtonDirectory()));
        buttonShowSubject.setGraphic(new ImageView(ImageResources.getButtonDirectory()));
    }

    private void initButtonsToolTip() {
        buttonOk.setTooltip(new Tooltip("Підтвердити зміни"));
        buttonCancel.setTooltip(new Tooltip("Відмінити зміни"));

        buttonShowGroup.setTooltip(new Tooltip("Показати довідник груп"));
        buttonShowClassroom.setTooltip(new Tooltip("Показати довідник аудиторій"));
        buttonShowSubject.setTooltip(new Tooltip("Показати довідник предметів"));
        buttonShowTeacher.setTooltip(new Tooltip("Показати довідник викладачів"));
    }

    private void initComponentListeners() {

        comboBoxSubject.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            comboBoxTeacher.setItems(getTeacherComboBoxItems(newValue));
        });
        comboBoxGroup.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            comboBoxLesson.setItems(getLessonComboBoxItems(newValue));
        });
    }


    private ObservableList<ComboBoxItem> getTeacherComboBoxItems(ComboBoxItem subjectItem) {
        ObservableList<ComboBoxItem> teachersList = FXCollections.observableArrayList();
        if (mainApp != null && subjectItem != null) {
            TeacherDao teacherDao = new TeacherDao(mainApp.getConnection());
            try {
                List<ComboBoxItem> list =
                        teacherDao.findAllTeachersWithSubject(Integer.parseInt(subjectItem.getObjectId()));
                teachersList.addAll(list);
            } catch (SQLException e) {
                logger.error(e);
            }

        } else {

        }
        return teachersList;
    }

    private ObservableList<ComboBoxItem> getLessonComboBoxItems(ComboBoxItem groupItem) {
        ObservableList<ComboBoxItem> lessonsList = FXCollections.observableArrayList();
        if (mainApp != null && groupItem != null) {
            LessonDao lessonDao = new LessonDao(mainApp.getConnection());
            try {
                List<ComboBoxItem> list = lessonDao.findFreeLessonsOnDateForGroup(currentCurriculum.getDate(),
                                                                                  Integer.parseInt(
                                                                                          groupItem.getObjectId()));
                if (currentCurriculum.getLesson() != null &&
                    Integer.parseInt(groupItem.getObjectId()) == currentCurriculum.getLesson().getId()) {
                    lessonsList.add(lessonDao.getComboBoxItem(currentCurriculum.getLesson()));
                }

                lessonsList.addAll(list);
            } catch (SQLException e) {
                logger.error(e);
            }

        } else {

        }
        return lessonsList;
    }

    private void setSelectedObject(Curriculum curriculum) {
        if (curriculum != null) {
            textFDate.setText(DateUtil.format(curriculum.getDate()));

            try {
                GroupDao groupDao = new GroupDao(mainApp.getConnection());
                ObservableList<ComboBoxItem> comboBoxGroups = FXCollections.observableArrayList();
                comboBoxGroups.addAll(groupDao.findAllGroupOnDate(curriculum.getDate()));
                comboBoxGroup.setItems(comboBoxGroups);
                if (curriculum.getGroup() != null) {
                    comboBoxGroup.getSelectionModel().select(groupDao.getComboBoxItem(curriculum.getGroup()));
                    if (curriculum.getLesson() != null) {
                        LessonDao lessonDao = new LessonDao(mainApp.getConnection());
                        ComboBoxItem comboBoxItem = lessonDao.getComboBoxItem(curriculum.getLesson());
                        comboBoxGroups.add(comboBoxItem);
                        comboBoxLesson.getSelectionModel().select(comboBoxItem);
                    }
                }


                /*LessonDao lessonDao = new LessonDao(mainApp.getConnection());
                ObservableList<ComboBoxItem> comboBoxLessons = FXCollections.observableArrayList();
                comboBoxLessons.addAll(lessonDao.findFreeLessonsOnDate(curriculum.getDate()));
                comboBoxLesson.setItems(comboBoxLessons);
                if (curriculum.getLesson() != null) {
                    comboBoxLesson.getSelectionModel().select(lessonDao.getComboBoxItem(curriculum.getLesson()));
                }*/

                LessonTypeDao lessonTypeDao = new LessonTypeDao(mainApp.getConnection());
                ObservableList<ComboBoxItem> comboBoxLessonsType = FXCollections.observableArrayList();
                comboBoxLessonsType.addAll(lessonTypeDao.findAllComboBoxData());
                comboBoxLessonType.setItems(comboBoxLessonsType);
                if (curriculum.getLessonType() != null) {
                    comboBoxLessonType.getSelectionModel()
                            .select(lessonTypeDao.getComboBoxItem(curriculum.getLessonType()));
                }

                ClassroomDao classroomDao = new ClassroomDao(mainApp.getConnection());
                ObservableList<ComboBoxItem> comboBoxClassrooms = FXCollections.observableArrayList();
                comboBoxClassrooms.addAll(classroomDao.findAllComboBoxData());
                comboBoxClassroom.setItems(comboBoxClassrooms);
                if (curriculum.getClassroom() != null) {
                    comboBoxClassroom.getSelectionModel()
                            .select(classroomDao.getComboBoxItem(curriculum.getClassroom()));
                }

                SubjectDao subjectDao = new SubjectDao(mainApp.getConnection());
                ObservableList<ComboBoxItem> comboBoxSubjects = FXCollections.observableArrayList();
                comboBoxSubjects.addAll(subjectDao.findAllComboBoxData());
                comboBoxSubject.setItems(comboBoxSubjects);

                if (curriculum.getSubject() != null) {
                    comboBoxSubject.getSelectionModel().select(subjectDao.getComboBoxItem(curriculum.getSubject()));
                    if (curriculum.getTeacher() != null) {
                        TeacherDao teacherDao = new TeacherDao(mainApp.getConnection());
                        comboBoxTeacher.getSelectionModel().select(teacherDao.getComboBoxItem(curriculum.getTeacher()));
                    }
                }


            } catch (SQLException e) {
                logger.error(e);
            }

        } else {
            //selectedItem = null;
        }
    }

    public void onButtonOk(ActionEvent actionEvent) {

        if (isInputValid()) {
            try {
                LessonDao lessonDao = new LessonDao(mainApp.getConnection());
                ComboBoxItem comboBoxItem = comboBoxLesson.getValue();
                Lesson lesson = lessonDao.findById(Integer.parseInt(comboBoxItem.getObjectId()));
                currentCurriculum.setLesson(lesson);

                LessonTypeDao lessonTypeDao = new LessonTypeDao(mainApp.getConnection());
                comboBoxItem = comboBoxLessonType.getValue();
                LessonType lessonType = lessonTypeDao.findById(Integer.parseInt(comboBoxItem.getObjectId()));
                currentCurriculum.setLessonType(lessonType);

                ClassroomDao classroomDao = new ClassroomDao(mainApp.getConnection());
                comboBoxItem = comboBoxClassroom.getValue();
                Classroom classroom = classroomDao.findById(Integer.parseInt(comboBoxItem.getObjectId()));
                currentCurriculum.setClassroom(classroom);

                SubjectDao subjectDao = new SubjectDao(mainApp.getConnection());
                comboBoxItem = comboBoxSubject.getValue();
                Subject subject = subjectDao.findById(Integer.parseInt(comboBoxItem.getObjectId()));
                currentCurriculum.setSubject(subject);

                TeacherDao teacherDao = new TeacherDao(mainApp.getConnection());
                comboBoxItem = comboBoxTeacher.getValue();
                Teacher teacher = teacherDao.findById(Integer.parseInt(comboBoxItem.getObjectId()));
                currentCurriculum.setTeacher(teacher);

                GroupDao groupDao = new GroupDao(mainApp.getConnection());
                comboBoxItem = comboBoxGroup.getValue();
                Group group = groupDao.findById(Integer.parseInt(comboBoxItem.getObjectId()));
                currentCurriculum.setGroup(group);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            okClicked = true;
            dialogStage.close();
        }
    }

    public void onButtonCancel(ActionEvent actionEvent) {
        currentCurriculum = null;
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (comboBoxLesson.getValue() == null) {
            errorMessage += "Вкажіть номер!\n";
        }

        if (comboBoxLessonType.getValue() == null) {
            errorMessage += "Вкажіть тип!\n";
        }

        if (comboBoxClassroom.getValue() == null) {
            errorMessage += "Вкажіть аудиторію!\n";
        }

        if (comboBoxSubject.getValue() == null) {
            errorMessage += "Вкажіть предмет!\n";
        }

        if (comboBoxTeacher.getValue() == null) {
            errorMessage += "Вкажіть викладача!\n";
        }

        if (comboBoxGroup.getValue() == null) {
            errorMessage += "Вкажіть групу!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.showMessage(Alert.AlertType.ERROR,
                                new DialogText("Помилка заповнення", "Будь ласка заповнить помилкові поля",
                                               errorMessage), null);
            //alert.initOwner(dialogStage);
            return false;
        }
    }

    public void onButtonShowClassloom(ActionEvent actionEvent) {
        ClassroomDao classroomDao = new ClassroomDao(mainApp.getConnection());

        try {
            ComboBoxItem item = mainApp.showSearchFormDialog(classroomDao.findAllComboBoxData());
            if (item!=null){
                comboBoxClassroom.getSelectionModel().select(item);
            }
        } catch (SQLException e) {
            Dialogs.showMessage(Alert.AlertType.ERROR,
                                new DialogText("Помилка заповнення", "Будь ласка заповнить помилкові поля",
                                               e.getMessage()), logger);
        }
    }

    public void onButtonShowGroup(ActionEvent actionEvent) {
        GroupDao groupDao = new GroupDao(mainApp.getConnection());

        try {
            ComboBoxItem item = mainApp.showSearchFormDialog(groupDao.findAllGroupOnDate(currentCurriculum.getDate()));
            if (item!=null){
                comboBoxGroup.getSelectionModel().select(item);
            }
        } catch (SQLException e) {
            Dialogs.showMessage(Alert.AlertType.ERROR,
                                new DialogText("Помилка заповнення", "Будь ласка заповнить помилкові поля",
                                               e.getMessage()), logger);
        }
    }

    public void onButtonShowSubject(ActionEvent actionEvent) {
        SubjectDao subjectDao = new SubjectDao(mainApp.getConnection());

        try {
            ComboBoxItem item = mainApp.showSearchFormDialog(subjectDao.findAllComboBoxData());
            if (item!=null){
                comboBoxSubject.getSelectionModel().select(item);
            }
        } catch (SQLException e) {
            Dialogs.showMessage(Alert.AlertType.ERROR,
                                new DialogText("Помилка заповнення", "Будь ласка заповнить помилкові поля",
                                               e.getMessage()), logger);
        }
    }

    public void onButtonShowTeacher(ActionEvent actionEvent) {
        if (comboBoxSubject.getValue()!=null){
        TeacherDao teacherDao = new TeacherDao(mainApp.getConnection());

        try {
            ComboBoxItem item = mainApp.showSearchFormDialog(teacherDao.findAllTeachersWithSubject(Integer.parseInt
                    (comboBoxSubject.getValue().getObjectId())));
            if (item!=null){
                comboBoxTeacher.getSelectionModel().select(item);
            }
        } catch (SQLException e) {
            Dialogs.showMessage(Alert.AlertType.ERROR,
                                new DialogText("Помилка заповнення", "Будь ласка заповнить помилкові поля",
                                               e.getMessage()), logger);
        }
        }else {
            Dialogs.showMessage(Alert.AlertType.ERROR,
                                new DialogText("Помилка відображення", "Будь ласка вкажіть предмет",
                                               "Предмет не вибрано"), null);
        }
    }

}
