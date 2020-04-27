package sasj.controller;

import sasj.controller.principal.PrincipalHomeController;
import sasj.controller.principal.PrincipalCourseController;
import sasj.controller.principal.PrincipalGradeController;
import sasj.controller.principal.PrincipalRoomController;
import sasj.controller.principal.PrincipalScheduleController;
import sasj.controller.principal.PrincipalSchoolClassController;
import sasj.controller.principal.PrincipalSchoolYearController;
import sasj.controller.principal.PrincipalStudentController;
import sasj.controller.principal.PrincipalSubjectController;
import sasj.controller.principal.PrincipalTeacherController;
import sasj.controller.principal.PrincipalTermController;
import sasj.controller.principal.PrincipalUserController;
import sasj.controller.blob.BlobUrls;
import sasj.config.WebMvcConfig;
import sasj.controller.student.course.StudentCourseController;
import sasj.controller.student.course.StudentCourseUrls;
import sasj.controller.home.HomeUrls;
import sasj.controller.teacher.lesson.TeacherLessonUrls;
import sasj.controller.student.home.StudentHomeUrls;
import sasj.controller.student.note.StudentNoteUrls;
import sasj.controller.teacher.course.TeacherCourseUrls;
import sasj.controller.teacher.home.TeacherHomeUrls;
import sasj.controller.teacher.note.TeacherNoteUrls;
import sasj.controller.teacher.student.TeacherStudentUrls;
import sasj.data.schoolclass.SchoolClass;
import sasj.data.schoolyear.SchoolYear;
import sasj.data.student.Student;
import sasj.data.teacher.Teacher;
import sasj.data.term.Term;
import sasj.data.user.User;
import sasj.controller.user.UserUrls;
import sasj.util.Util;
import sasj.web.UrlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Extensions {
    @Autowired
    private HomeUrls homeURLs;
    @Autowired
    private UserUrls userURLs;
    @Autowired
    private BlobUrls blobURLs;
    @Autowired
    private StudentCourseUrls studentCourseURLs;
    @Autowired
    private TeacherLessonUrls teacherLessonURLs;
    @Autowired
    private TeacherHomeUrls teacherHomeUrls;
    @Autowired
    private TeacherNoteUrls teacherNoteURLs;
    @Autowired
    private TeacherStudentUrls teacherStudentUrls;
    @Autowired
    private TeacherCourseUrls teacherCourseUrls;
    @Autowired
    private TeacherLessonUrls teacherLessonUrls;
    @Autowired
    private StudentHomeUrls studentHomeUrls;
    @Autowired
    private StudentCourseUrls studentCourseUrls;
    @Autowired
    private StudentNoteUrls studentNoteUrls;
    @Autowired
    private Util util;
    private Urls urls;
    private PrincipalUrls principalUrls;
    private StudentUrls studentURLs;
    private TeacherUrls teacherUrls;

    protected Extensions() {
    }

    public String c(String label, String... args) {
        return util.capitalize(label, args);
    }

    public String l(String label, String... args) {
        return util.localize(label, args);
    }

    public Util util() {
        return util;
    }

    public Urls u() {
        return getURLs();
    }

    public Urls getURLs() {
        if (urls == null) {
            urls = new Urls();
        }
        return urls;
    }

    public PrincipalUrls pu() {
        return getPrincipalUrls();
    }

    public synchronized PrincipalUrls getPrincipalUrls() {
        if (principalUrls == null) {
            principalUrls = new PrincipalUrls();
        }
        return principalUrls;
    }

    public StudentUrls su() {
        return getStudentURLs();
    }

    public synchronized StudentUrls getStudentURLs() {
        if (studentURLs == null) {
            studentURLs = new StudentUrls();
        }
        return studentURLs;
    }

    public TeacherUrls tu() {
        return getTeacherUrls();
    }

    public synchronized TeacherUrls getTeacherUrls() {
        if (teacherUrls == null) {
            teacherUrls = new TeacherUrls();
        }
        return teacherUrls;
    }

    public class Urls {

        private Urls() {
        }

        public HomeUrls home() {
            return homeURLs;
        }

        public UserUrls users() {
            return userURLs;
        }

        public BlobUrls blobs() {
            return blobURLs;
        }
    }

    public static class PrincipalUrls {

        public String adminIndex() {
            return UrlBuilder.get(PrincipalHomeController.class, WebMvcConfig.indexName);
        }

        public String schoolYears() {
            return UrlBuilder.get(PrincipalSchoolYearController.class, WebMvcConfig.listName);
        }

        public String terms() {
            return UrlBuilder.get(PrincipalTermController.class, WebMvcConfig.listName);
        }

        public String termsBySchoolYear(SchoolYear schoolYear) {
            return new UrlBuilder(PrincipalTermController.class, WebMvcConfig.listName)
                .gatherNamedURIParams()
                .queryParam(PrincipalTermController.schoolYearQueryParam, schoolYear.getId())
                .build();
        }

        public String term(Term term) {
            return UrlBuilder.get(PrincipalTermController.class, WebMvcConfig.detailsName,
                WebMvcConfig.objectIdParamName, term.getId());
        }

        public String subjects() {
            return UrlBuilder.get(PrincipalSubjectController.class, WebMvcConfig.listName);
        }

        public String addSubject() {
            return UrlBuilder.get(PrincipalSubjectController.class, WebMvcConfig.addName);
        }

        public String users() {
            return UrlBuilder.get(PrincipalUserController.class, WebMvcConfig.listName);
        }

        public String addUser() {
            return UrlBuilder.get(PrincipalUserController.class, WebMvcConfig.addName);
        }

        public String user(User user) {
            return UrlBuilder.get(PrincipalUserController.class, WebMvcConfig.detailsName,
                WebMvcConfig.objectIdParamName, user.getId());
        }

        public String grades() {
            return UrlBuilder.get(PrincipalGradeController.class, WebMvcConfig.listName);
        }

        public String addGrade() {
            return UrlBuilder.get(PrincipalGradeController.class, WebMvcConfig.addName);
        }

        public String schoolClasses() {
            return UrlBuilder.get(PrincipalSchoolClassController.class, WebMvcConfig.listName);
        }

        public String addSchoolClass() {
            return UrlBuilder.get(PrincipalSchoolClassController.class, WebMvcConfig.addName);
        }

        public String schoolClass(SchoolClass schoolClass) {
            return UrlBuilder.get(PrincipalSchoolClassController.class, WebMvcConfig.detailsName,
                WebMvcConfig.objectIdParamName, schoolClass.getId());
        }

        public String schoolClassSchedule(SchoolClass schoolClass) {
            return UrlBuilder.get(PrincipalSchoolClassController.class, StudentCourseController.schedule,
                WebMvcConfig.objectIdParamName, schoolClass.getId());
        }

        public String teachers() {
            return UrlBuilder.get(PrincipalTeacherController.class, WebMvcConfig.listName);
        }

        public String teacher(Teacher teacher) {
            return UrlBuilder.get(PrincipalTeacherController.class, WebMvcConfig.detailsName,
                WebMvcConfig.objectIdParamName, teacher.getId());
        }

        public String selectFormMasterFragment() {
            return UrlBuilder.get(PrincipalTeacherController.class, PrincipalTeacherController.selectFormMasterFragment);
        }

        public String teacherList() {
            return UrlBuilder.get(PrincipalTeacherController.class, PrincipalTeacherController.teacherList);
        }

        public String students() {
            return UrlBuilder.get(PrincipalStudentController.class, WebMvcConfig.listName);
        }

        public String student(Student student) {
            return UrlBuilder.get(PrincipalStudentController.class, WebMvcConfig.detailsName,
                WebMvcConfig.objectIdParamName, student.getId());
        }

        public String studentList() {
            return UrlBuilder.get(PrincipalStudentController.class, PrincipalStudentController.studentList);
        }

        public String weeklySlots() {
            return UrlBuilder.get(PrincipalScheduleController.class, PrincipalScheduleController.weeklySlotsList);
        }

        public String rooms() {
            return UrlBuilder.get(PrincipalRoomController.class, WebMvcConfig.listName);
        }

        public String addRoom() {
            return UrlBuilder.get(PrincipalRoomController.class, WebMvcConfig.addName);
        }

        public String courses() {
            return UrlBuilder.get(PrincipalCourseController.class, WebMvcConfig.listName);
        }
    }

    public class StudentUrls {

        private StudentUrls() {
        }

        public StudentHomeUrls home() {
            return studentHomeUrls;
        }

        public StudentCourseUrls courses() {
            return studentCourseURLs;
        }

        public StudentNoteUrls notes() {
            return studentNoteUrls;
        }
    }

    public class TeacherUrls {

        private TeacherUrls() {
        }

        public TeacherHomeUrls home() {
            return teacherHomeUrls;
        }

        public TeacherStudentUrls students() {
            return teacherStudentUrls;
        }

        public TeacherCourseUrls courses() {
            return teacherCourseUrls;
        }

        public TeacherLessonUrls lessons() {
            return teacherLessonURLs;
        }

        public TeacherNoteUrls notes() {
            return teacherNoteURLs;
        }
    }
}
