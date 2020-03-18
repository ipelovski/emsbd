package emsbj;

import emsbj.config.WebMvcConfig;
import emsbj.controller.AuthorizedController;
import emsbj.controller.SecuredController;
import emsbj.user.User;
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
@RequestMapping("/lessons")
public class LessonController implements AuthorizedController, SecuredController {
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

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers(WebMvcConfig.localePathParam + "/lessons/**")
            .hasRole(User.Role.teacher.name().toUpperCase());
    }

    @GetMapping
    public String list(
        @RequestParam(value = LessonController.date, required = false) String date,
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
            return "lessons";
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
            model.addAttribute("breadcrumbs", detailsBreadCrumb(lesson).build());
            return "course";
        } else {
            return "";
        }
    }

    public Breadcrumb detailsBreadCrumb(Lesson lesson) {
        return new Breadcrumb(
            extensions.u().lessons().lesson(lesson),
            "Lesson in " + lesson.getCourse().getSubject().getName().getValue(),
            homeController.indexBreadcrumb()
        );
    }

    @PostMapping(value = "/start", name = start)
    public String start(Course course, WeeklySlot weeklySlot) {
        // TODO check if not started already that is a lesson already exists
        Lesson lesson = new Lesson(course, weeklySlot);
        lesson.setBegin(LocalDateTime.now());
        lessonRepository.save(lesson);
        return "redirect:" + extensions.getURLs().lessons().lesson(lesson);
    }

    @PostMapping(value = "/set-presence", name = setPresence)
    public String setPresence(Long lessonId, Long studentId, double value) {
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
            absence.setValue(value);
            absenceRepository.save(absence);
            return "redirect:" + extensions.getURLs().lessons().lesson(lesson);
        } else {
            return "";
        }
    }
}
