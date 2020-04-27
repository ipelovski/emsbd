package sasj.controller.teacher.home;

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
import sasj.data.teacher.Teacher;
import sasj.data.teacher.TeacherRepository;
import sasj.data.user.User;
import sasj.data.user.UserService;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/teacher")
public class TeacherHomeController implements SecuredController, AuthorizedController {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private School school;
    @Autowired
    private HomeService homeService;

    @Override
    public void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry
            .antMatchers(WebMvcConfig.localePathParam + "/teacher/**")
            .hasRole(User.Role.teacher.name().toUpperCase());
    }

    @GetMapping
    public String index(Model model) {
        Optional<User> optionalUser = userService.getCurrentUser();
        if (optionalUser.isPresent()) {
            LocalDate currentDate = LocalDate.now();
            User user = optionalUser.get();
            Teacher teacher = teacherRepository.findByUserId(user.getId()).get();
            Iterable<Lesson> lessonsToday = lessonRepository
                .findAllByTeacherAndDay(teacher, currentDate.getDayOfWeek(), school.getTerm());
            homeService.populateHomeModel(model, lessonsToday, currentDate);
            return "teacher/home";
        } else {
            return "";
        }
    }
}
