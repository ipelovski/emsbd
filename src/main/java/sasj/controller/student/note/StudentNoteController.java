package sasj.controller.student.note;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sasj.config.WebMvcConfig;
import sasj.controller.AuthorizedController;
import sasj.controller.Breadcrumbs;
import sasj.controller.Extensions;
import sasj.controller.SecuredController;
import sasj.data.course.Course;
import sasj.data.course.CourseRepository;
import sasj.data.note.Note;
import sasj.data.note.NoteRepository;
import sasj.data.student.Student;
import sasj.data.student.StudentRepository;
import sasj.data.user.User;
import sasj.data.user.UserService;

@Controller
@RequestMapping("/student/notes")
public class StudentNoteController implements SecuredController, AuthorizedController {
    public static final String studentQueryParam = "student";
    public static final String courseQueryParam = "course";
    public static final String lessonQueryParam = "lesson";
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private Extensions extensions;
    @Autowired
    private StudentNoteUrls studentNoteURLs;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers(WebMvcConfig.localePathParam + "/notes", WebMvcConfig.localePathParam + "/notes/")
            .hasRole(User.Role.student.name().toUpperCase());
    }

    @GetMapping
    public String list(
        @RequestParam(value = courseQueryParam) Long courseId,
        Model model
    ) {
        Course course = courseRepository.findById(courseId).get();
        User user = userService.getCurrentUser().get();
        if (user.getRole() == User.Role.student) {
            Student student = studentRepository.findByUserId(user.getId()).get();
            Iterable<Note> notes = noteRepository.findByStudentAndCourse(student, course);
            model.addAttribute("student", student);
            model.addAttribute("course", course);
            model.addAttribute("notes", notes);
            model.addAttribute(Breadcrumbs.modelAttributeName, studentNoteURLs.listBreadcrumb(course).build());
            return "student/notes";
        } else {
            return "";
        }
    }
}
