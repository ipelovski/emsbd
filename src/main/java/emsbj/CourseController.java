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

@Controller
@RequestMapping("/courses")
public class CourseController implements AuthorizedController, SecuredController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private MarkRepository markRepository;
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

    public static class CourseStudent {
        private Long id;
        private Integer number;
        private String name;
        private List<Mark> marks;
        private String averageMark;
        private List<String> notes;
        private List<String> absences;

        public CourseStudent(Course course, Student student) {
            this.id = student.getId();
            this.number = student.getNumber();
            this.name = student.getUser().getPersonalInfo().getName();
            this.marks = student.getMarks().stream()
                .filter(mark -> Objects.equals(mark.getSubject().getId(), course.getSubject().getId()))
                .collect(Collectors.toList());
            if (this.marks.size() > 0) {
                this.averageMark = Double.toString(Math.round(
                    this.marks.stream()
                        .map(Mark::getRawScore)
                        .reduce(0, Integer::sum)
                        / (double) this.marks.size())
                    / 100.0);
            } else {
                this.averageMark = null;
            }
            this.notes = new ArrayList<>();
            this.absences = new ArrayList<>();
        }

        public Long getId() {
            return id;
        }

        public Integer getNumber() {
            return number;
        }

        public String getName() {
            return name;
        }

        public List<Mark> getMarks() {
            return marks;
        }

        public String getAverageMark() {
            return averageMark;
        }

        public List<String> getNotes() {
            return notes;
        }

        public List<String> getAbsences() {
            return absences;
        }
    }
}
