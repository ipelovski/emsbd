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
import emsbj.blob.BlobUrls;
import emsbj.config.WebMvcConfig;
import emsbj.course.CourseController;
import emsbj.course.CourseUrls;
import emsbj.home.HomeUrls;
import emsbj.lesson.LessonUrls;
import emsbj.note.NoteUrls;
import emsbj.schoolclass.SchoolClass;
import emsbj.schoolyear.SchoolYear;
import emsbj.student.Student;
import emsbj.student.StudentUrls;
import emsbj.teacher.Teacher;
import emsbj.term.Term;
import emsbj.user.User;
import emsbj.user.UserUrls;
import emsbj.util.Util;
import emsbj.web.UrlBuilder;
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
    private CourseUrls courseURLs;
    @Autowired
    private LessonUrls lessonURLs;
    @Autowired
    private NoteUrls noteURLs;
    @Autowired
    private StudentUrls studentURLs;
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

        public HomeUrls home() {
            return homeURLs;
        }

        public UserUrls users() {
            return userURLs;
        }

        public BlobUrls blobs() {
            return blobURLs;
        }

        public CourseUrls courses() {
            return courseURLs;
        }

        public LessonUrls lessons() {
            return lessonURLs;
        }

        public NoteUrls notes() {
            return noteURLs;
        }

        public StudentUrls students() {
            return studentURLs;
        }
    }

    public class AdminUrls {

        public String adminIndex() {
            return UrlBuilder.get(AdminController.class, WebMvcConfig.indexName);
        }

        public String schoolYears() {
            return UrlBuilder.get(AdminSchoolYearController.class, WebMvcConfig.listName);
        }

        public String terms() {
            return UrlBuilder.get(AdminTermController.class, WebMvcConfig.listName);
        }

        public String termsBySchoolYear(SchoolYear schoolYear) {
            return new UrlBuilder(AdminTermController.class, WebMvcConfig.listName)
                .gatherNamedURIParams()
                .queryParam(AdminTermController.schoolYearQueryParam, schoolYear.getId())
                .build();
        }

        public String term(Term term) {
            return UrlBuilder.get(AdminTermController.class, WebMvcConfig.detailsName,
                WebMvcConfig.objectIdParamName, term.getId());
        }

        public String subjects() {
            return UrlBuilder.get(AdminSubjectController.class, WebMvcConfig.listName);
        }

        public String addSubject() {
            return UrlBuilder.get(AdminSubjectController.class, WebMvcConfig.addName);
        }

        public String users() {
            return UrlBuilder.get(AdminUserController.class, WebMvcConfig.listName);
        }

        public String addUser() {
            return UrlBuilder.get(AdminUserController.class, WebMvcConfig.addName);
        }

        public String user(User user) {
            return UrlBuilder.get(AdminUserController.class, WebMvcConfig.detailsName,
                WebMvcConfig.objectIdParamName, user.getId());
        }

        public String grades() {
            return UrlBuilder.get(AdminGradeController.class, WebMvcConfig.listName);
        }

        public String addGrade() {
            return UrlBuilder.get(AdminGradeController.class, WebMvcConfig.addName);
        }

        public String schoolClasses() {
            return UrlBuilder.get(AdminSchoolClassController.class, WebMvcConfig.listName);
        }

        public String addSchoolClass() {
            return UrlBuilder.get(AdminSchoolClassController.class, WebMvcConfig.addName);
        }

        public String schoolClass(SchoolClass schoolClass) {
            return UrlBuilder.get(AdminSchoolClassController.class, WebMvcConfig.detailsName,
                WebMvcConfig.objectIdParamName, schoolClass.getId());
        }

        public String schoolClassSchedule(SchoolClass schoolClass) {
            return UrlBuilder.get(AdminSchoolClassController.class, CourseController.schedule,
                WebMvcConfig.objectIdParamName, schoolClass.getId());
        }

        public String teachers() {
            return UrlBuilder.get(AdminTeacherController.class, WebMvcConfig.listName);
        }

        public String teacher(Teacher teacher) {
            return UrlBuilder.get(AdminTeacherController.class, WebMvcConfig.detailsName,
                WebMvcConfig.objectIdParamName, teacher.getId());
        }

        public String selectFormMasterFragment() {
            return UrlBuilder.get(AdminTeacherController.class, AdminTeacherController.selectFormMasterFragment);
        }

        public String teacherList() {
            return UrlBuilder.get(AdminTeacherController.class, AdminTeacherController.teacherList);
        }

        public String students() {
            return UrlBuilder.get(AdminStudentController.class, WebMvcConfig.listName);
        }

        public String student(Student student) {
            return UrlBuilder.get(AdminStudentController.class, WebMvcConfig.detailsName,
                WebMvcConfig.objectIdParamName, student.getId());
        }

        public String studentList() {
            return UrlBuilder.get(AdminStudentController.class, AdminStudentController.studentList);
        }

        public String weeklySlots() {
            return UrlBuilder.get(AdminScheduleController.class, AdminScheduleController.weeklySlotsList);
        }

        public String rooms() {
            return UrlBuilder.get(AdminRoomController.class, WebMvcConfig.listName);
        }

        public String addRoom() {
            return UrlBuilder.get(AdminRoomController.class, WebMvcConfig.addName);
        }

        public String courses() {
            return UrlBuilder.get(AdminCourseController.class, WebMvcConfig.listName);
        }
    }
}
