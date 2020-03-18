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
import emsbj.config.WebMvcConfig;
import emsbj.user.User;
import emsbj.user.UserController;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Service
public class Extensions {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private HomeURLs homeURLs;
    private Urls urls;
    private AdminUrls adminUrls;

    protected Extensions() {
    }

    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    public Collection<Locale> getSupportedLocales() {
        return WebMvcConfig.supportedLocales.stream()
            .map(Locale::forLanguageTag)
            .collect(Collectors.toList());
    }

    public String c(String label, String... args) {
        return capitalize(label, args);
    }

    public String capitalize(String label, String... args) {
        return StringUtils.capitalize(localize(label, args));
    }

    public String l(String label, String... args) {
        return localize(label, args);
    }

    public String localize(String label, String... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(label, args, locale);
    }

    public String lu(String url) {
        return localizedUrl(url);
    }

    public String localizedUrl(String url) {
        Locale locale = LocaleContextHolder.getLocale();
        return localizedUrl(url, locale);
    }

    public String localizedUrl(String url, Locale locale) {
        if (Strings.isBlank(url)) {
            throw new IllegalArgumentException("url should not be blank");
        }
        if (url.charAt(0) != '/') {
            throw new IllegalArgumentException("non absolute urls are not supported");
        }
        return "/" + locale.toLanguageTag() + url;
    }

    public String localizeCurrentRequestURL(Locale locale) {
        HttpServletRequest currentRequest = getCurrentRequest();
        StringBuffer currentRequestURL = currentRequest.getRequestURL();
        if (currentRequest.getQueryString() != null) {
            currentRequestURL
                .append('?')
                .append(currentRequest.getQueryString());
        }
        Matcher matcher = WebMvcConfig.localePathPattern.matcher(currentRequestURL);
        if (matcher.find()) {
            return matcher.replaceFirst("/" + locale.toLanguageTag());
        } else {
            return currentRequestURL.toString();
        }
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

    // from ServletUriComponentsBuilder.getCurrentRequest
    private HttpServletRequest getCurrentRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        Assert.state(attrs instanceof ServletRequestAttributes, "No current ServletRequestAttributes");
        return ((ServletRequestAttributes)attrs).getRequest();
    }

    public class Urls {

        private Urls() {
        }

        public HomeURLs home() {
            return homeURLs;
        }

        public String blob(Blob blob) {
            return URLBuilder.get(BlobController.class, WebMvcConfig.detailsName, blob.getId());
        }

        public String profilePicture(User user) {
            if (user.getPersonalInfo().getPicture() != null) {
                return blob(user.getPersonalInfo().getPicture());
            } else {
                return WebMvcConfig.noProfilePicture;
            }
        }

        public String uploadProfilePicture(User user) {
            return URLBuilder.get(BlobController.class, BlobController.uploadProfilePicture, user.getId());
        }

        public String signIn() {
            return URLBuilder.get(UserController.class, UserController.signIn);
        }

        public String signIn(Locale locale) {
            return localizedUrl("/sign-in", locale);
        }

        public String signInRole() {
            return URLBuilder.get(UserController.class, UserController.signInRole);
        }

        public String signUp() {
            return URLBuilder.get(UserController.class, UserController.signUp);
        }

        public String signOut() {
            return localizedUrl("/sign-out");
        }

        public String profile() {
            return URLBuilder.get(UserController.class, UserController.profile);
        }

        public String course(Course course) {
            return URLBuilder.get(CourseController.class, WebMvcConfig.detailsName, course.getId());
        }

        public String addNote() {
            return URLBuilder.get(NoteController.class, WebMvcConfig.addName);
        }

        public String schedule() {
            return URLBuilder.get(CourseController.class, CourseController.schedule);
        }

        public String lessons() {
            return URLBuilder.get(LessonController.class, WebMvcConfig.listName);
        }

        public String lesson(Lesson lesson) {
            return URLBuilder.get(LessonController.class, WebMvcConfig.detailsName, lesson.getId());
        }

        public String startLesson() {
            return URLBuilder.get(LessonController.class, LessonController.start);
        }

        public String lessonsPerWeek(LocalDate date) {
            return new URLBuilder(LessonController.class, WebMvcConfig.listName)
                .queryParam(LessonController.date, date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .build();
        }

        public String setPresence() {
            return URLBuilder.get(LessonController.class, LessonController.setPresence);
        }

        public String notes(CourseStudent student, Course course) {
            return new URLBuilder(NoteController.class, WebMvcConfig.listName)
                .queryParam(NoteController.studentQueryParam, student.getId())
                .queryParam(NoteController.courseQueryParam, course.getId())
                .build();
        }

        public String notes(Student student, Course course, Lesson lesson) {
            return new URLBuilder(NoteController.class, WebMvcConfig.listName)
                .queryParam(NoteController.studentQueryParam, student.getId())
                .queryParam(NoteController.courseQueryParam, course.getId())
                .queryParam(NoteController.lessonQueryParam, lesson,
                    Lesson::getId, Objects::nonNull)
                .build();
        }

        public String addNote(CourseStudent student, Course course, Lesson lesson) {
            return new URLBuilder(NoteController.class, WebMvcConfig.addName)
                .queryParam(NoteController.studentQueryParam, student.getId())
                .queryParam(NoteController.courseQueryParam, course.getId())
                .queryParam(NoteController.lessonQueryParam, lesson,
                    Lesson::getId, Objects::nonNull)
                .build();
        }

        public String addNote(Student student, Course course, Lesson lesson) {
            return new URLBuilder(NoteController.class, WebMvcConfig.addName)
                .queryParam(NoteController.studentQueryParam, student.getId())
                .queryParam(NoteController.courseQueryParam, course.getId())
                .queryParam(NoteController.lessonQueryParam, lesson,
                    Lesson::getId, Objects::nonNull)
                .build();
        }

        public String editNote(Note note) {
            return URLBuilder.get(NoteController.class, WebMvcConfig.editName, note.getId());
        }

        public String removeNote(Note note) {
            return URLBuilder.get(NoteController.class, WebMvcConfig.removeName, note.getId());
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
