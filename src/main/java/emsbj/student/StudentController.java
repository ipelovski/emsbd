package emsbj.student;

import emsbj.Breadcrumbs;
import emsbj.School;
import emsbj.config.WebMvcConfig;
import emsbj.controller.AuthorizedController;
import emsbj.controller.SecuredController;
import emsbj.course.Course;
import emsbj.course.CourseRepository;
import emsbj.course.CourseStudent;
import emsbj.user.User;
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
    public static final String course = "course";
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private School school;
    @Autowired
    private StudentURLs studentURLs;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers(WebMvcConfig.localePathParam + "/students/**")
            .hasRole(User.Role.teacher.name().toUpperCase());
    }

    @GetMapping(WebMvcConfig.objectIdPathParam)
    public String details(
        @PathVariable(WebMvcConfig.objectIdParamName) Long studentId,
        @RequestParam(value = course) Long courseId,
        Model model
    ) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            Course course = courseRepository.findById(courseId).get();
            List<CourseStudent> courseStudents = studentService.getStudentCourses(student);
            if (courseStudents.size() > 0) {
                model.addAttribute("courseStudent", courseStudents.get(0));
            }
            model.addAttribute("courseStudents", courseStudents);
            model.addAttribute(
                Breadcrumbs.modelAttributeName,
                studentURLs.studentBreadcrumb(student, course).build());
            return "student-courses";
        } else {
            return "";
        }
    }
}
