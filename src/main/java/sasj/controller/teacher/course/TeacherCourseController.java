package sasj.controller.teacher.course;

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
import sasj.config.WebMvcConfig;
import sasj.controller.AuthorizedController;
import sasj.controller.Breadcrumbs;
import sasj.controller.SecuredController;
import sasj.controller.WeeklyLessons;
import sasj.data.School;
import sasj.data.course.Course;
import sasj.data.course.CourseRepository;
import sasj.data.lesson.Lesson;
import sasj.data.lesson.LessonRepository;
import sasj.data.mark.Mark;
import sasj.data.mark.MarkRepository;
import sasj.data.student.Student;
import sasj.data.student.StudentRepository;
import sasj.data.student.StudentService;
import sasj.data.teacher.Teacher;
import sasj.data.teacher.TeacherRepository;
import sasj.data.teacher.TeacherService;
import sasj.data.user.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/teacher/courses")
public class TeacherCourseController implements AuthorizedController, SecuredController {
    public static final String schedule = "schedule";
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private MarkRepository markRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherCourseUrls teacherCourseURLs;
    @Autowired
    private School school;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers(WebMvcConfig.localePathParam + "/student/courses", WebMvcConfig.localePathParam + "/courses/schedule")
            .hasRole(User.Role.student.name().toUpperCase());
    }

    @GetMapping(WebMvcConfig.objectIdPathParam)
    public String details(
        @PathVariable(WebMvcConfig.objectIdParamName) Long courseId,
        Model model
    ) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            List<Student> students = course.getSchoolClass().getStudents();
            List<CourseStudent> courseStudents = students.stream()
                .map(student -> new CourseStudent(course, student))
                .collect(Collectors.toList());
            model.addAttribute("course", course);
            model.addAttribute("courseStudents", courseStudents);
            model.addAttribute(Breadcrumbs.modelAttributeName, teacherCourseURLs.courseBreadcrumb(course).build());
            return "teacher/course";
        } else {
            return "";
        }
    }

    // TODO add lesson to mark
    @PostMapping(WebMvcConfig.objectIdPathParam)
    public String addMark(
        @PathVariable(WebMvcConfig.objectIdParamName) Long courseId,
        @RequestParam("markScore") String markScore,
        @RequestParam("studentId") Long studentId
    ) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        if (optionalCourse.isPresent() && optionalStudent.isPresent() && markScore.length() > 0) {
            Course course = optionalCourse.get();
            Student student = optionalStudent.get();
            double score = Double.parseDouble(markScore);
            int rawScore = (int) Math.round(score * 100);
            Mark mark = new Mark(student, course.getSubject(), rawScore);
            markRepository.save(mark);
            return "redirect:" + teacherCourseURLs.course(course);
        } else {
            return "";
        }
    }

    @GetMapping(value = "/schedule", name = schedule)
    public String schedule(Model model) {
        Teacher teacher = teacherService.getCurrentTeacher().get();
        Iterable<Lesson> lessons = lessonRepository.findAllByTeacher(teacher, school.getTerm());
        model.addAttribute("lessons", lessons);
        model.addAttribute("weeklyLessons", new WeeklyLessons(
            StreamSupport.stream(lessons.spliterator(), false)
            .collect(Collectors.toList())
        ));
        model.addAttribute(Breadcrumbs.modelAttributeName, teacherCourseURLs.scheduleBreadcrumb().build());
        return "teacher/schedule";
    }
}
