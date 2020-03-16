package emsbj;

import emsbj.config.WebMvcConfig;
import emsbj.controller.AuthorizedController;
import emsbj.controller.SecuredController;
import emsbj.mark.Mark;
import emsbj.mark.MarkRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/courses")
public class CourseController implements AuthorizedController, SecuredController {
    public static final String schedule = "schedule";
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private MarkRepository markRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private Extensions extensions;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers(WebMvcConfig.localePathParam + "/courses/**")
            .hasRole(User.Role.teacher.name().toUpperCase());
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
            return "course";
        } else {
            return "";
        }
    }

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
            return "redirect:" + extensions.getUrls().course(course);
        } else {
            return "";
        }
    }

    @GetMapping(value = "/schedule", name = schedule)
    public String schedule(Model model) {
        Optional<Teacher> optionalTeacher = teacherService.getCurrentTeacher();
        if (optionalTeacher.isPresent()) {
            Teacher teacher = optionalTeacher.get();
            Iterable<Lesson> lessons = courseRepository.findAllByTeacher(teacher);
            model.addAttribute("lessons", lessons);
            model.addAttribute("weeklyLessons", new WeeklyLessons(
                StreamSupport.stream(lessons.spliterator(), false)
                .collect(Collectors.toList())
            ));
        }
        return "schedule";
    }
}
