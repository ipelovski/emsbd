package sasj.controller.student.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sasj.config.WebMvcConfig;
import sasj.controller.AuthorizedController;
import sasj.controller.SecuredController;
import sasj.controller.home.HomeService;
import sasj.data.School;
import sasj.data.lesson.Lesson;
import sasj.data.lesson.LessonRepository;
import sasj.data.student.Student;
import sasj.data.student.StudentRepository;
import sasj.data.user.User;
import sasj.data.user.UserService;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentHomeController implements SecuredController, AuthorizedController {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private School school;
    @Autowired
    private HomeService homeService;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers( WebMvcConfig.localePathParam, WebMvcConfig.localePathParam + "/",
                WebMvcConfig.localePathParam + "/home/**")
            .permitAll();
    }

    @GetMapping
    public String index(Model model) {
        Optional<User> optionalUser = userService.getCurrentUser();
        if (optionalUser.isPresent()) {
            LocalDate currentDate = LocalDate.now();
            User user = optionalUser.get();
            Student student = studentRepository.findByUserId(user.getId()).get();
            Iterable<Lesson> lessonsToday = lessonRepository
                .findAllBySchoolClassAndDay(student.getSchoolClass(), currentDate.getDayOfWeek(), school.getTerm());
            homeService.populateHomeModel(model, lessonsToday, currentDate);
            return "student/home";
        } else {
            return "";
        }
    }
}
