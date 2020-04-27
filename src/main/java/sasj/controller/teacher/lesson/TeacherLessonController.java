package sasj.controller.teacher.lesson;

import sasj.controller.Breadcrumbs;
import sasj.data.absence.Absence;
import sasj.data.absence.AbsenceRepository;
import sasj.controller.Extensions;
import sasj.controller.home.HomeController;
import sasj.data.lesson.Lesson;
import sasj.data.lesson.LessonRepository;
import sasj.data.student.Student;
import sasj.data.student.StudentRepository;
import sasj.data.teacher.Teacher;
import sasj.data.teacher.TeacherService;
import sasj.controller.WeeklyLessons;
import sasj.data.weeklyslot.WeeklySlot;
import sasj.data.weeklyslot.WeeklySlotRepository;
import sasj.config.WebMvcConfig;
import sasj.controller.AuthorizedController;
import sasj.controller.SecuredController;
import sasj.data.course.Course;
import sasj.data.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/teacher/lessons")
public class TeacherLessonController implements AuthorizedController, SecuredController {
    public static final String start = "start";
    public static final String date = "date";
    public static final String setPresence = "setPresence";
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private WeeklySlotRepository weeklySlotRepository;
    @Autowired
    private AbsenceRepository absenceRepository;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private Extensions extensions;
    @Autowired
    private HomeController homeController;
    @Autowired
    private TeacherLessonUrls teacherLessonURLs;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers(WebMvcConfig.localePathParam + "/lessons/**")
            .hasRole(User.Role.teacher.name().toUpperCase());
    }

    @GetMapping
    public String list(
        @RequestParam(value = TeacherLessonController.date, required = false) String date,
        Model model
    ) {
        LocalDate startDate;
        if (date != null) {
            startDate = LocalDate.parse(date);
        } else {
            LocalDate now = LocalDate.now();
            startDate = now.minusDays(now.getDayOfWeek().ordinal());
        }
        model.addAttribute("previousWeek", startDate.minusWeeks(1));
        model.addAttribute("nextWeek", startDate.plusWeeks(1));

        Optional<Teacher> optionalTeacher = teacherService.getCurrentTeacher();
        if (optionalTeacher.isPresent()) {
            Iterable<WeeklySlot> weeklySlots = weeklySlotRepository.findAll();
            Iterable<Lesson> teacherLessons = lessonRepository
                .findAllByCourseTeacherAndBeginIsBetween(optionalTeacher.get(),
                startDate.atStartOfDay(), startDate.plusWeeks(1).atStartOfDay());
            List<Lesson> lessons = StreamSupport
                .stream(weeklySlots.spliterator(), false)
                .sorted((o1, o2) -> {
                    if (o1.getDay() != o2.getDay()) {
                        return o1.getDay().getValue() - o2.getDay().getValue();
                    } else {
                        return o1.getBegin().toSecondOfDay() - o2.getBegin().toSecondOfDay();
                    }
                })
                .map(weeklySlot -> {
                    Optional<Lesson> optionalLesson = StreamSupport
                        .stream(teacherLessons.spliterator(), false)
                        .filter(lesson -> lesson.getWeeklySlot().getId().equals(weeklySlot.getId()))
                        .findAny();
                    if (optionalLesson.isPresent()) {
                        return optionalLesson.get();
                    } else {
                        Lesson emptyLesson = new Lesson();
                        emptyLesson.setWeeklySlot(weeklySlot);
                        return emptyLesson;
                    }
                })
                .collect(Collectors.toList());
            model.addAttribute("lessons", lessons);
            model.addAttribute("weeklyLessons", new WeeklyLessons(lessons));
            Breadcrumbs breadcrumbs = teacherLessonURLs.lessonsPerWeekBreadcrumb(startDate).build();
            model.addAttribute(Breadcrumbs.modelAttributeName, breadcrumbs);
            return "teacher/lessons";
        } else {
            return "";
        }
    }

    @GetMapping(WebMvcConfig.objectIdPathParam)
    public String details(
        @PathVariable(WebMvcConfig.objectIdParamName) Long lessonId,
        Model model
    ) {
        Optional<Lesson> optionalLesson = lessonRepository.findById(lessonId);
        if (optionalLesson.isPresent()) {
            Lesson lesson = optionalLesson.get();
            Course course = lesson.getCourse();
            List<Student> students = course.getSchoolClass().getStudents();
            List<LessonStudent> lessonStudents = students.stream()
                .map(student -> new LessonStudent(lesson, student))
                .collect(Collectors.toList());
            model.addAttribute("lesson", lesson);
            model.addAttribute("course", course);
            model.addAttribute("courseStudents", lessonStudents);
            model.addAttribute(Breadcrumbs.modelAttributeName, teacherLessonURLs.detailsBreadcrumb(lesson).build());
            return "teacher/course";
        } else {
            return "";
        }
    }

    @PostMapping(value = "/start", name = start)
    public String start(Course course, WeeklySlot weeklySlot) {
        // TODO check if not started already that is a lesson already exists
        Lesson lesson = new Lesson(course, weeklySlot);
        lesson.setBegin(LocalDateTime.now());
        lessonRepository.save(lesson);
        return "redirect:" + extensions.getTeacherUrls().lessons().lesson(lesson);
    }

    @PostMapping(value = "/set-presence", name = setPresence)
    public String setPresence(Long lessonId, Long studentId, Absence.Type type) {
        Optional<Lesson> optionalLesson = lessonRepository.findById(lessonId);
        if (optionalLesson.isPresent()) {
            Lesson lesson = optionalLesson.get();
            Student student = studentRepository.findById(studentId).get();
            Optional<Absence> optionalAbsence = absenceRepository
                .findByStudentAndLesson(student, lesson);
            Absence absence;
            if (optionalAbsence.isPresent()) {
                absence = optionalAbsence.get();
            } else {
                absence = new Absence();
                absence.setLesson(lesson);
                absence.setStudent(student);
            }
            if (type != null) {
                absence.setType(type);
                absenceRepository.save(absence);
            } else if (!absence.isNew()) {
                absenceRepository.delete(absence);
            }
            return "redirect:" + extensions.getTeacherUrls().lessons().lesson(lesson);
        } else {
            return "";
        }
    }
}
