package emsbj;

import emsbj.admin.AdminController;
import emsbj.admin.AdminCourseController;
import emsbj.admin.AdminGradeController;
import emsbj.admin.AdminRoomController;
import emsbj.admin.AdminScheduleController;
import emsbj.admin.AdminSchoolClassController;
import emsbj.admin.AdminSchoolYearController;
import emsbj.admin.AdminStudentController;
import emsbj.admin.AdminSubjectController;
import emsbj.admin.AdminTeacherController;
import emsbj.admin.AdminTermController;
import emsbj.admin.AdminUserController;
import emsbj.blob.BlobURLs;
import emsbj.config.WebMvcConfig;
import emsbj.course.CourseController;
import emsbj.course.CourseURLs;
import emsbj.home.HomeURLs;
import emsbj.lesson.LessonURLs;
import emsbj.note.NoteURLs;
import emsbj.schoolclass.SchoolClass;
import emsbj.schoolyear.SchoolYear;
import emsbj.student.Student;
import emsbj.student.StudentURLs;
import emsbj.teacher.Teacher;
import emsbj.term.Term;
import emsbj.user.User;
import emsbj.user.UserURLs;
import emsbj.web.URLBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Extensions {
    @Autowired
    private HomeURLs homeURLs;
    @Autowired
    private UserURLs userURLs;
    @Autowired
    private BlobURLs blobURLs;
    @Autowired
    private CourseURLs courseURLs;
    @Autowired
    private LessonURLs lessonURLs;
    @Autowired
    private NoteURLs noteURLs;
    @Autowired
    private StudentURLs studentURLs;
    @Autowired
    private Util util;
    private Urls urls;
    private AdminUrls adminUrls;

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
        if(urls == null) {
            urls = new Urls();
        }
        return urls;
    }

    public AdminUrls au() {
        return getAdminUrls();
    }

    public AdminUrls getAdminUrls() {
        if(adminUrls == null) {
            adminUrls = new AdminUrls();
        }
        return adminUrls;
    }

    public class Urls {

        private Urls() {
        }

        public HomeURLs home() {
            return homeURLs;
        }

        public UserURLs users() {
            return userURLs;
        }

        public BlobURLs blobs() {
            return blobURLs;
        }

        public CourseURLs courses() {
            return courseURLs;
        }

        public LessonURLs lessons() {
            return lessonURLs;
        }

        public NoteURLs notes() {
            return noteURLs;
        }

        public StudentURLs students() {
            return studentURLs;
        }
    }

    public class AdminUrls {

        public String adminIndex() {
            return URLBuilder.get(AdminController.class, WebMvcConfig.indexName);
        }

        public String schoolYears() {
            return URLBuilder.get(AdminSchoolYearController.class, WebMvcConfig.listName);
        }

        public String addSchoolYear() {
            return URLBuilder.get(AdminSchoolYearController.class, WebMvcConfig.addName);
        }

        public String terms() {
            return URLBuilder.get(AdminTermController.class, WebMvcConfig.listName);
        }

        public String termsBySchoolYear(SchoolYear schoolYear) {
            return new URLBuilder(AdminTermController.class, WebMvcConfig.listName)
                .gatherNamedURIParams()
                .queryParam(AdminTermController.schoolYearQueryParam, schoolYear.getId())
                .build();
        }

        public String term(Term term) {
            return URLBuilder.get(AdminTermController.class, WebMvcConfig.detailsName, term.getId());
        }

        public String addTermWithSchoolYear(SchoolYear schoolYear) {
            return URLBuilder.get(AdminTermController.class, WebMvcConfig.addName, schoolYear.getId());
        }

        public String subjects() {
            return URLBuilder.get(AdminSubjectController.class, WebMvcConfig.listName);
        }

        public String addSubject() {
            return URLBuilder.get(AdminSubjectController.class, WebMvcConfig.addName);
        }

        public String users() {
            return URLBuilder.get(AdminUserController.class, WebMvcConfig.listName);
        }

        public String addUser() {
            return URLBuilder.get(AdminUserController.class, WebMvcConfig.addName);
        }

        public String user(User user) {
            return URLBuilder.get(AdminUserController.class, WebMvcConfig.detailsName, user.getId());
        }

        public String grades() {
            return URLBuilder.get(AdminGradeController.class, WebMvcConfig.listName);
        }

        public String addGrade() {
            return URLBuilder.get(AdminGradeController.class, WebMvcConfig.addName);
        }

        public String schoolClasses() {
            return URLBuilder.get(AdminSchoolClassController.class, WebMvcConfig.listName);
        }

        public String addSchoolClass() {
            return URLBuilder.get(AdminSchoolClassController.class, WebMvcConfig.addName);
        }

        public String schoolClass(SchoolClass schoolClass) {
            return URLBuilder.get(AdminSchoolClassController.class, WebMvcConfig.detailsName, schoolClass.getId());
        }

        public String schoolClassSchedule(SchoolClass schoolClass) {
            return URLBuilder.get(AdminSchoolClassController.class, CourseController.schedule, schoolClass.getId());
        }

        public String teachers() {
            return URLBuilder.get(AdminTeacherController.class, WebMvcConfig.listName);
        }

        public String teacher(Teacher teacher) {
            return URLBuilder.get(AdminTeacherController.class, WebMvcConfig.detailsName, teacher.getId());
        }

        public String selectFormMasterFragment() {
            return URLBuilder.get(AdminTeacherController.class, AdminTeacherController.selectFormMasterFragment);
        }

        public String teacherList() {
            return URLBuilder.get(AdminTeacherController.class, AdminTeacherController.teacherList);
        }

        public String students() {
            return URLBuilder.get(AdminStudentController.class, WebMvcConfig.listName);
        }

        public String student(Student student) {
            return URLBuilder.get(AdminStudentController.class, WebMvcConfig.detailsName, student.getId());
        }

        public String studentList() {
            return URLBuilder.get(AdminStudentController.class, AdminStudentController.studentList);
        }

        public String weeklySlots() {
            return URLBuilder.get(AdminScheduleController.class, AdminScheduleController.weeklySlotsList);
        }

        public String rooms() {
            return URLBuilder.get(AdminRoomController.class, WebMvcConfig.listName);
        }

        public String addRoom() {
            return URLBuilder.get(AdminRoomController.class, WebMvcConfig.addName);
        }

        public String courses() {
            return URLBuilder.get(AdminCourseController.class, WebMvcConfig.listName);
        }
    }
}
