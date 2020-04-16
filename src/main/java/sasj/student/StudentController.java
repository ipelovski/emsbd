package sasj.student;

import sasj.Breadcrumbs;
import sasj.School;
import sasj.config.WebMvcConfig;
import sasj.controller.AuthorizedController;
import sasj.controller.SecuredController;
import sasj.course.Course;
import sasj.course.CourseRepository;
import sasj.course.CourseStudent;
import sasj.teacher.Teacher;
import sasj.teacher.TeacherService;
import sasj.user.User;
import sasj.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/students")
public class StudentController implements AuthorizedController, SecuredController {
    public static final String teacherStudents = "teacherStudents";
    public static final String teacherCoursesStudents = "teacherAllStudents";
    public static final String searchStudent = "searchStudent";
    public static final String course = "course";
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private UserService userService;
    @Autowired
    private School school;
    @Autowired
    private StudentUrls studentURLs;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers(WebMvcConfig.localePathParam + "/students/**")
            .hasRole(User.Role.teacher.name().toUpperCase());
    }

    @GetMapping(name = teacherStudents)
    public String teacherStudents() {
        return "teacher-students";
    }

    @GetMapping(value = "/course", name = teacherCoursesStudents)
    public String teacherCoursesStudents(Model model) {
        Optional<Teacher> optionalTeacher = teacherService.getCurrentTeacher();
        if (optionalTeacher.isPresent()) {
            List<Student> students = studentRepository
                .findBySchoolClassCoursesTeacher(optionalTeacher.get());
            userService.sortByPersonalName(students);
            model.addAttribute("students", students);
            return "teacher-courses-students";
        } else {
            return "";
        }
    }

    @GetMapping(WebMvcConfig.objectIdPathParam)
    public String details(
        @PathVariable(WebMvcConfig.objectIdParamName) Long studentId,
        @RequestParam(value = course, required = false) Long courseId,
        Model model
    ) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            if (courseId != null) {
                Course course = courseRepository.findById(courseId).get();
                List<CourseStudent> courseStudents = studentService.getStudentCourses(student);
                if (courseStudents.size() > 0) {
                    model.addAttribute("courseStudent", courseStudents.get(0));
                }
                model.addAttribute("courseStudents", courseStudents);
                model.addAttribute(
                    Breadcrumbs.modelAttributeName,
                    studentURLs.studentBreadcrumb(student, course).build());
            } else {
                model.addAttribute(
                    "courseStudents",
                    studentService.getStudentCourses(student));
            }
            return "student-courses";
        } else {
            return "";
        }
    }

    @GetMapping(value = "search", name = searchStudent)
    public String searchStudent(
        @RequestParam(value = "name", required = false) String name,
        Model model
    ) {
        if (name != null && name.length() > 0) {
            List<Student> students = studentRepository.findByName(name);
            userService.sortByPersonalName(students);
            model.addAttribute("students", students);
            return "teacher-courses-students";
        } else {
            return "search-student";
        }
    }
}
