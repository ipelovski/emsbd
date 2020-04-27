package sasj.controller.student.course;

import sasj.controller.Breadcrumbs;
import sasj.data.School;
import sasj.data.lesson.Lesson;
import sasj.data.lesson.LessonRepository;
import sasj.data.student.Student;
import sasj.data.student.StudentService;
import sasj.controller.WeeklyLessons;
import sasj.config.WebMvcConfig;
import sasj.controller.AuthorizedController;
import sasj.controller.SecuredController;
import sasj.data.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/student/courses")
public class StudentCourseController implements AuthorizedController, SecuredController {
    public static final String schedule = "schedule";
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentCourseUrls studentCourseURLs;
    @Autowired
    private School school;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers(WebMvcConfig.localePathParam + "/student/courses", WebMvcConfig.localePathParam + "/courses/schedule")
            .hasRole(User.Role.student.name().toUpperCase());
    }

    @GetMapping
    public String list(Model model) {
        Optional<Student> optionalStudent = studentService.getCurrentStudent();
        if (optionalStudent.isPresent()) {
            model.addAttribute(
                "courseStudents",
                studentService.getStudentCourses(optionalStudent.get()));
            model.addAttribute(
                Breadcrumbs.modelAttributeName,
                studentCourseURLs.listBreadcrumb().build());
            return "student/courses";
        } else {
            return "";
        }
    }

    @GetMapping(value = "/schedule", name = schedule)
    public String schedule(Model model) {
        Student student = studentService.getCurrentStudent().get();
        Iterable<Lesson> lessons = lessonRepository.findAllBySchoolClass(
            student.getSchoolClass(), school.getTerm());
        model.addAttribute("lessons", lessons);
        model.addAttribute("weeklyLessons", new WeeklyLessons(
            StreamSupport.stream(lessons.spliterator(), false)
                .collect(Collectors.toList())));
        model.addAttribute(Breadcrumbs.modelAttributeName, studentCourseURLs.scheduleBreadcrumb().build());
        return "student/schedule";
    }
}
